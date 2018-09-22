package com.example.mibne.scheduledapp;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleNoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_notice);

        TextView titleTextView = (TextView) findViewById(R.id.notice_title);
        TextView dateTextView = (TextView) findViewById(R.id.notice_date);
        TextView descriptionTextView = (TextView) findViewById(R.id.notice_description);
        TextView ownerTextView = (TextView) findViewById(R.id.notice_owner);
        TextView deadlineTextView = (TextView) findViewById(R.id.notice_deadline);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data… and set value to the textView
        titleTextView.setText(bundle.getString("Title"));
        dateTextView.setText(formatDate(Integer.valueOf(bundle.getString("Date"))));
        descriptionTextView.setText(bundle.getString("Description"));
        ownerTextView.setText(bundle.getString("Owner"));
        deadlineTextView.setText(formatDate(Integer.valueOf(bundle.getString("Deadline"))));
    }

    /**
     * Return the formatted date string (i.e. "Mar 3") from a Date object.
     */
    private String formatDate(Integer dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd");
        return dateFormat.format(dateObject);
    }

}
