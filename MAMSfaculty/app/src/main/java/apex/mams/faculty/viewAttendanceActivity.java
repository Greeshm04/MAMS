package apex.mams.faculty;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class viewAttendanceActivity extends AppCompatActivity {

    RecyclerView list_Student;
    MyPref myPref;
    Getip getip;
    int totalStudent;
    List<HashMap<String, String>> lsStudent = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myPref = new MyPref(getApplicationContext());
        getip = new Getip(getApplicationContext());
        list_Student = findViewById(R.id.list_Student);


        fetch_Student();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void fetch_Student() {
        final ProgressDialog progressDialog = new ProgressDialog(viewAttendanceActivity.this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        lsStudent.clear();
        list_Student.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/select_attendance_student.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    int i;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("studentName", jsonObject1.getString("studentName"));
                        map.put("studentEnroll", jsonObject1.getString("studentEnroll"));
                        map.put("attendanceStatus", jsonObject1.getString("attendanceStatus"));
                        map.put("srNo", String.valueOf(i + 1));

                        lsStudent.add(map);
                        list_Student.setAdapter(new CustomAdpForViewAttendance(getApplicationContext(), lsStudent));

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
                Toast.makeText(viewAttendanceActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subjectid", getIntent().getStringExtra("SUBJECTID"));
                map.put("facultyid", myPref.getID());
                map.put("imonth", getIntent().getStringExtra("MONTH"));
                map.put("iyear", getIntent().getStringExtra("YEAR"));
                map.put("idate", getIntent().getStringExtra("DATE"));
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }


}
