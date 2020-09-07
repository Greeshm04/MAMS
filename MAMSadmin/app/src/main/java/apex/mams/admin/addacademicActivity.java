package apex.mams.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
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

public class addacademicActivity extends AppCompatActivity {


    NumberPicker yearpicker;
    EditText start_date, end_date;
    Button btn_addacademic;
    int year = 2019;
    Getip getip;
    View parentLayout;
    String ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addacademic);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        yearpicker = (NumberPicker) findViewById(R.id.yearpicker);
        start_date = (EditText) findViewById(R.id.start_date);
        end_date = (EditText) findViewById(R.id.end_date);
        btn_addacademic = (Button) findViewById(R.id.btn_addacademic);
        parentLayout = findViewById(android.R.id.content);

        getip = new Getip(this);
        yearpicker.setMinValue(2000);
        yearpicker.setMaxValue(3000);


        ch = getIntent().getStringExtra("academicid");
        final String status = getIntent().getStringExtra("status");
        yearpicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                year = numberPicker.getValue();
            }
        });
        start_date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            int month = i1 + 1;
                            start_date.setText("" + i2 + "-" + month + "-" + i);
                        }
                    };
                    new DatePickerDialog(addacademicActivity.this, mDateSetListener, year, 7, 3).show();
                }
                return false;
            }
        });

        end_date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            int month = i1 + 1;
                            end_date.setText("" + i2 + "-" + month + "-" + i);
                        }
                    };
                    new DatePickerDialog(addacademicActivity.this, mDateSetListener, year + 1, 7, 3).show();
                }
                return false;
            }
        });

        if (status.equals("update")) {
            fetch_academic();
            btn_addacademic.setText("Update");
        }

        btn_addacademic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("insert"))
                    add_academic();
                else if (status.equals("update"))
                    update_academic();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void fetch_academic() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "academic_year_master/select_academic_year.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    int i;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if (ch.equals(jsonObject1.getString("academicid"))) {

                            yearpicker.setValue(Integer.parseInt(jsonObject1.getString("academic_text")));
                            start_date.setText(jsonObject1.getString("start_date"));
                            end_date.setText(jsonObject1.getString("end_date"));

                        }
                    }
                    if (i == jsonArray.length()) {
                        progressDialog.dismiss();
                    }


                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout, "ERROR", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                progressDialog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }


    public void add_academic() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adding Academic Year..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "academic_year_master/add_academic_year.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addacademicActivity.this, "Academic Year added successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    } else if (st == 0) {
                        Toast.makeText(addacademicActivity.this, "Academic Year can't added ", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentLayout, "ERROR", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(parentLayout, "Error Occured", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("academic", String.valueOf(year));
                map.put("start", start_date.getText().toString());
                map.put("end", end_date.getText().toString());
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
    public void update_academic() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Academic Year..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "academic_year_master/update_academic_year.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addacademicActivity.this, "Academic Year Updated successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    } else if (st == 0) {
                        Toast.makeText(addacademicActivity.this, "Academic Year can't Updated ", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentLayout, "ERROR", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(parentLayout, "Error Occured", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("academicid",ch);
                map.put("academic", String.valueOf(year));
                map.put("start", start_date.getText().toString());
                map.put("end", end_date.getText().toString());
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
}
