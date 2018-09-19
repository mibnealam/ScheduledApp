package com.example.mibne.scheduledapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoutineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        // Get the widgets reference from XML layout
        GridView gv = (GridView) findViewById(R.id.gv);
        final TextView tv_message = (TextView) findViewById(R.id.tv_message);

        // Initializing a new String Array
        String[] plants = new String[]{
                "", "8.00", "9.30", "11.00", "12.30", "2.00", "3.30",
                "Sat", "CSE-0101", "CSE-0105", "CSE-0201", "CSE-0109", "",
                "", "Sun", "", "", "", "",
                "", "", "Mon", "", "CSE-0400", "",
                "", "", "", "Wed", "", "",
                "", "", "", "", "Thu", "",
                "", "", "", "", ""
        };

        // Populate a List from Array elements
        final List<String> plantsList = new ArrayList<String>(Arrays.asList(plants));

        // Data bind GridView with ArrayAdapter (String Array elements)
        gv.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, plantsList){
            public View getView(int position, View convertView, ViewGroup parent) {

                // Return the GridView current item as a View
                View view = super.getView(position,convertView,parent);

                // Convert the view as a TextView widget
                TextView textView = (TextView) view;

                //textView.setTextColor(Color.DKGRAY);

                // Set the layout parameters for TextView widget
                LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                );
                textView.setLayoutParams(layoutParams);

                // Get the TextView LayoutParams
                //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)textView.getLayoutParams();

                // Set the width of TextView widget (item of GridView)
                //params.width = getPixelsFromDPs(RoutineActivity.this, 70);

                // Set the TextView layout parameters
                //textView.setLayoutParams(params);

                // Display TextView text in center position
                //textView.setGravity(Gravity.CENTER);

                // Set the TextView text font family and text size
                textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

                // Set the TextView text (GridView item text)
                textView.setText(plantsList.get(position));

                // Set the TextView background color
                textView.setBackgroundColor(Color.parseColor("#ffffff"));

                // Return the TextView widget as GridView item
                return textView;
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                tv_message.setText("Selected Item : " + selectedItem);
            }
        });
    }

    // Method for converting DP value to pixels
    public static int getPixelsFromDPs(Activity activity, int dps){
        Resources r = activity.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }
}