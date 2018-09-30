package com.example.mibne.scheduledapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AllCoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new AllCoursesFragment())
                .commit();
    }
}
