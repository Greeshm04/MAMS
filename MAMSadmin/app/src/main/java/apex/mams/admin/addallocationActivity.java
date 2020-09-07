package apex.mams.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

public class addallocationActivity extends AppCompatActivity {

    TextView txt_allocate_subject, txt_allocate_faculty;
    Spinner spn_allocate;
    Button btn_update_allocation;
    List<String> lsFaculty = new ArrayList<String>();
    List<String> lsFaculty2 = new ArrayList<String>();
    ArrayAdapter adpFaculty;
    Getip getip;
    int position;
    View parentLayout;
    String firstName, subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addallocation);

        getip = new Getip(this);
        txt_allocate_subject = (TextView) findViewById(R.id.txt_allocation_subject);
        txt_allocate_faculty = (TextView) findViewById(R.id.txt_allocation_faculty);
        spn_allocate = (Spinner) findViewById(R.id.spn_allocate);
        btn_update_allocation = (Button) findViewById(R.id.btn_update_allocation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        subjectId = getIntent().getStringExtra("subjectId");
        txt_allocate_subject.setText(getIntent().getStringExtra("subjectName"));
        txt_allocate_faculty.setText(getIntent().getStringExtra("subjectFaculty"));
        parentLayout = findViewById(android.R.id.content);

        fetch_faculty();

        spn_allocate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_update_allocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = lsFaculty.get(position);
                if (firstName.equals("None")) {
                    firstName = "";
                }
                update_subject_allocation();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void fetch_faculty() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/select_faculty_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    int flagfornotfound = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    lsFaculty.add("None");
                    lsFaculty2.add("None");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        lsFaculty.add(jsonObject1.getString("firstname"));
                        lsFaculty2.add(jsonObject1.getString("firstname")+" "+jsonObject1.getString("lastname"));
                        flagfornotfound = 1;

                    }


                    adpFaculty = new ArrayAdapter(addallocationActivity.this, android.R.layout.simple_list_item_1, lsFaculty2);
                    spn_allocate.setAdapter(adpFaculty);


                    if (flagfornotfound == 0) {
                        Log.e("###########Try", "Data Not Found");
                    } else {
                        Log.e("###########Try", "Success");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentLayout, "ERROR", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void update_subject_allocation() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Allocating Subject..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "subject_allocation_master/update_subject_allocation_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int i = jsonObject.getInt("status");

                    if (i == 1) {
                        Toast.makeText(getApplicationContext(), "Subject Allocation Updated Successfully..", Toast.LENGTH_SHORT).show();
                        Log.e("###########Try", "Success");
                        progressDialog.dismiss();
                        finish();
                    } else if (i == 0) {
                        Log.e("###############Try", "Error");
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subjectId", subjectId);
                map.put("firstName", firstName);


                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
}
