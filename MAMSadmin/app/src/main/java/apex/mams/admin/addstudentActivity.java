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

public class addstudentActivity extends AppCompatActivity {

    EditText edt_add_student_enrollmentno, edt_add_student_firstname, edt_add_student_lastname, edt_add_student_dob, edt_add_student_mobno, edt_add_student_emailid, edt_add_student_address, edt_add_student_city, edt_add_student_doj, edt_add_student_dol, edt_add_student_semester, edt_add_student_academicid, edt_add_student_password, edt_add_student_cpassword, edt_add_student_status;
    Button btn_add_student_add, btn_prev_1, btn_prev_2, btn_prev_3, btn_next_1, btn_next_2, btn_next_3;
    Getip getip;
    String ch, strpassword, rdstatus;
    LinearLayout lin_lay_1, lin_lay_2, lin_lay_3, lin_lay_4,lin_lay_stud1,lin_lay_stud2,lin_lay_stud3;
    String academicid;
    View parentLayout;
    Switch swi_status;
    int a = 2017, a1 = 2, a2 = 21, ja = 2017, ja1 = 2, ja2 = 21, la = 2017, la1 = 2, la2 = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstudent);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        edt_add_student_enrollmentno = (EditText) findViewById(R.id.edt_add_student_enrollmentno);
        edt_add_student_firstname = (EditText) findViewById(R.id.edt_add_student_firstname);
        edt_add_student_lastname = (EditText) findViewById(R.id.edt_add_student_lastname);
        edt_add_student_dob = (EditText) findViewById(R.id.edt_add_student_dob);
        edt_add_student_mobno = (EditText) findViewById(R.id.edt_add_student_mobno);
        edt_add_student_emailid = (EditText) findViewById(R.id.edt_add_student_emailid);
        edt_add_student_address = (EditText) findViewById(R.id.edt_add_student_address);
        edt_add_student_city = (EditText) findViewById(R.id.edt_add_student_city);
        edt_add_student_doj = (EditText) findViewById(R.id.edt_add_student_doj);
        edt_add_student_dol = (EditText) findViewById(R.id.edt_add_student_dol);
        edt_add_student_semester = (EditText) findViewById(R.id.edt_add_student_semester);
        edt_add_student_password = (EditText) findViewById(R.id.edt_add_student_password);
        edt_add_student_cpassword = (EditText) findViewById(R.id.edt_add_student_cpassword);
        btn_add_student_add = (Button) findViewById(R.id.btn_add_student_add);
        swi_status= (Switch) findViewById(R.id.swi_status);

        lin_lay_1 = (LinearLayout) findViewById(R.id.lin_lay_1);
        lin_lay_2 = (LinearLayout) findViewById(R.id.lin_lay_2);
        lin_lay_3 = (LinearLayout) findViewById(R.id.lin_lay_3);
        lin_lay_4 = (LinearLayout) findViewById(R.id.lin_lay_4);
        lin_lay_stud1= (LinearLayout) findViewById(R.id.lin_lay_stud1);
        lin_lay_stud2= (LinearLayout) findViewById(R.id.lin_lay_stud2);
        lin_lay_stud3= (LinearLayout) findViewById(R.id.lin_lay_stud3);

        btn_next_1 = (Button) findViewById(R.id.btn_next_1);
        btn_next_2 = (Button) findViewById(R.id.btn_next_2);
        btn_next_3 = (Button) findViewById(R.id.btn_next_3);

        btn_prev_1 = (Button) findViewById(R.id.btn_prev_1);
        btn_prev_2 = (Button) findViewById(R.id.btn_prev_2);
        btn_prev_3 = (Button) findViewById(R.id.btn_prev_3);

        lin_lay_1.setVisibility(View.VISIBLE);
        lin_lay_2.setVisibility(View.GONE);
        lin_lay_3.setVisibility(View.GONE);
        lin_lay_4.setVisibility(View.GONE);
        lin_lay_stud1.setVisibility(View.GONE);
        lin_lay_stud2.setVisibility(View.GONE);
        lin_lay_stud3.setVisibility(View.GONE);



        swi_status.setChecked(true);
        getip = new Getip(this);

        parentLayout=findViewById(android.R.id.content);

        ch = getIntent().getStringExtra("studentsid");
        final String status = getIntent().getStringExtra("status");




        edt_add_student_dob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            a = i;
                            a1 = i1;
                            a2 = i2;
                            int month = a1 + 1;
                            edt_add_student_dob.setText("" + a2 + "-" + month + "-" + a);
                        }
                    };

                    new DatePickerDialog(addstudentActivity.this, mDateSetListener, a, a1, a2).show();
                }

                return false;
            }
        });

        edt_add_student_doj.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            ja = i;
                            ja1 = i1;
                            ja2 = i2;
                            int month = ja1 + 1;
                            edt_add_student_doj.setText("" + ja2 + "-" + month + "-" + ja);
                        }
                    };

                    new DatePickerDialog(addstudentActivity.this, mDateSetListener, ja, ja1, ja2).show();
                }

                return false;
            }
        });

        edt_add_student_dol.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            la=i;
                            la1=i1;
                            la2=i2;
                            int month=la1+1;
                            edt_add_student_dol.setText("" + la2 + "-" + month + "-" + la);
                        }
                    };

                    new DatePickerDialog(addstudentActivity.this, mDateSetListener, la, la1, la2).show();
                }
                return false;
            }
        });
        if (status.equals("update")) {
            fetch_student_master();
            btn_add_student_add.setText("Update");
        }
        btn_next_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror = 0;
                if (edt_add_student_firstname.getText().toString().equals("")) {
                    edt_add_student_firstname.setError("Please Enter First Name");
                    flagforerror = 1;
                }
                if (edt_add_student_lastname.getText().toString().equals("")) {
                    edt_add_student_lastname.setError("Please Enter Last NAme");
                    flagforerror = 1;
                }
                if (edt_add_student_dob.getText().toString().equals("")) {
                    edt_add_student_dob.setError("Please Enter Date Of Birth");
                    flagforerror = 1;
                }
                if (flagforerror == 0) {
                    lin_lay_1.setVisibility(View.GONE);
                    lin_lay_2.setVisibility(View.VISIBLE);
                    lin_lay_stud1.setVisibility(View.VISIBLE);
                    btn_next_1.setVisibility(View.GONE);
                }
            }
        });
        btn_next_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror = 0;
                if (edt_add_student_mobno.getText().toString().equals("")) {
                    edt_add_student_mobno.setError("Please Enter Mobile Number");
                    flagforerror = 1;
                }
                if (edt_add_student_emailid.getText().toString().equals("")) {
                    edt_add_student_emailid.setError("Please Enter Email address");
                    flagforerror = 1;
                }

                if (!isValidEmaillId(edt_add_student_emailid.getText().toString()))
                {
                    edt_add_student_emailid.setError("Invalid Email Address");
                    flagforerror=1;
                }
                if (edt_add_student_address.getText().toString().equals("")) {
                    edt_add_student_address.setError("Please Enter address");
                    flagforerror = 1;
                }
                if (edt_add_student_city.getText().toString().equals("")) {
                    edt_add_student_city.setError("Please Enter City");
                    flagforerror = 1;
                }
                if (flagforerror == 0) {
                    lin_lay_2.setVisibility(View.GONE);
                    lin_lay_3.setVisibility(View.VISIBLE);
                    lin_lay_stud2.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_next_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror = 0;
                if (edt_add_student_doj.getText().toString().equals("")) {
                    edt_add_student_doj.setError("Please Enter Date Of Joining");
                    flagforerror = 1;
                }
                if (edt_add_student_semester.getText().toString().equals("")) {
                    edt_add_student_semester.setError("Please Enter Semester");
                    flagforerror = 1;
                }
                if (flagforerror == 0) {
                    lin_lay_3.setVisibility(View.GONE);
                    lin_lay_4.setVisibility(View.VISIBLE);
                    lin_lay_stud3.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_prev_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_lay_1.setVisibility(View.VISIBLE);
                lin_lay_2.setVisibility(View.GONE);
                lin_lay_stud1.setVisibility(View.GONE);
                btn_next_1.setVisibility(View.VISIBLE);

            }
        });
        btn_prev_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_lay_2.setVisibility(View.VISIBLE);
                lin_lay_3.setVisibility(View.GONE);
                lin_lay_stud2.setVisibility(View.GONE);
                lin_lay_stud1.setVisibility(View.VISIBLE);
            }
        });
        btn_prev_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_lay_3.setVisibility(View.VISIBLE);
                lin_lay_4.setVisibility(View.GONE);
                lin_lay_stud3.setVisibility(View.GONE);
                lin_lay_stud2.setVisibility(View.VISIBLE);
            }
        });
        btn_add_student_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror = 0;


                if (status.equals("insert")) {


                    if (edt_add_student_password.getText().toString().equals("")) {
                        edt_add_student_password.setError("Please Enter Password");
                        flagforerror = 1;
                    }
                    if (edt_add_student_cpassword.getText().toString().equals("")) {
                        edt_add_student_cpassword.setError("Please Confirm Password");
                        flagforerror = 1;
                    }

                }

                if (!edt_add_student_password.getText().toString().equals(edt_add_student_cpassword.getText().toString())) {
                    edt_add_student_cpassword.setError("Password Mismatch");
                    flagforerror = 1;
                }


                if (swi_status.isChecked())
                    rdstatus="Active";
                else
                    rdstatus="Deactive";

                if (flagforerror == 0) {


                    if (status.equals("update")) {

                        Toast.makeText(addstudentActivity.this, "CHHH"+ja, Toast.LENGTH_SHORT).show();
                        add_academic_master(0);
                        finish();

                    } else if (status.equals("insert")) {

                        add_academic_master(1);
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
        return super.onSupportNavigateUp();
    }

    public void add_academic_master(final int a) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Inserting..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "academic_year_master/insert_academic_year.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("response");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        academicid=jsonObject1.getString("academicid");
                    }
                    progressDialog.dismiss();

                    if (a==0)
                    {
                        update_student_master();
                    }
                    else if (a==1) {

                        add_student_master();
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
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("academic_text", String.valueOf(ja));
                map.put("start_date", edt_add_student_doj.getText().toString());
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void add_student_master() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating An Account..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/insert_student_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addstudentActivity.this, "Student Account Created Successfully", Toast.LENGTH_SHORT).show();
                        edt_add_student_enrollmentno.setText("");
                        edt_add_student_firstname.setText("");
                        edt_add_student_lastname.setText("");
                        edt_add_student_dob.setText("");
                        edt_add_student_mobno.setText("");
                        edt_add_student_emailid.setText("");
                        edt_add_student_address.setText("");
                        edt_add_student_city.setText("");
                        edt_add_student_doj.setText("");
                        edt_add_student_dol.setText("");
                        edt_add_student_semester.setText("");
                        edt_add_student_password.setText("");
                        edt_add_student_cpassword.setText("");
                        progressDialog.dismiss();
                    } else if (st == 0) {
                        Toast.makeText(addstudentActivity.this, "Student Account can't Created ", Toast.LENGTH_SHORT).show();
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
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("enrollmentno", edt_add_student_enrollmentno.getText().toString());
                map.put("firstname", edt_add_student_firstname.getText().toString());
                map.put("lastname", edt_add_student_lastname.getText().toString());
                map.put("dob", edt_add_student_dob.getText().toString());
                map.put("mobno", edt_add_student_mobno.getText().toString());
                map.put("emailid", edt_add_student_emailid.getText().toString());
                map.put("address", edt_add_student_address.getText().toString());
                map.put("city", edt_add_student_city.getText().toString());
                map.put("doj", edt_add_student_doj.getText().toString());
                map.put("dol", edt_add_student_dol.getText().toString());
                map.put("semester", edt_add_student_semester.getText().toString());
                map.put("academicid", String.valueOf(academicid));
                map.put("password", edt_add_student_password.getText().toString());
                map.put("status", rdstatus);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void fetch_student_master() {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching DATA...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/select_student_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    int flagfornotfound = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (ch.equals(jsonObject1.getString("studentid"))) {


                            edt_add_student_enrollmentno.setText(jsonObject1.getString("enrollmentno"));
                            edt_add_student_firstname.setText(jsonObject1.getString("firstname"));
                            edt_add_student_lastname.setText(jsonObject1.getString("lastname"));
                            edt_add_student_dob.setText(jsonObject1.getString("dob"));
                            edt_add_student_mobno.setText(jsonObject1.getString("mobno"));
                            edt_add_student_emailid.setText(jsonObject1.getString("emailid"));
                            edt_add_student_address.setText(jsonObject1.getString("address"));
                            edt_add_student_city.setText(jsonObject1.getString("city"));
                            edt_add_student_doj.setText(jsonObject1.getString("doj"));
                            edt_add_student_dol.setText(jsonObject1.getString("dol"));
                            edt_add_student_semester.setText(jsonObject1.getString("semester"));
                            academicid=jsonObject1.getString("academicid");
                            ja= Integer.parseInt(jsonObject1.getString("academic_text"));


                            if (jsonObject1.getString("status").equals("Active"))
                                swi_status.setChecked(true);
                            else
                                swi_status.setChecked(false);


                            strpassword = jsonObject1.getString("password");
                            flagfornotfound = 1;
                            progressDialog.dismiss();
                            break;
                        } else {
                            flagfornotfound = 0;
                        }
                    }


                    if (flagfornotfound == 0) {
                        Toast.makeText(addstudentActivity.this, "Might DATA Not Found", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(addstudentActivity.this, "Successfully Fetched", Toast.LENGTH_SHORT).show();
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

    public void update_student_master() {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating An Account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/update_student_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addstudentActivity.this, "Student Account Updated Successfully", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    } else if (st == 0) {
                        Toast.makeText(addstudentActivity.this, "Sstudent Account can't Updated ", Toast.LENGTH_SHORT).show();
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("studentid", ch);
                map.put("enrollmentno", edt_add_student_enrollmentno.getText().toString());
                map.put("firstname", edt_add_student_firstname.getText().toString());
                map.put("lastname", edt_add_student_lastname.getText().toString());
                map.put("dob", edt_add_student_dob.getText().toString());
                map.put("mobno", edt_add_student_mobno.getText().toString());
                map.put("emailid", edt_add_student_emailid.getText().toString());
                map.put("address", edt_add_student_address.getText().toString());
                map.put("city", edt_add_student_city.getText().toString());
                map.put("doj", edt_add_student_doj.getText().toString());
                map.put("dol", edt_add_student_dol.getText().toString());
                map.put("semester", edt_add_student_semester.getText().toString());
                map.put("academicid", academicid);

                if (edt_add_student_password.getText().toString().equals("")) {
                    map.put("password", strpassword);

                } else {
                    map.put("password", edt_add_student_password.getText().toString());

                }

                map.put("status", rdstatus);
                return map;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
}
