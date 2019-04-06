package com.example.mibne.scheduledapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mibne.scheduledapp.Fragments.AllCoursesFragment;
import com.example.mibne.scheduledapp.R;

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
