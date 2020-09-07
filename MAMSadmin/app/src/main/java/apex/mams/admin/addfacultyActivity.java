package apex.mams.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class addfacultyActivity extends AppCompatActivity {

    EditText edt_add_faculty_firstname,edt_add_faculty_lastname,edt_add_faculty_dob,edt_add_faculty_doj,edt_add_faculty_mobno,edt_add_faculty_emailid,edt_add_faculty_address,edt_add_faculty_city,edt_add_faculty_password,edt_add_faculty_cpassword,edt_add_faculty_status;
    Button btn_add_faculty_add;
    Switch swi_status;
    LinearLayout lin_lay_1,lin_lay_2,lin_lay_3,lin_lay_btn2,lin_lay_btn3;
    Button btn_prev_1,btn_prev_2,btn_next_1,btn_next_2;
    Getip getip;
    String ch,strFpassword,rdstatus;
    int a=2017,a1=2,a2=21,ja=2017,ja1=2,ja2=21;
    View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfaculty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        edt_add_faculty_firstname= (EditText) findViewById(R.id.edt_add_faculty_firstname);
        edt_add_faculty_lastname= (EditText) findViewById(R.id.edt_add_faculty_lastname);
        edt_add_faculty_dob= (EditText) findViewById(R.id.edt_add_faculty_dob);
        edt_add_faculty_doj= (EditText) findViewById(R.id.edt_add_faculty_doj);
        edt_add_faculty_mobno= (EditText) findViewById(R.id.edt_add_faculty_mobno);
        edt_add_faculty_emailid= (EditText) findViewById(R.id.edt_add_faculty_emailid);
        edt_add_faculty_address= (EditText) findViewById(R.id.edt_add_faculty_address);
        edt_add_faculty_city= (EditText) findViewById(R.id.edt_add_faculty_city);
        edt_add_faculty_password= (EditText) findViewById(R.id.edt_add_faculty_password);
        edt_add_faculty_cpassword= (EditText) findViewById(R.id.edt_add_faculty_cpassword);
        btn_add_faculty_add= (Button) findViewById(R.id.btn_add_faculty_add);
        swi_status= (Switch) findViewById(R.id.swi_status);
        btn_prev_1= (Button) findViewById(R.id.btn_prev_1);
        btn_prev_2= (Button) findViewById(R.id.btn_prev_2);
        btn_next_1= (Button) findViewById(R.id.btn_next_1);
        btn_next_2= (Button) findViewById(R.id.btn_next_2);
        lin_lay_1= (LinearLayout) findViewById(R.id.lin_lay_1);
        lin_lay_2= (LinearLayout) findViewById(R.id.lin_lay_2);
        lin_lay_3= (LinearLayout) findViewById(R.id.lin_lay_3);
        lin_lay_btn2= (LinearLayout) findViewById(R.id.lin_lay_btn2);
        lin_lay_btn3= (LinearLayout) findViewById(R.id.lin_lay_btn3);

        getip=new Getip(this);

        parentLayout=findViewById(android.R.id.content);
        lin_lay_2.setVisibility(View.GONE);
        lin_lay_3.setVisibility(View.GONE);
        lin_lay_btn2.setVisibility(View.GONE);
        lin_lay_btn3.setVisibility(View.GONE);



        swi_status.setChecked(true);
        ch=getIntent().getStringExtra("facultysid");
        final String status=getIntent().getStringExtra("status");


        edt_add_faculty_dob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            a=i;
                            a1=i1;
                            a2=i2;
                            int month=a1+1;
                            edt_add_faculty_dob.setText("" + a2 + "-" + month + "-" + a);
                        }
                    };

                    new DatePickerDialog(addfacultyActivity.this, mDateSetListener, a, a1, a2).show();
                }
                return false;
            }
        });

        edt_add_faculty_doj.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            ja=i;
                            ja1=i1;
                            ja2=i2;
                            int month=ja1+1;
                            edt_add_faculty_doj.setText("" + ja2 + "-" + month + "-" + ja);
                        }
                    };

                    new DatePickerDialog(addfacultyActivity.this, mDateSetListener, ja, ja1, ja2).show();
                }
                return false;
            }
        });
        if (status.equals("update"))
        {
            fetch_faculty_master();
            btn_add_faculty_add.setText("Update");
        }
        btn_next_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror=0;



                if (edt_add_faculty_firstname.getText().toString().equals(""))
                {
                    edt_add_faculty_firstname.setError("Please Enter First Name");
                    flagforerror=1;
                }

                if (edt_add_faculty_lastname.getText().toString().equals(""))
                {
                    edt_add_faculty_lastname.setError("Please Enter Last Name");
                    flagforerror=1;
                }

                if (edt_add_faculty_dob.getText().toString().equals(""))
                {
                    edt_add_faculty_dob.setError("Please Enter Date Of Birth");
                    flagforerror=1;
                }


                if (edt_add_faculty_doj.getText().toString().equals(""))
                {
                    edt_add_faculty_doj.setError("Please Enter Date Of Joining");
                    flagforerror=1;
                }
                if (flagforerror==0)
                {
                    lin_lay_1.setVisibility(View.GONE);
                    lin_lay_2.setVisibility(View.VISIBLE);
                    lin_lay_btn2.setVisibility(View.VISIBLE);
                    btn_next_1.setVisibility(View.GONE);
                }

            }
        });

        btn_prev_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_lay_2.setVisibility(View.GONE);
                lin_lay_1.setVisibility(View.VISIBLE);
                lin_lay_btn2.setVisibility(View.GONE);
                btn_next_1.setVisibility(View.VISIBLE);
            }
        });

        btn_next_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror=0;

                if (edt_add_faculty_mobno.getText().toString().equals(""))
                {
                    edt_add_faculty_mobno.setError("Please Enter Mobile Number");
                    flagforerror=1;
                }


                if (edt_add_faculty_emailid.getText().toString().equals(""))
                {
                    edt_add_faculty_emailid.setError("Please Enter Email Id");
                    flagforerror=1;
                }

                if (!isValidEmaillId(edt_add_faculty_emailid.getText().toString()))
                {
                    edt_add_faculty_emailid.setError("Invalid Email Address");
                    flagforerror=1;
                }

                if (edt_add_faculty_address.getText().toString().equals(""))
                {
                    edt_add_faculty_address.setError("Please Enter Address");
                    flagforerror=1;
                }

                if (edt_add_faculty_city.getText().toString().equals(""))
                {
                    edt_add_faculty_city.setError("Please Enter city");
                    flagforerror=1;
                }
                if (flagforerror==0)
                {

                    lin_lay_2.setVisibility(View.GONE);
                    lin_lay_3.setVisibility(View.VISIBLE);
                    lin_lay_btn2.setVisibility(View.GONE);
                    lin_lay_btn3.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_prev_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_lay_3.setVisibility(View.GONE);
                lin_lay_2.setVisibility(View.VISIBLE);
                lin_lay_btn3.setVisibility(View.GONE);
                lin_lay_btn2.setVisibility(View.VISIBLE);
            }
        });

        btn_add_faculty_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror=0;
                if (status.equals("insert"))
                {
                    if (edt_add_faculty_password.getText().toString().equals(""))
                    {
                        edt_add_faculty_password.setError("Please Enter Password");
                        flagforerror=1;
                    }
                    if (edt_add_faculty_cpassword.getText().toString().equals("")) {
                        edt_add_faculty_cpassword.setError("Please Confirm Password");
                        flagforerror = 1;
                    }
                }


                if (!edt_add_faculty_password.getText().toString().equals(edt_add_faculty_cpassword.getText().toString()))
                {
                    flagforerror=1;
                    edt_add_faculty_cpassword.setError("Password Mismatch");
                }

                if (swi_status.isChecked())
                    rdstatus="Active";
                else
                    rdstatus="Deactive";


                if (flagforerror==0)
                {


                    if (status.equals("update"))
                    {
                        update_faculty_master();
                        finish();

                    }
                    else if (status.equals("insert"))
                    {
                        add_faculty_master();
                        finish();

                    }
                }
            }
        });
    }

    public boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    public void add_faculty_master()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating An Account..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/insert_faculty_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject=new JSONObject(response);
                    int st=jsonObject.getInt("status");

                    if (st==1)
                    {
                        Toast.makeText(addfacultyActivity.this, "Faculty Account Created Successfully", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        finish();
                    }
                    else if (st==0)
                    {
                        Toast.makeText(addfacultyActivity.this, "Faculty Account can't Created ", Toast.LENGTH_SHORT).show();
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
                progressDialog.dismiss();
                Snackbar.make(parentLayout,"Error Occured",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<String, String>();
                map.put("firstname",edt_add_faculty_firstname.getText().toString());
                map.put("lastname",edt_add_faculty_lastname.getText().toString());
                map.put("dob",edt_add_faculty_dob.getText().toString());
                map.put("doj",edt_add_faculty_doj.getText().toString());
                map.put("mobno",edt_add_faculty_mobno.getText().toString());
                map.put("emailid",edt_add_faculty_emailid.getText().toString());
                map.put("address",edt_add_faculty_address.getText().toString());
                map.put("city",edt_add_faculty_city.getText().toString());
                map.put("password",edt_add_faculty_password.getText().toString());
                map.put("status",rdstatus);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void fetch_faculty_master()
    {


        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching DATA...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/select_faculty_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    int flagfornotfound=0;
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("response");

                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                        if (ch.equals(jsonObject1.getString("facultyid")))
                        {


                            edt_add_faculty_firstname.setText(jsonObject1.getString("firstname"));
                            edt_add_faculty_lastname.setText(jsonObject1.getString("lastname"));
                            edt_add_faculty_dob.setText(jsonObject1.getString("dob"));
                            edt_add_faculty_doj.setText(jsonObject1.getString("doj"));
                            edt_add_faculty_mobno.setText(jsonObject1.getString("mobno"));
                            edt_add_faculty_emailid.setText(jsonObject1.getString("emailid"));
                            edt_add_faculty_address.setText(jsonObject1.getString("address"));
                            edt_add_faculty_city.setText(jsonObject1.getString("city"));

                            if (jsonObject1.getString("status").equals("Active"))
                                swi_status.setChecked(true);
                            else
                                swi_status.setChecked(false);



                            strFpassword=jsonObject1.getString("password");
                            flagfornotfound=1;
                            progressDialog.dismiss();
                            break;
                        }
                        else
                        {
                            flagfornotfound=0;
                        }
                    }


                    if (flagfornotfound==0)
                    {
                        Toast.makeText(addfacultyActivity.this, "Might DATA Not Found", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else
                    {
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
                progressDialog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void update_faculty_master()
    {


        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating An Account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/update_faculty_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject=new JSONObject(response);
                    int st=jsonObject.getInt("status");

                    if (st==1)
                    {
                        Toast.makeText(addfacultyActivity.this, "Faculty Account Updated Successfully", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        finish();
                    }
                    else if (st==0)
                    {
                        Toast.makeText(addfacultyActivity.this, "Faculty Account can't Updated ", Toast.LENGTH_SHORT).show();
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
                progressDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<String, String>();
                map.put("facultyid",ch);
                map.put("firstname",edt_add_faculty_firstname.getText().toString());
                map.put("lastname",edt_add_faculty_lastname.getText().toString());
                map.put("dob",edt_add_faculty_dob.getText().toString());
                map.put("doj",edt_add_faculty_doj.getText().toString());
                map.put("mobno",edt_add_faculty_mobno.getText().toString());
                map.put("emailid",edt_add_faculty_emailid.getText().toString());
                map.put("address",edt_add_faculty_address.getText().toString());
                map.put("city",edt_add_faculty_city.getText().toString());
                if (edt_add_faculty_password.getText().toString().equals(""))
                {
                    map.put("password", strFpassword);

                }
                else
                {
                    map.put("password", edt_add_faculty_password.getText().toString());

                }

                map.put("status",rdstatus);
                return map;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }


}
