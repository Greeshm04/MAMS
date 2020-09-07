package apex.mams.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class AdminDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MyPref myPref;
    TextView profile;
    Button btn_logout;
    Getip getip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_dashboard);
        myPref = new MyPref(this);
        getip = new Getip(getApplicationContext());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkToken();
        profile = (TextView) navigationView.getHeaderView(0).findViewById(R.id.profile);

        profile.setText("ADMIN");

        btn_logout = (Button) navigationView.getHeaderView(0).findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboardActivity.this);
                builder.setTitle("Are You Sure to Logout?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myPref.ClearAll();
                        Intent intent = new Intent(AdminDashboardActivity.this, Loginactivity.class);
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



        navigationView.getMenu().getItem(0).setChecked(true);
        DashBoardFragment dashBoardFragment = new DashBoardFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.rel_for_fragment, dashBoardFragment).commit();


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

    public void checkToken() {

        try {
            if (!MyFirebaseInstanceIDService.token.equals(null)) {
                registerToken();
            }
        } catch (NullPointerException e) {

        }
    }

    public void registerToken() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notification/RegisterToken.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(AdminDashboardActivity.this, "TOKEN ADDED SUCESSIN", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(AdminDashboardActivity.this, "Error OccuredTokenIn" + error, Toast.LENGTH_SHORT).show();
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

    public void setActionbarTitle(String Title) {
        getSupportActionBar().setTitle(Title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_logout) {
            btn_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myPref.ClearAll();
                    Intent intent = new Intent(AdminDashboardActivity.this, Loginactivity.class);
                    finish();
                    startActivity(intent);
                }
            });

        }

        if (id == R.id.nav_DashBoard) {
            DashBoardFragment dashBoardFragment = new DashBoardFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rel_for_fragment, dashBoardFragment).commit();
        } else if (id == R.id.nav_Faculty) {
            FacultyFragment facultyFragment = new FacultyFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rel_for_fragment, facultyFragment).commit();

        } else if (id == R.id.nav_Student) {
            StudentFragment studentFragment = new StudentFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rel_for_fragment, studentFragment).commit();

        } else if (id == R.id.nav_Subject) {
            SubjectFragment subjectFragment = new SubjectFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rel_for_fragment, subjectFragment).commit();

        } else if (id == R.id.nav_Academic) {
            AcademicFragment academicFragment = new AcademicFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rel_for_fragment, academicFragment).commit();

        } else if (id == R.id.nav_AttendanceReport) {
            AttendanceReportFragment attendanceReportFragment = new AttendanceReportFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.rel_for_fragment, attendanceReportFragment).commit();
        }  else if (id == R.id.nav_send) {

            Intent intent=new Intent(getApplicationContext(),ChangePasswordActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
