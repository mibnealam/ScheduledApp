package com.example.mibne.scheduledapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mibne.scheduledapp.Fragments.EnrolledCoursesFragment;
import com.example.mibne.scheduledapp.R;

public class EnrolledCoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new EnrolledCoursesFragment())
                .commit();
    }
}