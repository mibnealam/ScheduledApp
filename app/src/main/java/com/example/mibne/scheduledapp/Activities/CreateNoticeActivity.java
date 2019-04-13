package com.example.mibne.scheduledapp.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mibne.scheduledapp.Models.Notice;
import com.example.mibne.scheduledapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class CreateNoticeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private TextInputLayout noticeTitleEditText;
    private TextView buttonDatePicker;
    private TextInputLayout noticeDescriptionEditText;
    private TextInputLayout noticeFromEditText;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoticeDatabaseReference;

    private Notice notice;

    private String noticeTo;
    private static Long deadline;
    private static boolean deadlineIsEmpty = true;
    private String noticePriority;
    private boolean noticePriorityIsEmpty = true;
    private String noticeType;
    private String mUserDepartment;
    private String mUserOrganization;
    private String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

//        Bundle bundle = getIntent().getExtras();
//        noticeType = bundle.getString("Type");
//        mUserOrganization = bundle.getString("organization");
//        mUserDepartment = bundle.getString("department");
//        role = bundle.getString("role");
        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        noticeType = "Notice";
        mUserOrganization = sharedPreferences.getString("organization", null);
        mUserDepartment = sharedPreferences.getString("department", null);
        role = sharedPreferences.getString("role", "student");


        getSupportActionBar().setTitle("Create " + noticeType);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNoticeDatabaseReference = mFirebaseDatabase.getReference();

        Spinner spinner = (Spinner) findViewById(R.id.spinner_notice_priority);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_notice_priority, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        notice  = new Notice();

        buttonDatePicker = (TextView) findViewById(R.id.button_date_picker);

        noticeTitleEditText = (TextInputLayout) findViewById(R.id.notice_title_wrapper);
        noticeDescriptionEditText = (TextInputLayout) findViewById(R.id.notice_description_wrapper);
        noticeFromEditText = (TextInputLayout) findViewById(R.id.notice_from_wrapper);


        buttonDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                noticePriority = parent.getItemAtPosition(position).toString();
                noticePriorityIsEmpty = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(null,"Please Select priority", Toast.LENGTH_SHORT).show();
                noticePriorityIsEmpty = true;
            }
        });
    }

    private boolean validateNoticeTitle() {
        String noticeTitleInput = noticeTitleEditText.getEditText().getText().toString().trim();

        if (noticeTitleInput.isEmpty()) {
            noticeTitleEditText.setError("Title can't be empty");
            return false;
        } else if (noticeTitleInput.length() > 200) {
            noticeTitleEditText.setError("Title is too long");
            return false;
        } else if (noticeTitleInput.isEmpty()) {
            noticeTitleEditText.setError("Title is empty");
            return false;
        } else {
            noticeTitleEditText.setError(null);
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean validateNoticeDeadline() {
        if (deadlineIsEmpty){
                buttonDatePicker.setText("Choose Date");
                buttonDatePicker.setTextColor(getColor(R.color.credit6));
                return false;
        } else {
            return true;
        }
    }

    private boolean validateNoticeDescription() {
        String noticeDescriptionInput = noticeDescriptionEditText.getEditText().getText().toString().trim();

        if (noticeDescriptionInput.isEmpty()) {
            noticeDescriptionEditText.setError("Description can't be empty");
            return false;
        } else if (noticeDescriptionInput.length() > 800) {
            noticeDescriptionEditText.setError("Description is too long");
            return false;
        } else {
            noticeDescriptionEditText.setError(null);
            return true;
        }
    }

    private boolean validateNoticeFrom() {
        String noticeFromInput = noticeFromEditText.getEditText().getText().toString().trim();

        if (noticeFromInput.isEmpty()) {
            noticeFromEditText.setError("Field can't be empty");
            return false;
        } else if (noticeFromInput.length() > 60) {
            noticeFromEditText.setError("Too long");
            return false;
        } else if (noticeFromInput.length() < 2) {
            noticeFromEditText.setError("Too short");
            return false;
        } else {
            noticeFromEditText.setError(null);
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean confirmInput() {
        if (!validateNoticeTitle() | !validateNoticeDescription() | !validateNoticeFrom() | !validateNoticeDeadline()) {
            return false;
        } else {
            notice.setNoticeTitle(noticeTitleEditText.getEditText().getText().toString());
            notice.setNoticeDescription(noticeDescriptionEditText.getEditText().getText().toString());
            notice.setNoticeOwner(noticeFromEditText.getEditText().getText().toString());
            notice.setNoticeDate(System.currentTimeMillis());
            notice.setNoticeDeadline(deadline);
            notice.setNoticePriority(noticePriority);
            notice.setNoticeType(noticeType);

            noticeTo = mUserDepartment;

            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_notice_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
            finish();
            return true;
            case R.id.send_notice:
                sendNotice();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotice() {
        if (confirmInput()) {
            // Handle item selection
            if (noticeTo.matches("[a-zA-Z]*") && role.equals("admin")){
                //Sends notice to the users department
                mNoticeDatabaseReference.child(mUserOrganization).child(noticeTo).child("notices").push().setValue(notice).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateNoticeActivity.this, "Notice created successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateNoticeActivity.this, "Failed to create Notice!", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (noticeTo.matches("[a-zA-Z]*") && role.equals("super")) {
                mNoticeDatabaseReference.child(mUserOrganization).child("notices").push().setValue(notice).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateNoticeActivity.this, "Notice created successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateNoticeActivity.this, "Failed to create Notice!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "Wrong input To!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Create a Date variable/object with user chosen date
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            Date chosenDate = cal.getTime();
            deadline = chosenDate.getTime();
            deadlineIsEmpty = false;
            // Format the date using style full
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
            String date = dateFormat.format(chosenDate);
            // Update deadline text view when the date is chosen by the user
            TextView textView = getActivity().findViewById(R.id.button_date_picker);
            textView.setText(date);
            textView.setTextColor(getActivity().getColor(R.color.white));
        }
    }
}
