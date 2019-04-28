package com.example.mibne.scheduledapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mibne.scheduledapp.Models.Course;
import com.example.mibne.scheduledapp.Adapters.CourseAdapter;
import com.example.mibne.scheduledapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.INVISIBLE;
import static com.example.mibne.scheduledapp.Activities.LoginActivity.checkConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodayFragment} interface
 * to handle interaction events.
 * Use the {@link AllCoursesFragment#} factory method to
 * create an instance of this fragment.
 */
public class AllCoursesFragment extends Fragment {
    private String TAG = "AllCoursesFragment";
    private static final int READ_REQUEST_CODE = 42;

    private List<Course> courseList = new ArrayList<>();
    private List<Course> uploadCourseList = new ArrayList<>();

    private RecyclerView mCourseRecyclerView;
    private CourseAdapter mCourseAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private LinearLayout mNoInternetView;

    private String mUserDepartment;
    private String mUserOrganization;
    private String role;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCourseDatabaseReferance;
    private ValueEventListener mValueEventListener;

    private SharedPreferences sharedPreferences;

    public AllCoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.course_list, container, false);

        sharedPreferences = getActivity().getSharedPreferences("userPrefs",MODE_PRIVATE);

        //final Bundle mNoticeTypeBundle = getIntent().getExtras();
        mUserOrganization = sharedPreferences.getString("organization", null);
        mUserDepartment = sharedPreferences.getString("department", null);
        role = sharedPreferences.getString("role", "student");


        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        if (role.equals("admin") || role.equals("super")) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
                checkFilePermissions();
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mCourseDatabaseReferance = mFirebaseDatabase.getReference().child(mUserOrganization + "/" + mUserDepartment + "/courses");
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_course_list);
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_view_course_list);
        mNoInternetView = (LinearLayout) rootView.findViewById(R.id.no_internet);
        mNoInternetView.setVisibility(View.GONE);
        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mCourseRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_course);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mCourseRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mCourseRecyclerView.setHasFixedSize(true);
        /*
         * The CourseAdapter is responsible for linking our course data with the Views that
         * will end up displaying our course data.
         */
        mCourseAdapter = new CourseAdapter();

        mCourseRecyclerView.setAdapter(mCourseAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    readExcelData(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        courseList.clear();
        if (checkConnection(getContext())) {
            attachDatabaseReadListener();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mNoInternetView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        courseList.clear();
    }

    private void attachDatabaseReadListener() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    courseList.clear();
                    for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                        Course courses =  courseSnapshot.getValue(Course.class);
                        courseList.add(courses);
                    }
                    mCourseAdapter.setCourseData(courseList);
                    mProgressBar.setVisibility(INVISIBLE);
                    mNoInternetView.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(INVISIBLE);
                    mEmptyTextView.setText(R.string.prompt_no_course);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mCourseDatabaseReferance.addListenerForSingleValueEvent(mValueEventListener);
    }
    private void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            mCourseDatabaseReferance.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an xls file.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only xls files, using the xls MIME data type.
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("application/vnd.ms-excel");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     * Checks file permissions
     * If there is no file read permission then this method will make a request.
     */

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private InputStream getInputUri(Uri uri) throws IOException {
        InputStream inputStream = getActivity().getApplicationContext().getContentResolver().openInputStream(uri);
        return inputStream;
    }

    public void readExcelData(Uri uri) throws IOException  {
        //File inputWorkbook = new File();
        Workbook w;
        try {
            InputStream inputStream = getInputUri(uri);
            w = Workbook.getWorkbook(inputStream);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over first 10 column and lines
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 1; j < sheet.getRows(); j++) {
                for (int i = 0; i < sheet.getColumns(); i++) {
                    Cell cell = sheet.getCell(i, j);
                    CellType type = cell.getType();
                    if (type == CellType.LABEL) {
                        stringBuilder.append(cell.getContents() + ",");
                    }
                    if (type == CellType.NUMBER) {
                        stringBuilder.append(String.valueOf(cell.getContents()) + ",");
                    }

                }
                stringBuilder.append(":");
            }
            parseStringBuilder(stringBuilder);
            Log.v("TestData", stringBuilder.toString());
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for parsing imported data and storing in ArrayList<XYValue>
     */
    public void parseStringBuilder(StringBuilder mStringBuilder){
        Log.d(TAG, "parseStringBuilder: Started parsing.");

        // splits the sb into rows.
        String[] rows = mStringBuilder.toString().split(":");

        //Add to the ArrayList<XYValue> row by row
        for(int i=0; i<rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split(",");

            //use try catch to make sure there are no "" that try to parse into doubles.
            try{
                String courseCode = (columns[0]);
                String courseName = (columns[1]);
                String courseCredit = (columns[2]);

                String cellInfo = "(courseCode,courseName): (" + courseCode + "," + courseName + "," + courseCredit + ")";
                Log.d(TAG, "ParseStringBuilder: Data from row: " + cellInfo);

                uploadCourseList.add(new Course(fixCourseCredit(courseCredit),courseCode,courseName));
                mCourseDatabaseReferance.child(courseCode).setValue(new Course(fixCourseCredit(courseCredit),courseCode,courseName)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mEmptyTextView.setVisibility(View.GONE);
                    }
                });

            }catch (NumberFormatException e){

                Log.e(TAG, "parseStringBuilder: NumberFormatException: " + e.getMessage());

            }
        }
    }

    private String fixCourseCredit (String courseCredit) {
        switch (courseCredit) {
            case "1" : return courseCredit + ".0";
            case "2" : return courseCredit + ".0";
            case "3" : return courseCredit + ".0";
            case "4" : return courseCredit + ".0";
            default: return  courseCredit;
        }
    }
}