package com.example.mibne.scheduledapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private static String TAG = "SettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //addPreferencesFromResource(R.xml.pref_main);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_main);

            Preference editUserInfoPref = findPreference(getString(R.string.key_pref_edit_user_info));
            editUserInfoPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), EditUserAccountActivity.class);
                    Bundle bundle = getActivity().getIntent().getExtras();
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
                }
            });

            Preference userCourseRegistrationPref = findPreference(getString(R.string.key_user_course_registration));
            userCourseRegistrationPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), RegistrationActivity.class);
                    Bundle bundle = getActivity().getIntent().getExtras();
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
                }
            });

            Preference feedbackPref = findPreference(getString(R.string.key_send_feedback));
            feedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity().getApplicationContext());
                    return true;
                }
            });

            Preference contactDeveloperPref = findPreference(getString(R.string.key_contact_developer));
            contactDeveloperPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    contactDeveloper(getActivity().getApplicationContext());
                    return true;
                }
            });

//            final Preference notificationPref = (Preference) findPreference(getString(R.string.key_pref_notification_targets));
//            notificationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    android.app.FragmentManager fragmentManager = getFragmentManager();
//                    //openDialog(fragmentManager);
//                    return true;
//                }
//            });

            final SwitchPreference notificationStatePref = (SwitchPreference) findPreference(getString(R.string.key_pref_notifications));
            notificationStatePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                boolean isSelfSubscriptionEnabled = false;
                boolean isGeneralSubscriptionEnabled = false;
                boolean isDepartmentSubscriptionEnabled = false;
                boolean isCourseSubscriptionEnabled = false;
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (notificationStatePref.isChecked()){
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("General").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isGeneralSubscriptionEnabled = true;
                                if (!task.isSuccessful()) {
                                    isGeneralSubscriptionEnabled = false;
                                }
                            }
                        });
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Department").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isDepartmentSubscriptionEnabled = true;
                                if (!task.isSuccessful()) {
                                    isDepartmentSubscriptionEnabled = false;
                                }
                            }
                        });
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Course").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isCourseSubscriptionEnabled = true;
                                if (!task.isSuccessful()) {
                                    isCourseSubscriptionEnabled = false;
                                }
                            }
                        });
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Routine").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                }
                                if (isGeneralSubscriptionEnabled && isDepartmentSubscriptionEnabled && isCourseSubscriptionEnabled) {
                                    Log.v(TAG, "Un-subscribes from all notifications");
                                    notificationStatePref.setChecked(false);
                                    //notificationPref.setEnabled(false);
                                }
                            }
                        });
                    } else {
                        FirebaseMessaging.getInstance().subscribeToTopic("Self").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isSelfSubscriptionEnabled = true;
                                if (!task.isSuccessful()) {
                                    isSelfSubscriptionEnabled = false;
                                }
                            }
                        });
                        FirebaseMessaging.getInstance().subscribeToTopic("General").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isGeneralSubscriptionEnabled = true;
                                if (!task.isSuccessful()) {
                                    isGeneralSubscriptionEnabled = false;
                                }
                            }
                        });
                        FirebaseMessaging.getInstance().subscribeToTopic("Department").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isDepartmentSubscriptionEnabled = true;
                                if (!task.isSuccessful()) {
                                    isDepartmentSubscriptionEnabled = false;
                                }
                            }
                        });
                        FirebaseMessaging.getInstance().subscribeToTopic("Course").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isCourseSubscriptionEnabled = true;
                                if (!task.isSuccessful()) {
                                    isCourseSubscriptionEnabled = false;
                                }
                            }
                        });
                        FirebaseMessaging.getInstance().subscribeToTopic("Routine").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                }
                                if (isSelfSubscriptionEnabled && isGeneralSubscriptionEnabled && isDepartmentSubscriptionEnabled && isCourseSubscriptionEnabled) {
                                    Log.v(TAG, "Subscribed to all notifications");
                                    notificationStatePref.setChecked(true);
                                    //notificationPref.setEnabled(true);
                                }
                            }
                        });
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact.scheduledapp@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from scheduled app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.createChooser(intent, context.getString(R.string.choose_email_client));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Email client intent to contact developer
     */
    public static void contactDeveloper(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mibnealam@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from scheduled app");
        intent.createChooser(intent, context.getString(R.string.choose_email_client));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static void openDialog(FragmentManager fragmentManager) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NotificationOptionsDialogueFragment();
        //dialog.setArguments(bundle);
        dialog.show(fragmentManager, "Notifications");
    }
}