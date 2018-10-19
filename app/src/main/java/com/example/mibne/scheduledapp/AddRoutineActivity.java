package com.example.mibne.scheduledapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddRoutineActivity extends AppCompatActivity {

    private static String timePickerFlag;

    private TextInputLayout courseCodeEditText;
    private TextInputLayout roomNoEditText;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private String routineDay;
    private Button addClassButton;
    private static boolean startTimeIsEmpty = true;
    private static boolean endTimeIsEmpty = true;
    private boolean routineDayIsEmpty = true;

    private String mOrganization;
    private String mDepartment;

    Routine routine;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRoutineDatabaseReferance;
    private DatabaseReference mUserDatabaseReferance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);

        getSupportActionBar().setTitle("Add A Class");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mOrganization = "sub";
        mDepartment = "cse";

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRoutineDatabaseReferance = mFirebaseDatabase.getReference().child(mOrganization).child(mDepartment).child("routines");
        mUserDatabaseReferance = mFirebaseDatabase.getReference().child("users");

        courseCodeEditText = (TextInputLayout) findViewById(R.id.course_code_wrapper);
        roomNoEditText = (TextInputLayout) findViewById(R.id.room_no_wrapper);
        startTimeTextView = (TextView) findViewById(R.id.text_view_start_time);
        endTimeTextView = (TextView) findViewById(R.id.text_view_end_time);
        addClassButton = (Button) findViewById(R.id.button_add_class);

        Spinner spinner = (Spinner) findViewById(R.id.week_day_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_week_day, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                routineDay = parent.getItemAtPosition(position).toString();
                routineDayIsEmpty = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(null,"Please Select priority", Toast.LENGTH_SHORT).show();
                routineDayIsEmpty = true;
            }
        });


        routine = new Routine();

        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRoutineData();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateCourseCode() {
        String courseCodeInput = courseCodeEditText.getEditText().getText().toString().trim();

        if (courseCodeInput.isEmpty()) {
            courseCodeEditText.setError("Please set course code.");
            return false;
        } else if (courseCodeInput.length() > 20) {
            courseCodeEditText.setError("Too long");
            return false;
        } else if (courseCodeInput.length() < 7) {
            courseCodeEditText.setError("Too short");
            return false;
        } else {
            courseCodeEditText.setError(null);
            return true;
        }
    }

    private boolean validateRoomNo() {
        String roomNoInput = roomNoEditText.getEditText().getText().toString().trim();

        if (roomNoInput.isEmpty()) {
            roomNoEditText.setError("Please set room number.");
            return false;
        } else if (roomNoInput.length() > 40) {
            roomNoEditText.setError("Too long");
            return false;
        } else if (roomNoInput.length() < 3) {
            roomNoEditText.setError("Too short");
            return false;
        } else {
            roomNoEditText.setError(null);
            return true;
        }
    }

    private boolean validateStartTime() {
        if (startTimeIsEmpty){
            startTimeTextView.setText("Choose Start Time");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startTimeTextView.setTextColor(getColor(R.color.credit6));
            }
            return false;
        } else {
            startTimeIsEmpty = false;
            return true;
        }
    }
    private boolean validateEndTime() {
        if (endTimeIsEmpty){
            endTimeTextView.setText("Choose End Time");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startTimeTextView.setTextColor(getColor(R.color.credit6));
            }
            return false;
        } else {
            endTimeIsEmpty = false;
            return true;
        }
    }

    public boolean confirmInput() {
        if (!validateCourseCode() | !validateRoomNo() | !validateStartTime() | !validateEndTime()){
            Log.v("routineStored", "validation error!");
            return false;
        } else {
            routine.setCourseCode(courseCodeEditText.getEditText().getText().toString());
            routine.setRoomNo(roomNoEditText.getEditText().getText().toString());
            routine.setStartTime(startTimeTextView.getText().toString());
            routine.setEndTime(endTimeTextView.getText().toString());
            routine.setDay(routineDay);

            Log.v("routineStored", routine.toString());

            return true;
        }
    }

    public void sendRoutineData() {
        if (confirmInput()) {
            mRoutineDatabaseReferance.push().setValue(routine).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddRoutineActivity.this, "Class is added!", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "startTimePicker");
        timePickerFlag = "startTimePicker";
    }
    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "endTimePicker");
        timePickerFlag = "endTimePicker";
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Update deadline text view when the date is chosen by the user
            Calendar calendar = Calendar.getInstance();
            calendar.set(0, 0, 0, hourOfDay, minute);
            switch (timePickerFlag){
                case "startTimePicker" : {
                    TextView textView = getActivity().findViewById(R.id.text_view_start_time);
                    textView.setText(DateFormat.format("hh:mm aaa", calendar));
                    startTimeIsEmpty = false;
                    break;
                }
                case "endTimePicker" : {
                    TextView textView = getActivity().findViewById(R.id.text_view_end_time);
                    textView.setText(DateFormat.format("hh:mm aaa", calendar));
                    endTimeIsEmpty = false;
                    break;
                }
                default:
                    break;
            }

        }
    }
}
