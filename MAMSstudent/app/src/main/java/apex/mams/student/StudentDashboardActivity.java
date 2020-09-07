package apex.mams.student;

import android.app.AlertDialog;
import android.app.FragmentManager;
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

public class StudentDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    MyPref myPref;
    Getip getip;
    TextView profile, proEmail;
    Button btn_logout;
    ImageView proImage;
    public static String user;
    public static String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myPref = new MyPref(this);
        getip = new Getip(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkToken();
        profile = (TextView) navigationView.getHeaderView(0).findViewById(R.id.profile);
        proEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.proEmail);

        profile.setText("Student");

        btn_logout = (Button) navigationView.getHeaderView(0).findViewById(R.id.btn_logout);
        proImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.proImage);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(StudentDashboardActivity.this);
                builder.setTitle("Are You Sure to Logout?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myPref.ClearAll();
                        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
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

        proImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentDashboardActivity.this, UpdateProfileActivity.class));

            }
        });


        DashboardFragment dashboardFragment = new DashboardFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, dashboardFragment).commit();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notification/RegisterTokenStud.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(StudentDashboardActivity.this, "TOKEN ADDED SUCESSIN", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(StudentDashboardActivity.this, "Error OccuredTokenIn" + error, Toast.LENGTH_SHORT).show();
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
            DashboardFragment dashboardFragment = new DashboardFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, dashboardFragment).commit();
        } else if (id == R.id.nav_material) {
            MaterialFragment materialFragment = new MaterialFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, materialFragment).commit();
        } else if (id == R.id.nav_attendanceReport) {
            AttendanceFragment attendanceFragment = new AttendanceFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.rel_for_fragment, attendanceFragment).commit();
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

    @Override
    protected void onResume() {
        super.onResume();
        profile.setText(myPref.getName());
        proEmail.setText(myPref.getEmail());

        user = profile.getText().toString();

        userId = myPref.getID();
        fetch_profile();
    }


    public void fetch_profile() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/select_student_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String student_id = myPref.getID().toString();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                        if (student_id.equals(jsonObject1.getString("studentid"))) {

                            Glide.with(StudentDashboardActivity.this).load(jsonObject1.getString("dpurl"))
                                    .asBitmap()
                                    .centerCrop()
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(new BitmapImageViewTarget(proImage) {

                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            super.setResource(resource);
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            proImage.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });

                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StudentDashboardActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentDashboardActivity.this, "Error Occured  " + error, Toast.LENGTH_SHORT).show();

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

}
