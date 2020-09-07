package apex.mams.admin;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class addsubjectActivity extends AppCompatActivity {

    Getip getip;
    EditText edt_add_subjectcode,edt_add_subjectname,edt_add_subjectsem;
    Button btn_subject_add;
    String ch,status;
    View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsubject);
        edt_add_subjectcode= (EditText) findViewById(R.id.edt_add_subjectcode);
        edt_add_subjectname= (EditText) findViewById(R.id.edt_add_subjectname);
        edt_add_subjectsem= (EditText) findViewById(R.id.edt_add_subjectsem);
        btn_subject_add= (Button) findViewById(R.id.btn_subject_add);

        getip=new Getip(this);
        parentLayout=findViewById(android.R.id.content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ch=getIntent().getStringExtra("subjectsid");
        status=getIntent().getStringExtra("status");

        if (status.equals("update"))
        {

            fetch_subject();
            btn_subject_add.setText("Update");
        }
        btn_subject_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VolleyApp.IsNotEmpty(new EditText[]{edt_add_subjectcode,edt_add_subjectname,edt_add_subjectsem}))
                {
                    if (status.equals("update"))
                    {
                        update_subject_master();
                    }
                    else if (status.equals("insert"))
                    {
                        add_subject_master();
                    }
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    public void add_subject_master()
    {
        final ProgressDialog progressDialog=new ProgressDialog(addsubjectActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Inserting Subject..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "subject_master/insert_subject_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int i=jsonObject.getInt("status");

                    if(i==1)
                    {
                        Toast.makeText(getApplicationContext(), "Subject Added Successfully..", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        add_allocation_null();

                    }
                    else if(i==0)
                    {
                        Toast.makeText(getApplicationContext(), "Subject can't Added", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentLayout,"ERROR",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout,"Error Occured",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<String,String>();
                map.put("subject_code",edt_add_subjectcode.getText().toString());
                map.put("subject_name",edt_add_subjectname.getText().toString());
                map.put("subject_sem",edt_add_subjectsem.getText().toString());


                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
    public void add_allocation_null()
    {
        final ProgressDialog progressDialog=new ProgressDialog(addsubjectActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Inserting Subject Alloaction..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "subject_allocation_master/insert_subject_allocation_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int i=jsonObject.getInt("status");

                    if(i==1)
                    {
                        progressDialog.dismiss();

                        finish();

                    }
                    else if(i==0)
                    {
                        Toast.makeText(getApplicationContext(), "Subject can't Allocate", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentLayout,"ERROR",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                    Log.e("###################",e.toString());
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout,"Error Occured",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

                progressDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<String,String>();
                map.put("subjectName",edt_add_subjectname.getText().toString());
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
    public void fetch_subject()
    {


        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "subject_master/select_subject_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    int i=0;
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("response");
                    for (i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                        if (jsonObject1.getString("subjectid").equals(ch))
                        {
                            edt_add_subjectcode.setText(jsonObject1.getString("subject_code"));
                            edt_add_subjectname.setText(jsonObject1.getString("subject_name"));
                            edt_add_subjectsem.setText(jsonObject1.getString("subject_sem"));


                        }

                    }

                    if (i==jsonArray.length())
                    {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentLayout,"ERROR",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout,"Error Occured",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
    public void update_subject_master()
    {
        final ProgressDialog progressDialog=new ProgressDialog(addsubjectActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Subject..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "subject_master/update_subject_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int i=jsonObject.getInt("status");

                    if(i==1)
                    {
                        Toast.makeText(getApplicationContext(), "Subject Updated Successfully..", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                    else if(i==0)
                    {
                        Toast.makeText(getApplicationContext(), "Subject can't Updated", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentLayout,"ERROR",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout,"Error Occured",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<String,String>();
                map.put("subjectid",ch);
                map.put("subject_code",edt_add_subjectcode.getText().toString());
                map.put("subject_name",edt_add_subjectname.getText().toString());
                map.put("subject_sem",edt_add_subjectsem.getText().toString());


                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

}
