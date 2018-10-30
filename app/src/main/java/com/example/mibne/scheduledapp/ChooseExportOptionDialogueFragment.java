package com.example.mibne.scheduledapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;


public class ChooseExportOptionDialogueFragment extends DialogFragment {

    Bundle bundle;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        bundle = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_export_option)
                .setItems(R.array.export_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0 :
                                exportAsPdf();
                                break;
                            case 1 :
                                exportToCalendar();
                                break;
                                default:
                                    break;
                        }
                    }
                });
        return builder.create();
    }

    public void exportToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        Long.parseLong(bundle.getString("Deadline")) + 8*60*60*1000) //added 8 hours from 0.00
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        Long.parseLong(bundle.getString("Deadline")) + 17*60*60*1000) //added 17 hours from 0.00
                .putExtra(CalendarContract.Events.TITLE, bundle.getString("Title"))
                .putExtra(CalendarContract.Events.DESCRIPTION, bundle.getString("Description"))
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, R.string.app_email);

        startActivity(intent);
    }

    public void exportAsPdf() {
        Toast.makeText(getContext(), "Feature is comming soon!", Toast.LENGTH_SHORT).show();
    }
}
