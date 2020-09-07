package apex.mams.faculty;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FacultyDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btn_logout;
    TextView profile, proEmail;
    ImageView proImage;
    public static String user;
    public static String userId;
    MyPref myPref;
    Getip getip;
    public static String subjectid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_faculty_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        profile = navigationView.getHeaderView(0).findViewById(R.id.profile);
        proEmail = navigationView.getHeaderView(0).findViewById(R.id.proEmail);
        proImage = navigationView.getHeaderView(0).findViewById(R.id.proImage);
        btn_logout = navigationView.getHeaderView(0).findViewById(R.id.btn_logout);

        myPref = new MyPref(this);
        getip = new Getip(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        DashboardFragment dashboardFragment = new DashboardFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, dashboardFragment).commit();

        proImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FacultyDashboardActivity.this, UpdateProfileActivity.class));

            }
        });

        checkToken();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(FacultyDashboardActivity.this);
                builder.setTitle("Are You Sure to Logout?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myPref.ClearAll();
                        Intent intent = new Intent(FacultyDashboardActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();



        profile.setText(myPref.getName());
        proEmail.setText(myPref.getEmail());
        userId = myPref.getID();
        user = profile.getText().toString();
        fetch_profile();
    }

    public void checkToken() {

        try {
            if (!MyFirebaseInstanceIDService.token.equals(null)) {
                registerToken();
            }
        } catch (NullPointerException e) {

        }
    }

    public void registerToken() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notification/RegisterTokenfac.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(FacultyDashboardActivity.this, "TOKEN ADDED SUCESSIN", Toast.LENGTH_SHORT).show();
                        Log.e("TokenIn", "Success");


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("TOKENIn", "ERROR" + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(FacultyDashboardActivity.this, "Error OccuredTokenIn" + error, Toast.LENGTH_SHORT).show();
                Log.e("TOKENIn", "e occured du to" + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Token", MyFirebaseInstanceIDService.token);
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void setActionBarTitle(String Title) {
        getSupportActionBar().setTitle(Title);
    }

    public void fetch_profile() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/select_faculty_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String faculty_id = myPref.getID().toString();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                        if (faculty_id.equals(jsonObject1.getString("facultyid"))) {


                            Glide.with(FacultyDashboardActivity.this).load(jsonObject1.getString("dpurl")).asBitmap().centerCrop().into(new BitmapImageViewTarget(proImage) {

                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    proImage.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                            subjectid = jsonObject1.getString("subjectid");

                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FacultyDashboardActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FacultyDashboardActivity.this, "Error Occured  " + error, Toast.LENGTH_SHORT).show();

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_dashboard) {
            // Handle the camera action
            DashboardFragment dashboardFragment = new DashboardFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, dashboardFragment).commit();

        } else if (id == R.id.menu_notice) {
            UploadNoticeFragment uploadNoticeFragment = new UploadNoticeFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, uploadNoticeFragment).commit();

        } else if (id == R.id.menu_material) {

            MaterialFragment materialFragment = new MaterialFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, materialFragment).commit();
        } else if (id == R.id.menu_atttendance) {
            AttendanceFragment attendanceFragment = new AttendanceFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, attendanceFragment).commit();


        } else if (id == R.id.menu_atttendanceReport) {
            AttendanceReportFragment attendanceReportFragment=new AttendanceReportFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, attendanceReportFragment).commit();


        } else if (id == R.id.nav_share) {

            Intent intent=new Intent(getApplicationContext(),UpdateProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {

            Intent intent=new Intent(getApplicationContext(),ChangePasswordActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
