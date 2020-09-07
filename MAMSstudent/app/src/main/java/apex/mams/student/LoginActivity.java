package apex.mams.student;

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

public class LoginActivity extends Activity {
    EditText edtloginuname, edtloginupass;
    private static final int REQUEST_PERMISSION = 10;
    Button btnlogin;
    TextView txtforgotpassword;
    Getip getip;
    ProgressDialog progressDialog;
    MyPref myPref;
    EditText ip;
    ImageView img_logo;
    Button ip_chng;
    ArrayList<String> permissionToRequest = new ArrayList<String>();
    ArrayList<String> permissionToReject = new ArrayList<String>();
    ArrayList<String> permission = new ArrayList<String>();
    final int ALL_PERMISSION_RESULT = 101;

    ArrayList<String> currSemSubCodeArray = new ArrayList<>();
    ArrayList<String> currSemSubIdArray = new ArrayList<>();
    ArrayList<String> currSemSubNameArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPref = new MyPref(LoginActivity.this);


        try {

            if (myPref.getKEY_ID().equals("Student")) {

                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, StudentDashboardActivity.class));
                return;
            }
        } catch (NullPointerException e) {
            setContentView(R.layout.activity_login);
        }

        edtloginuname = (EditText) findViewById(R.id.edtloginuname);
        edtloginupass = (EditText) findViewById(R.id.edtloginupass);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        txtforgotpassword = (TextView) findViewById(R.id.txtforgotpassword);
        img_logo = (ImageView) findViewById(R.id.img_logo);
        ip = findViewById(R.id.ip);
        ip_chng = findViewById(R.id.chng_ip);
        getip = new Getip(this);

        ip.setVisibility(View.GONE);
        ip_chng.setVisibility(View.GONE);

        ip_chng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPref.setIP(ip.getText().toString());
                finish();
                startActivity(getIntent());
                Toast.makeText(LoginActivity.this, "" + getip.getUrl(), Toast.LENGTH_SHORT).show();
            }
        });

        img_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip.setVisibility(View.VISIBLE);
                ip_chng
                        .setVisibility(View.VISIBLE);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror = 0;
                if (edtloginuname.getText().toString().length() == 0) {
                    edtloginuname.setError("Please Enter User Name");
                    flagforerror = 1;
                }

                if (edtloginupass.getText().toString().length() == 0) {
                    edtloginupass.setError("Please Enter Password");
                    flagforerror = 1;
                }

                if (flagforerror == 0) {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(true);
                    progressDialog.setMessage("Login in progress..");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();

                    checkLOGINstudent();
                }
            }
        });

        txtforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                i.putExtra("Sel","1");
                startActivity(i);
            }
        });

        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);

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

    {

    }

    public void checkLOGINstudent() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    progressDialog.dismiss();
                    String auth = "";
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        auth = jsonObject1.getString("authenticated");
                        Log.e("%:", auth);


                        if (auth.equals("0")) {
                            Toast.makeText(LoginActivity.this, "Verify Account", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            myPref.setCurrent_Sem(jsonObject1.getString("semester"));

                            myPref.setKEY_ID("Student");
                            myPref.setID(jsonObject1.getString("studentid"));
                            myPref.setMobile(jsonObject1.getString("mobno"));
                            myPref.setEmail(jsonObject1.getString("emailid"));
                            myPref.setName(jsonObject1.getString("firstname") + " " + jsonObject1.getString("lastname"));
                            myPref.setPassID(jsonObject1.getString("password"));

                            currSemSubIdArray.add(jsonObject1.getString("subjectid"));
                            currSemSubCodeArray.add(jsonObject1.getString("subject_code"));
                            currSemSubNameArray.add(jsonObject1.getString("subject_name"));
                        }

                    }


                    Log.e("%:", auth);
                    if (auth.equals("0")) {
                        Intent intent3 = new Intent(LoginActivity.this, AuthenticateActivity.class);
                        intent3.putExtra("mobno", edtloginuname.getText().toString());
                        finish();
                        startActivity(intent3);
                    } else if (auth.equals("1")) {


                        if (myPref.saveArray(currSemSubCodeArray.toArray(new String[currSemSubCodeArray.size()]), "SubjectCode"))
                            Log.e("######MYPREF", "SubjectId added in mypref");
                        if (myPref.saveArray(currSemSubIdArray.toArray(new String[currSemSubIdArray.size()]), "SubjectId"))
                            Log.e("######MYPREF", "SubjectId added in mypref");
                        if (myPref.saveArray(currSemSubNameArray.toArray(new String[currSemSubNameArray.size()]), "SubjectName"))
                            Log.e("######MYPREF", "SubjectId added in mypref");


                        Intent intent3 = new Intent(LoginActivity.this, StudentDashboardActivity.class);
                        finish();
                        startActivity(intent3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Username Or Password Is Incorrect", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Error Occured" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("mobno", edtloginuname.getText().toString());
                map.put("pass", edtloginupass.getText().toString());
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }


}
