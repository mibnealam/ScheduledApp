package com.example.mibne.scheduledapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class userInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        Spinner orgSpinner = (Spinner) findViewById(R.id.organizations_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> orgSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.organizations_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        orgSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        orgSpinner.setAdapter(orgSpinnerAdapter);

        Spinner deptSpinner = (Spinner) findViewById(R.id.departments_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> deptSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.departments_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        deptSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        deptSpinner.setAdapter(deptSpinnerAdapter);
    }
}
