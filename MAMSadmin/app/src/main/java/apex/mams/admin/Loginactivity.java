package apex.mams.admin;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Loginactivity extends Activity {

    EditText edtloginuname, edtloginupass, ip;
    Button btnlogin;
    Getip getip;
    ProgressDialog progressDialog;
    MyPref myPref;
    Button chng_ip;
    ImageView img_logo;
    ArrayList<String> permissionToRequest = new ArrayList<String>();
    ArrayList<String> permissionToReject = new ArrayList<String>();
    ArrayList<String> permission = new ArrayList<String>();
    final int ALL_PERMISSION_RESULT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPref = new MyPref(Loginactivity.this);


        try {

            if (myPref.getKEY_ID().equals("admin")) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, AdminDashboardActivity.class));
                return;
            }

        } catch (NullPointerException e) {

            setContentView(R.layout.activity_loginactivity);

        }


        edtloginuname = (EditText) findViewById(R.id.edtloginuname);
        edtloginupass = (EditText) findViewById(R.id.edtloginupass);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        img_logo = (ImageView) findViewById(R.id.img_logo);
        chng_ip = (Button) findViewById(R.id.chng_ip);
        ip = (EditText) findViewById(R.id.ip);
        getip = new Getip(this);

        ip.setVisibility(View.GONE);
        chng_ip.setVisibility(View.GONE);

        chng_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myPref.setIP(ip.getText().toString());
                finish();
                startActivity(getIntent());

            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (VolleyApp.IsNotEmpty(new EditText[]{edtloginuname, edtloginupass})) {
                    progressDialog = new ProgressDialog(Loginactivity.this);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(true);
                    progressDialog.setMessage("Login in progress..");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    checkLOGINadmin();
                }
            }
        });

        img_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip.setVisibility(View.VISIBLE);
                chng_ip.setVisibility(View.VISIBLE);
            }
        });

        permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        permissionToRequest = findUnaskedPermission(permission);


        if (canMakesMore() && permissionToRequest.size() > 0) {
            requestPermissions(permissionToRequest.toArray(new String[permissionToRequest.size()]), ALL_PERMISSION_RESULT);
        }

    }

    public ArrayList<String> findUnaskedPermission(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < wanted.size(); i++) {
            if (hasPermission(wanted.get(i)) == false) {
                result.add(wanted.get(i));
            }
        }

        return result;
    }

    public Boolean hasPermission(String permission) {

        if (canMakesMore()) {

            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;

        }

        return true;
    }

    public Boolean canMakesMore() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ALL_PERMISSION_RESULT) {
            for (int i = 0; i < permissionToRequest.size(); i++) {
                if (!hasPermission(permissionToRequest.get(i))) {
                    permissionToReject.add(permissionToRequest.get(i));
                }
            }

            if (permissionToReject.size() > 0) {
                if (canMakesMore()) {
                    if (shouldShowRequestPermissionRationale(permissionToReject.get(0))) {
                        showMessageOkCancel("These permissions are mandatory for the application. Please allow access.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (canMakesMore()) {
                                            requestPermissions(permissionToRequest.toArray(new String[permissionToRequest.size()]), ALL_PERMISSION_RESULT);
                                        }
                                    }
                                });
                    }
                }
            }

        }

    }

    public void showMessageOkCancel(String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    public void checkLOGINadmin() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "admin_master/select.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                int flag = 0;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                        if (edtloginuname.getText().toString().trim().equals(jsonObject1.getString("adminid"))) {
                            if (edtloginupass.getText().toString().equals(jsonObject1.getString("password"))) {
                                flag = 0;

                                myPref.setKEY_ID("admin");
                                myPref.setID(jsonObject1.getString("adminid"));
                                myPref.setPassID(jsonObject1.getString("password"));


                                Intent intent3 = new Intent(Loginactivity.this, AdminDashboardActivity.class);
                                finish();
                                startActivity(intent3);


                                progressDialog.dismiss();
                                break;
                            } else {
                                flag = 1;
                                break;
                            }
                        } else {
                            flag = 1;
                        }
                    }

                    if (flag == 1) {
                        progressDialog.dismiss();
                        Toast.makeText(Loginactivity.this, "UserName Or Password Is Incorrect", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Loginactivity.this, "ERROR" + e, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Loginactivity.this, "Error Occured" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

}
