package com.example.mibne.scheduledapp;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class SingleNoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_notice);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView titleTextView = (TextView) findViewById(R.id.notice_title);
        TextView dateTextView = (TextView) findViewById(R.id.notice_date);
        TextView descriptionTextView = (TextView) findViewById(R.id.notice_description);
        TextView ownerTextView = (TextView) findViewById(R.id.notice_owner);
        TextView deadlineTextView = (TextView) findViewById(R.id.notice_deadline);

        Button button = (Button) findViewById(R.id.export_notice_button);

        //Get the bundle
        final Bundle bundle = getIntent().getExtras();

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
                Snackbar.make(v, "Exporting " + bundle.getString("Type"), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * Return the formatted date string (i.e. "Mar 3") from a Date object.
     */
    private String formatDate(Long dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd");
        return dateFormat.format(dateObject);
    }

}
