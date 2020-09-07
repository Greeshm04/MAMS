package apex.mams.faculty;

import android.*;
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
import java.util.List;
import java.util.Map;

public class LoginActivity extends Activity {

    EditText edtloginuname, edtloginupass, ip;
    Button btnlogin;
    Getip getip;
    ProgressDialog progressDialog;
    MyPref myPref;
    ArrayList<String> permissionToRequest = new ArrayList<String>();
    ArrayList<String> permissionToReject = new ArrayList<String>();
    ArrayList<String> permission = new ArrayList<String>();
    final int ALL_PERMISSION_RESULT = 101;
    Button chng_ip;
    TextView txt_forgetpassword;

    ImageView img_logo;

    ArrayList<String> subjectIdArray = new ArrayList<>();
    ArrayList<String> subjectNameArray = new ArrayList<>();
    ArrayList<String> subjectCodeArray = new ArrayList<>();
    ArrayList<String> subjectSemArray = new ArrayList<>();

    ArrayList<String> studentIdArray = new ArrayList<>();
    ArrayList<String> studentNameArray = new ArrayList<>();
    ArrayList<String> studentEnrollArray = new ArrayList<>();
    ArrayList<String> studentSemArray = new ArrayList<>();
    ArrayList<String> SemesterArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPref = new MyPref(LoginActivity.this);

        try {

            if (myPref.getKEY_ID().equals("Faculty")) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, FacultyDashboardActivity.class));
                return;
            }

        } catch (NullPointerException e) {

            setContentView(R.layout.activity_login);

        }

        edtloginuname = (EditText) findViewById(R.id.edtloginuname);
        edtloginupass = (EditText) findViewById(R.id.edtloginupass);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        chng_ip = (Button) findViewById(R.id.chng_ip);
        ip = (EditText) findViewById(R.id.ip);
        txt_forgetpassword = findViewById(R.id.txtforgotpassword);

        img_logo = (ImageView) findViewById(R.id.img_logo);

        ip.setText("192.168.0.");
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

        txt_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                i.putExtra("Sel", "1");
                startActivity(i);
            }
        });

        img_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip.setVisibility(View.VISIBLE);
                chng_ip.setVisibility(View.VISIBLE);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror = 0;
                if (edtloginuname.getText().toString().length() == 0) {
                    edtloginuname.setError("Please Enter Mobile Number");
                    flagforerror = 1;
                }

                if (edtloginupass.getText().toString().length() == 0) {
                    edtloginupass.setError("Please Enter Password");
                    flagforerror = 1;
                }

                if (edtloginuname.getText().toString().length() != 10) {
                    edtloginuname.setError("Mobile Number Is Not Valid");
                    flagforerror = 1;
                }

                if (flagforerror == 0) {

                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(true);
                    progressDialog.setMessage("Login in progress..");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    checkLOGINfaculty();


                }
            }
        });

        permission.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        permission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

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

    public void checkLOGINfaculty() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int flag = 0;
                String auth = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object = jsonObject.getJSONObject("response");

                    auth = object.getString("authenticated");
                    Log.e("%:", auth);
                    if (auth.equals("0")) {
                        Toast.makeText(LoginActivity.this, "Verify Account", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(LoginActivity.this, AuthenticateActivity.class);
                        intent3.putExtra("mobno", edtloginuname.getText().toString());
                        finish();
                        startActivity(intent3);
                    } else if (auth.equals("1")) {


                        myPref.setKEY_ID("Faculty");
                        myPref.setID(object.getString("facultyid"));
                        myPref.setMobile(object.getString("mobno"));
                        myPref.setEmail(object.getString("emailid"));
                        myPref.setPassID(object.getString("password"));
                        myPref.setName(object.getString("firstname") + " " + object.getString("lastname"));


                        try {
                            JSONArray jsonArray = object.getJSONArray("semester");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Log.e("##SEMESTER", jsonObject1.getString("semesterA"));


                                SemesterArray.add(jsonObject1.getString("semesterA"));
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("student");
                                for (int j = 0; j < jsonArray1.length(); j++) {

                                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                    Log.e("##STUDENT", jsonObject2.getString("enrollmentno"));
                                    studentEnrollArray.add(jsonObject2.getString("enrollmentno"));
                                    studentIdArray.add(jsonObject2.getString("studentid"));
                                    studentNameArray.add(jsonObject2.getString("firstname") + " " + jsonObject2.getString("lastname"));
                                    studentSemArray.add(jsonObject2.getString("semester"));

                                }

                            }

                        } catch (Exception e) {
                            progressDialog.dismiss();

                        }

                        try {

                            JSONArray jsonArray2 = object.getJSONArray("subject");
                            for (int k = 0; k < jsonArray2.length(); k++) {
                                JSONObject jsonObject3 = jsonArray2.getJSONObject(k);
                                Log.e("##SUBJECT", jsonObject3.getString("subject_name"));

                                subjectNameArray.add(jsonObject3.getString("subject_name"));
                                subjectIdArray.add(jsonObject3.getString("subjectid"));
                                subjectCodeArray.add(jsonObject3.getString("subject_code"));
                                subjectSemArray.add(jsonObject3.getString("subject_sem"));


                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();

                        }


                        progressDialog.dismiss();


                        if (myPref.saveArray(SemesterArray.toArray(new String[SemesterArray.size()]), "MainSemester"))
                            Log.e("######MYPREF", "MAin Semester added in mypref");


                        if (myPref.saveArray(subjectIdArray.toArray(new String[subjectIdArray.size()]), "SubjectId"))
                            Log.e("######MYPREF", "SubjectId added in mypref");
                        if (myPref.saveArray(subjectCodeArray.toArray(new String[subjectCodeArray.size()]), "SubjectCode"))
                            Log.e("######MYPREF", "SubjectCode added in mypref");
                        if (myPref.saveArray(subjectNameArray.toArray(new String[subjectNameArray.size()]), "SubjectName"))
                            Log.e("######MYPREF", "SubjectName added in mypref");
                        if (myPref.saveArray(subjectSemArray.toArray(new String[subjectSemArray.size()]), "SubjectSem"))
                            Log.e("######MYPREF", "SubjectSem added in mypref");

                        if (myPref.saveArray(studentIdArray.toArray(new String[studentIdArray.size()]), "StudentId"))
                            Log.e("######MYPREF", "StudentId added in mypref");
                        if (myPref.saveArray(studentEnrollArray.toArray(new String[studentEnrollArray.size()]), "StudentEnroll"))
                            Log.e("######MYPREF", "StudentEnroll added in mypref");
                        if (myPref.saveArray(studentNameArray.toArray(new String[studentNameArray.size()]), "StudentName"))
                            Log.e("######MYPREF", "StudentName added in mypref");
                        if (myPref.saveArray(studentSemArray.toArray(new String[studentSemArray.size()]), "StudentSem"))
                            Log.e("######MYPREF", "StudentSem added in mypref");


                        Intent intent3 = new Intent(LoginActivity.this, FacultyDashboardActivity.class);
                        finish();
                        startActivity(intent3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage());
                    Toast.makeText(LoginActivity.this, "Username Or Password Is Incorrect", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Error Occured  " + error, Toast.LENGTH_SHORT).show();
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
