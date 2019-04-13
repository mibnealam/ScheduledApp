package com.example.mibne.scheduledapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mibne.scheduledapp.R;
import com.example.mibne.scheduledapp.Models.Routine;
import com.example.mibne.scheduledapp.Adapters.RoutineAdapter;
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

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static android.view.View.INVISIBLE;

public class RoutineActivity extends AppCompatActivity implements RoutineAdapter.RoutineAdapterListener {

    private String TAG = "RoutineActivity";
    private static final int READ_REQUEST_CODE = 42;

    private List<Routine> routineList = new ArrayList<>();
    private List<Routine> uploadRoutineList = new ArrayList<>();

    private LinearLayout mRoutineUserView;
    private RecyclerView mRoutineRecyclerView;
    private RoutineAdapter mRoutineAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;

    private String mUserDepartment;
    private String mUserOrganization;
    private String role;

    private SearchView searchView;

    // Firebase instance variables
    private DatabaseReference mRoutineDatabaseReferance;
    private ValueEventListener mValueEventListener;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        mUserOrganization = sharedPreferences.getString("organization", null);
        mUserDepartment = sharedPreferences.getString("department", null);
        role = sharedPreferences.getString("role", "student");


        com.getbase.floatingactionbutton.FloatingActionsMenu floatingActionsMenu = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.fab_routine);

        com.getbase.floatingactionbutton.FloatingActionButton floatingActionButtonUploadRoutine = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_upload_routine);

        floatingActionButtonUploadRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();

            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton floatingActionButtonAddRoutine = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_add_routine);

        floatingActionButtonAddRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddRoutineActivity.class);
                startActivity(intent);
            }
        });

        switch (role) {
//            case "teacher" :
//                floatingActionsMenu.setVisibility(View.VISIBLE);
//                floatingActionButtonUploadRoutine.setVisibility(View.GONE);
//                checkFilePermissions();
//                break;
            case "admin" :
                floatingActionsMenu.setVisibility(View.VISIBLE);
                checkFilePermissions();
                break;

            case "super" :
                floatingActionsMenu.setVisibility(View.VISIBLE);
                checkFilePermissions();
                break;

                default:
                    floatingActionsMenu.setVisibility(View.GONE);
                    break;
        }


        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRoutineDatabaseReferance = mFirebaseDatabase.getReference().child(mUserOrganization + "/" + mUserDepartment + "/routines");
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_routine_list);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view_routine_list);
        mRoutineUserView = (LinearLayout) findViewById(R.id.user_view_routine);


        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRoutineRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_routine);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRoutineRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRoutineRecyclerView.setHasFixedSize(true);
        /*
         * The CourseAdapter is responsible for linking our course data with the Views that
         * will end up displaying our course data.
         */
        mRoutineAdapter = new RoutineAdapter(this, routineList, this);

        mRoutineRecyclerView.setAdapter(mRoutineAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        mRoutineUserView.setVisibility(INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mRoutineAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mRoutineAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        routineList.clear();
        attachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        routineList.clear();
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
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void attachDatabaseReadListener() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot routineDataSnapshot: dataSnapshot.getChildren()) {
                        Routine routine =  routineDataSnapshot.getValue(Routine.class);
                        routineList.add(routine);
                    }
                    mRoutineAdapter.setRoutineData(sortRoutineByDay(routineList));
                    mEmptyTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(INVISIBLE);
                } else {
                    mProgressBar.setVisibility(INVISIBLE);
                    mEmptyTextView.setText(R.string.prompt_no_routine);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mRoutineDatabaseReferance.addValueEventListener(mValueEventListener);
    }

    private void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            mRoutineDatabaseReferance.removeEventListener(mValueEventListener);
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
     *reads the excel file columns then rows. Stores data as ExcelUploadData object
     * @return
     */
    public void readExcelData(Uri uri) throws IOException  {
        Workbook w;
        try {
            InputStream inputStream = getInputUri(uri);
            w = Workbook.getWorkbook(inputStream);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 1; j < sheet.getRows(); j++) {
                for (int i = 0; i < sheet.getColumns(); i++) {
                    jxl.Cell cell = sheet.getCell(i, j);
                    stringBuilder.append(cell.getContents() + ",");

                }
                stringBuilder.append(">");
            }
            Log.v("TestData", stringBuilder.toString());
            parseStringBuilder(stringBuilder);
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
        String[] rows = mStringBuilder.toString().split(">");
        Log.v("TestData", String.valueOf(rows.length));

        //Add to the ArrayList<XYValue> row by row
        for(int i=0; i<rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split(",");

            //use try catch to make sure there are no "" that try to parse into doubles.
            try{
                String day = (columns[0]);
                String startTime = (columns[1]);
                String endTime = (columns[2]);
                String courseCode = (columns[3]);
                String roomNo = (columns[4]);
                String remarks = "";

                String cellInfo = "(courseCode,courseName): (" + day + "," + courseCode + "," + startTime + "," + endTime + "," + roomNo + ")";
                Log.d(TAG, "ParseStringBuilder: Data from row: " + cellInfo);

                uploadRoutineList.add(new Routine(day,courseCode,startTime,endTime,roomNo,remarks));
                Log.v(TAG, uploadRoutineList.toString());
                mRoutineDatabaseReferance.child(day + "-" + courseCode).setValue(new Routine(day,courseCode,startTime,endTime,roomNo,remarks)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mEmptyTextView.setVisibility(View.GONE);
                    }
                });

                //add the the uploadData ArrayList

            }catch (NumberFormatException e){

                Log.e(TAG, "parseStringBuilder: NumberFormatException: " + e.getMessage());

            }
        }
    }

    /**
     * Checks file permissions
     * If there is no file read permission then this method will make a request.
     */

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
                }
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
    private InputStream getInputUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        return inputStream;
    }

    @Override
    public void onRoutineSelected(Routine routine) {
        if (role.equals("admin") || role.equals("super")) {
            Intent editRoutineIntent = new Intent(this, EditRoutineActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("courseCode", routine.getCourseCode());
            bundle.putString("day", routine.getDay());
            bundle.putString("startTime", routine.getStartTime());
            bundle.putString("endTime", routine.getEndTime());
            bundle.putString("roomNo", routine.getRoomNo());
            editRoutineIntent.putExtras(bundle);
            this.startActivity(editRoutineIntent);
        }
    }

    public static List<Routine> sortRoutineByDay(List<Routine> routineList) {
        List<Routine> filteredList = new ArrayList<>();
        for (Routine routine: routineList) {
            if (routine.getDay().equals("Saturday")) {
                filteredList.add(routine);
            }
        }
        for (Routine routine: routineList) {
            if (routine.getDay().equals("Sunday")) {
                filteredList.add(routine);
            }
        }
        for (Routine routine: routineList) {
            if (routine.getDay().equals("Monday")) {
                filteredList.add(routine);
            }
        }
        for (Routine routine: routineList) {
            if (routine.getDay().equals("Tuesday")) {
                filteredList.add(routine);
            }
        }
        for (Routine routine: routineList) {
            if (routine.getDay().equals("Wednesday")) {
                filteredList.add(routine);
            }
        }
        for (Routine routine: routineList) {
            if (routine.getDay().equals("Thursday")) {
                filteredList.add(routine);
            }
        }
        for (Routine routine: routineList) {
            if (routine.getDay().equals("Friday")) {
                filteredList.add(routine);
            }
        }
        return filteredList;
    }
}