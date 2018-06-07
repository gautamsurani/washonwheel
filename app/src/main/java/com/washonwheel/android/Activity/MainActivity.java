package com.washonwheel.android.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.washonwheel.android.Adapter.MyAdapter;
import com.washonwheel.android.Fragment.*;
import com.washonwheel.android.Fragment.Wallet;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String TITLES[] = {"Dashboard"
            , "Book A Service"
            , "Booking History"
            , "My Account"
            , "My Wallet"
            , "Refer & Earn"
            , "Notification"
            , "About Us"
            , "WOW Service"
            , "Help"
            , "Contact Us"
            , "Rate Us"
            , "Logout"};

    int ICONS[] = {R.drawable.home
            , R.drawable.ic_car
            , R.drawable.ic_service_history
            , R.drawable.ic_user
            , R.drawable.walllet
            , R.drawable.refer
            , R.drawable.notification
            , R.drawable.ic_about
            , R.drawable.ic_wow_service
            , R.drawable.help
            , R.drawable.ic_contact_us
            , R.drawable.ic_contactus
            , R.drawable.logout};

    String NAME = "";
    String EMAIL = "";

    int PROFILE = R.drawable.user_layer;
    private DrawerLayout Drawer;                                  // Declaring DrawerLayout
    PackageInfo pInfo;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    public static final int REQUEST_ID = 2;
    Activity context;
    public static boolean isHome = true;
    String Token = "", DeviceID = "";
    String app_version;
    ProgressDialog progressDialog;
    String resMessage, resCode, version, msg;

    @SuppressLint({"RestrictedApi", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        context = this;

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);

        NAME = AppPreference.getPreference(context, AppPersistance.keys.USER_NAME);
        EMAIL = AppPreference.getPreference(context, AppPersistance.keys.USER_EMAIL);

        TextView tvTitle = toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setText(R.string.app_name);

        RecyclerView mRecyclerView = findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        RecyclerView.Adapter mAdapter = new MyAdapter(MainActivity.this, TITLES, ICONS, NAME, EMAIL, PROFILE);
        mRecyclerView.setAdapter(mAdapter);                             // Setting the adapter to RecyclerView

        Fragment fragment = new Home();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insertDummyContactWrapper();
        }

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();
                    displayView(recyclerView.getChildPosition(child));
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d("THE LOG", "ACTIONBAR DRAWER OPEN");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("THE LOG", "ACTIONBAR DRAWER CLOSE");
            }
        };

        // Drawer Toggle Object Made
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.toggle);
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (Drawer.isDrawerOpen(GravityCompat.START)) {
                    Drawer.closeDrawer(GravityCompat.START);
                } else {
                    Drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            app_version = pInfo.versionName;
            Token = FirebaseInstanceId.getInstance().getToken();
            DeviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            Log.e("Refreshed token: ", Token + "");
            Log.e("Refreshed  : DeviceId", DeviceID + "");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utils.isNetworkAvailable(context)) {
            new InsertTokan().execute();
        } else {
            retryInternet();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class InsertTokan extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_INSERT_TOKEN + Utils.getUserId(context)
                    + "&token=" + Token
                    + "&deviceID=" + DeviceID
                    + "&app_ver=" + app_version;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("Register", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        msg = jsonObjectList.getString("msg");
                        version = jsonObjectList.getString("version");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (Float.parseFloat(app_version) < Float.parseFloat(version)) {
                show();
            }
            if (!resCode.equalsIgnoreCase("0")) {
                if (resCode.equalsIgnoreCase("2")) {
                    Toast.makeText(context, "" + resMessage, Toast.LENGTH_SHORT).show();
                    AppPreference.removePreference(context, AppPersistance.keys.USER_ID);
                    AppPreference.removePreference(context, AppPersistance.keys.USER_NAME);
                    AppPreference.removePreference(context, AppPersistance.keys.USER_EMAIL);
                    AppPreference.removePreference(context, AppPersistance.keys.USER_NUMBER);
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            }
        }
    }

    public void show() {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.leogo)
                .setTitle("Update Available")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finishAffinity();
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException ignored) {

                        }
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .create();
        dialog.show();
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(context)) {
                    dialog.dismiss();
                    new InsertTokan().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, android.Manifest.permission.READ_SMS))
            permissionsNeeded.add("Read SMS");
        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Contacts");
        if (!addPermission(permissionsList, android.Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Find Location");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            return shouldShowRequestPermissionRationale(permission);
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(android.Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                break;
            case REQUEST_ID:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Location Permission Granted!", Toast.LENGTH_SHORT).show();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(MainActivity.this, "Location Permission Denied!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MyAccount();
                break;
            case 1:
                fragment = new Home();
                break;
            case 2:
                fragment = new Book_Services();
                break;
            case 3:
                fragment = new Service_History();
                break;
            case 4:
                fragment = new MyAccount();
                break;
            case 5:
                fragment = new Wallet();
                break;
            case 6:
                fragment = new Refer_Friend();
                break;
            case 7:
                startActivity(new Intent(MainActivity.this, OffersActivity.class));
                overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
                break;
            case 8:
                fragment = new AboutUsFragment();
                break;
            case 9:
                fragment = new WowServiceFragment();
                break;
            case 10:
                fragment = new HelpCenterFragment();
                break;
            case 11:
                fragment = new ContactUsFragment();
                break;
            case 12:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(intent);
                } catch (Exception ex) {
                    Log.e("Main Activity", "Main Activity" + ex.getMessage());
                }
                break;
            case 13:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to logout ?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AppPreference.removePreference(context, AppPersistance.keys.USER_ID);
                        AppPreference.removePreference(context, AppPersistance.keys.USER_NAME);
                        AppPreference.removePreference(context, AppPersistance.keys.USER_EMAIL);
                        AppPreference.removePreference(context, AppPersistance.keys.USER_NUMBER);
                        Intent i = new Intent(context, LoginActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            default:

                break;
        }

        if (fragment != null) {
            final Fragment finalFragment = fragment;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    isHome = false;
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    /*fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
                    fragmentTransaction.replace(R.id.frame_container, finalFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }, 0);
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public void onBackPressed() {
        if (isHome) {
            doExitApp();
        } else {
            Fragment fragment = new Home();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            isHome = true;
        }
    }
}
