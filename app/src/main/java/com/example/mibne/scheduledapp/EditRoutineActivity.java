package com.example.mibne.scheduledapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.example.mibne.scheduledapp.MainActivity.userDataBundle;


public class EditRoutineActivity extends AppCompatActivity {

    private static Activity activity;

    private String mUserOrganization;
    private String mUserDepartment;

    private TextInputLayout roomNoTextInputLayout;
    private TextInputLayout remarksTextInputLayout;
    private TextView startTimeTextView;
    private TextView endTimeTextView;

    private static String routineId;
    private String day;
    private String previousDay;
    private String courseCode;
    private String startTime;
    private String endTime;
    private String roomNo;
    private String remarks;

    private static DatabaseReference mRoutineDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_routine);

        activity = this;

        Bundle bundle = getIntent().getExtras();
        courseCode = bundle.getString("courseCode");
        getSupportActionBar().setTitle("Edit " + courseCode);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserOrganization = userDataBundle.getString("organization");
        mUserDepartment = userDataBundle.getString("department");

        mRoutineDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mUserOrganization).child(mUserDepartment).child("routines");

        routineId = bundle.getString("day") + "-" + bundle.getString("courseCode");
        day = bundle.getString("day");
        previousDay = bundle.getString("day");
        startTime = bundle.getString("startTime");
        endTime = bundle.getString("endTime");
        roomNo = bundle.getString("roomNo");
        remarks = "";

        roomNoTextInputLayout = (TextInputLayout) findViewById(R.id.edit_room_no_wrapper);
        remarksTextInputLayout = (TextInputLayout) findViewById(R.id.edit_remarks_wrapper);
        startTimeTextView = (TextView) findViewById(R.id.text_view_start_time);
        endTimeTextView = (TextView) findViewById(R.id.text_view_end_time);

        roomNoTextInputLayout.getEditText().setText(roomNo);
        remarksTextInputLayout.getEditText().setText(remarks);
        startTimeTextView.setText(startTime);
        endTimeTextView.setText(endTime);

        final Spinner spinner = (Spinner) findViewById(R.id.week_day_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_week_day, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setSelection(getDayPosition(day));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                day = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(null,"Please Select day", Toast.LENGTH_SHORT).show();
            }
        });

        Button button = (Button) findViewById(R.id.button_update_class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInput();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_routine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete:
                openDeleteRoutineConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateRemarks() {
        String courseCodeInput = remarksTextInputLayout.getEditText().getText().toString().trim();

        if (courseCodeInput.length() > 20) {
            remarksTextInputLayout.setError("Too long");
            return false;
        } else {
            remarksTextInputLayout.setError(null);
            return true;
        }
    }

    private boolean validateRoomNo() {
        String roomNoInput = roomNoTextInputLayout.getEditText().getText().toString().trim();

        if (roomNoInput.isEmpty()) {
            roomNoTextInputLayout.setError("Please set room number.");
            return false;
        } else if (roomNoInput.length() > 40) {
            roomNoTextInputLayout.setError("Too long");
            return false;
        } else if (roomNoInput.length() < 3) {
            roomNoTextInputLayout.setError("Too short");
            return false;
        } else {
            roomNoTextInputLayout.setError(null);
            return true;
        }
    }

    public boolean confirmInput() {
        if (!validateRemarks() | !validateRoomNo()){
            return false;
        } else {
            final Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("day", day);
            childUpdates.put("endTime", endTimeTextView.getText().toString());
            childUpdates.put("roomNo", roomNoTextInputLayout.getEditText().getText().toString());
            childUpdates.put("startTime", startTimeTextView.getText().toString());
            childUpdates.put("remarks", remarksTextInputLayout.getEditText().getText().toString());
            childUpdates.put("courseCode", courseCode);
            if (day.equals(previousDay)) {
                mRoutineDatabaseReference.child(routineId).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditRoutineActivity.this, "Class Updated Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                mRoutineDatabaseReference.child(routineId).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mRoutineDatabaseReference.child(day + "-" + courseCode).setValue(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditRoutineActivity.this, "Class Updated Successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }

            return true;
        }
    }

    /**
     * Deletes a routine from database
     */
    public static class confirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.drawable.ic_warning_black_24dp);
            builder.setTitle(R.string.title_dialog_confirm);
            builder.setMessage(R.string.prompt_dialog_confirm_course)
                    .setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // delete routine from FireBase
                            mRoutineDatabaseReference.child(routineId).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(activity, "Deletion of " + routineId + "  is complete", Toast.LENGTH_LONG).show();
                                    activity.finish();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public void openDeleteRoutineConfirmationDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new confirmDialogFragment();
        dialog.show(getSupportFragmentManager(), "confirmDeletion");

    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new AddRoutineActivity.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "startTimePicker");
    }
    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new AddRoutineActivity.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "endTimePicker");
    }

    /**
     * Return the position of array according to the value of weekday
     * @param day
     * @return
     */
    private int getDayPosition(String day) {
        int position;
        switch (day.toLowerCase()) {
            case "saturday" : position = 0;
                break;
            case "sunday" : position = 1;
                break;
            case "monday" : position = 2;
                break;
            case "tuesday" : position = 3;
                break;
            case "wednesday" : position = 4;
                break;
            case "thursday" : position = 5;
                break;
            default: position = 6;
                break;
        }

        return position;
    }


}
