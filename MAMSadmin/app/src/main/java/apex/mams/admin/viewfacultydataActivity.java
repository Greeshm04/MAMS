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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


public class viewfacultydataActivity extends AppCompatActivity {


    TextView txt_faculty_facultyid, txt_faculty_firstname, txt_faculty_lastname, txt_faculty_dob, txt_faculty_doj, txt_faculty_mobno, txt_faculty_emailid, txt_faculty_address, txt_faculty_city, txt_faculty_status;
    String facultyid="";
    Getip getip;
    Toolbar toolbar;
    View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewfacultydata);



        Intent intent = getIntent();
        facultyid = intent.getStringExtra("id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        txt_faculty_facultyid = (TextView) findViewById(R.id.txt_faculty_facultyid);
        txt_faculty_firstname = (TextView) findViewById(R.id.txt_faculty_firstname);
        txt_faculty_lastname = (TextView) findViewById(R.id.txt_faculty_lastname);
        txt_faculty_dob = (TextView) findViewById(R.id.txt_faculty_dob);
        txt_faculty_doj = (TextView) findViewById(R.id.txt_faculty_doj);
        txt_faculty_mobno = (TextView) findViewById(R.id.txt_faculty_mobno);
        txt_faculty_emailid = (TextView) findViewById(R.id.txt_faculty_emailid);
        txt_faculty_address = (TextView) findViewById(R.id.txt_faculty_address);
        txt_faculty_city = (TextView) findViewById(R.id.txt_faculty_city);
        txt_faculty_status = (TextView) findViewById(R.id.txt_faculty_status);

        getip=new Getip(this);

        parentLayout=findViewById(android.R.id.content);
        fill_faculty_data();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    public void fill_faculty_data() {
        final ProgressDialog progressDialog = new ProgressDialog(viewfacultydataActivity.this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        StringRequest stringRequest=new StringRequest(Request.Method.POST,getip.getUrl()+"faculty_master/select_faculty_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    int i;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (i = 0; i <= jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if (facultyid.equals(jsonObject1.getString("facultyid"))){

                            txt_faculty_firstname.setText("Name:="+jsonObject1.getString("firstname")+" "+jsonObject1.getString("lastname"));
                            txt_faculty_dob.setText("Date Of Birth:="+jsonObject1.getString("dob"));
                            txt_faculty_doj.setText("Date Of Joining:="+jsonObject1.getString("doj"));
                            txt_faculty_mobno.setText("Mobile Number:="+jsonObject1.getString("mobno"));
                            txt_faculty_emailid.setText("Email Id:="+jsonObject1.getString("emailid"));
                            txt_faculty_address.setText("Address:="+jsonObject1.getString("address"));
                            txt_faculty_city.setText("City:="+jsonObject1.getString("city"));
                            txt_faculty_status.setText("Status:="+jsonObject1.getString("status"));
                            progressDialog.dismiss();
                            break;
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
                Snackbar.make(parentLayout,"Error Occured",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }
}
