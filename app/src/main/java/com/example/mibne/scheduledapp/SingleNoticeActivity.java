package com.example.mibne.scheduledapp;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class SingleNoticeActivity extends AppCompatActivity {
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_notice);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView titleTextView = (TextView) findViewById(R.id.notice_title);
        TextView dateTextView = (TextView) findViewById(R.id.notice_date);
        TextView descriptionTextView = (TextView) findViewById(R.id.notice_description);
        TextView ownerTextView = (TextView) findViewById(R.id.notice_owner);
        TextView deadlineTextView = (TextView) findViewById(R.id.notice_deadline);

        Button button = (Button) findViewById(R.id.export_notice_button);

        //Get the bundle
        bundle = getIntent().getExtras();

        //Extract the data… and set value to the textView
        titleTextView.setText(bundle.getString("Title"));
        dateTextView.setText(formatDate(Long.valueOf(bundle.getString("Date"))));
        descriptionTextView.setText(bundle.getString("Description"));
        ownerTextView.setText(bundle.getString("Owner"));
        deadlineTextView.setText(formatDate(Long.valueOf(bundle.getString("Deadline"))));

        button.setText("Export this " + bundle.getString("Type"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Return the formatted date string (i.e. "Mar 3") from a Date object.
     */
    private String formatDate(Long dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd");
        return dateFormat.format(dateObject);
    }

    public void openDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ChooseExportOptionDialogueFragment();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "ExportDialogFragment");

    }
}
