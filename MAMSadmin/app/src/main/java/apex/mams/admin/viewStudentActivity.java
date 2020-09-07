package apex.mams.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class viewStudentActivity extends AppCompatActivity {
    Getip getip;
    String studentid = "";
    TextView txt_enno,txt_name,txt_semester,txt_dob,txt_mobno,txt_email,txt_address,txt_city,txt_doj,txt_academicid,txt_status;
    Toolbar toolbar;
    View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);
        getip = new Getip(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_enno= (TextView) findViewById(R.id.txt_enno);
        txt_name= (TextView) findViewById(R.id.txt_name);
        txt_semester= (TextView) findViewById(R.id.txt_semester);
        txt_dob= (TextView) findViewById(R.id.txt_dob);
        txt_mobno= (TextView) findViewById(R.id.txt_mobno);
        txt_email= (TextView) findViewById(R.id.txt_email);
        txt_address= (TextView) findViewById(R.id.txt_address);
        txt_city= (TextView) findViewById(R.id.txt_city);
        txt_doj= (TextView) findViewById(R.id.txt_doj);
        txt_academicid= (TextView) findViewById(R.id.txt_academicid);
        txt_status= (TextView) findViewById(R.id.txt_status);

        parentLayout=findViewById(android.R.id.content);
        Intent intent = getIntent();
        studentid = intent.getStringExtra("id");

        fill_student();
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void fill_student() {

        final ProgressDialog progressDialog = new ProgressDialog(viewStudentActivity.this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/select_student_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int i = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (studentid.equals(jsonObject1.getString("studentid"))) {


                            txt_enno.setText("Enrollment No = "+jsonObject1.getString("enrollmentno"));
                            txt_name.setText("Name = "+jsonObject1.getString("firstname")+ " " + jsonObject1.getString("lastname"));
                            txt_semester.setText("semester = "+jsonObject1.getString("semester"));
                            txt_dob.setText("dob = "+jsonObject1.getString("dob"));
                            txt_mobno.setText("mobno = "+jsonObject1.getString("mobno"));
                            txt_email.setText("email = "+jsonObject1.getString("emailid"));
                            txt_address.setText("address = "+jsonObject1.getString("address"));
                            txt_city.setText("city = "+jsonObject1.getString("city"));
                            txt_doj.setText("doj = "+jsonObject1.getString("doj"));
                            txt_academicid.setText("academicid = "+jsonObject1.getString("academicid"));
                            txt_status.setText("status"+jsonObject1.getString("status"));
                        }

                    }


                    if (i == jsonArray.length()) {
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Snackbar.make(parentLayout,"ERROR",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(parentLayout,"Error Occured",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
}
