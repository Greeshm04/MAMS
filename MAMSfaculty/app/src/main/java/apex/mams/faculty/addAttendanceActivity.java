package apex.mams.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
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

public class addAttendanceActivity extends AppCompatActivity {

    Spinner spn_Sem, spn_Subject;
    ListView list_Student;
    Button btn_addAttendance;
    List<String> lsSem = new ArrayList<String>();
    List<String> lsSubject = new ArrayList<String>();
    List<String> lsSubjectID = new ArrayList<String>();
    List<HashMap<String, String>> lsStudent = new ArrayList<HashMap<String, String>>();
    int posSem, posSubject;
    int totalStudent;
    String subjectid;
    MyPref myPref;
    Getip getip;

    TextView txt_nodata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendance);

        myPref = new MyPref(getApplicationContext());
        getip = new Getip(getApplicationContext());
        spn_Sem = findViewById(R.id.spn_Sem);
        spn_Subject = findViewById(R.id.spn_Subject);
        list_Student = findViewById(R.id.list_Student);
        btn_addAttendance = findViewById(R.id.btn_addAttendance);
        txt_nodata = findViewById(R.id.txt_nodata);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spn_Sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


                posSem = position;
                //to clear List
                try {
                    lsSubject.clear();
                    lsSubject.add("Select Subject");
                    spn_Subject.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lsSubject));

                    lsSubjectID.clear();
                    lsSubjectID.add("Selected SubjectId");
                    lsStudent.clear();
                    list_Student.setAdapter(new CustomAdpForStudentAttendance(getApplicationContext(), lsStudent));

                } catch (NullPointerException e) {
                }


                //to select subject based on Semester
                if (position != 0) {

                    String sem = lsSem.get(position);


                    //load Allocated subject from sem
                    for (int i = 0; i < myPref.loadArray("MainSemester").length; i++) {
                        if (sem.equals(myPref.loadArray("SubjectSem")[i])) {
                            lsSubject.add(myPref.loadArray("SubjectName")[i]);
                            lsSubjectID.add(myPref.loadArray("SubjectId")[i]);

                        }
                    }
                    spn_Subject.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lsSubject));


                } else {

                    btn_addAttendance.setVisibility(View.GONE);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spn_Subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                posSubject = position;
                try {

                    lsStudent.clear();
                    list_Student.setAdapter(new CustomAdpForStudentAttendance(getApplicationContext(), lsStudent));

                } catch (NullPointerException e) {
                }

                if (position != 0) {
                    if (position != 0 && posSem != 0) {
                        //load Student from sem
                        btn_addAttendance.setVisibility(View.VISIBLE);

                        for (int i = 0, j = 0; i < myPref.loadArray("StudentId").length; i++) {

                            if (lsSem.get(posSem).equals(myPref.loadArray("StudentSem")[i])) {


                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("id", String.valueOf(j));
                                map.put("studentid", myPref.loadArray("StudentId")[i]);
                                map.put("studentName", myPref.loadArray("StudentName")[i]);
                                map.put("studentEnroll", myPref.loadArray("StudentEnroll")[i]);
                                map.put("studentSem", myPref.loadArray("StudentSem")[i]);
                                j++;
                                totalStudent = j;

                                lsStudent.add(map);
                            }
                        }
                        list_Student.setAdapter(new CustomAdpForStudentAttendance(getApplicationContext(), lsStudent));

                        subjectid = lsSubjectID.get(posSubject);


                        if (totalStudent>0)
                        {
                            btn_addAttendance.setVisibility(View.VISIBLE);
                            txt_nodata.setVisibility(View.GONE);
                        }
                        else
                        {
                            btn_addAttendance.setVisibility(View.GONE);
                            txt_nodata.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    btn_addAttendance.setVisibility(View.GONE);

                    try {
                        lsStudent.clear();
                        list_Student.setAdapter(new CustomAdpForStudentAttendance(getApplicationContext(), lsStudent));


                    } catch (NullPointerException e) {
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btn_addAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (posSem != 0 && posSubject != 0) {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < totalStudent; i++) {
                        JSONObject jsonObject = new JSONObject();
                        Log.e("MYSTUENT",CustomAdpForStudentAttendance.lsAttendance.get(i).get("studentid"));
                        try {


                            jsonObject.put("studentid", CustomAdpForStudentAttendance.lsAttendance.get(i).get("studentid"));
                            jsonObject.put("studentName", CustomAdpForStudentAttendance.lsAttendance.get(i).get("studentName"));
                            jsonObject.put("studentEnroll", CustomAdpForStudentAttendance.lsAttendance.get(i).get("studentEnroll"));
                            jsonObject.put("studentStatus", CustomAdpForStudentAttendance.lsAttendance.get(i).get("status"));

                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }


                    Log.e("JSONARRAY:-   ", String.valueOf(jsonArray));
                    pass_attendance(jsonArray);

                }

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        lsSem.clear();
        lsSubject.clear();
        lsStudent.clear();
        lsSubjectID.clear();

        lsSem.add("Select Sem");
        lsSubject.add("Select Subject");

        btn_addAttendance.setVisibility(View.GONE);




        //Add Main semester for select
        for (int i = 0; i < myPref.loadArray("MainSemester").length; i++)
            lsSem.add(myPref.loadArray("MainSemester")[i]);
        spn_Sem.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lsSem));
        spn_Subject.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lsSubject));

    }

    public void pass_attendance(final JSONArray jsonArray) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adding A Attendance..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/attendance.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addAttendanceActivity.this, "Attendance Added Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    } else if (st == 0) {
                        Toast.makeText(addAttendanceActivity.this, "Attendance can't Added", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(addAttendanceActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR:-  ", e.getMessage());
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error occured:-  ", error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("list", String.valueOf(jsonArray));
                map.put("subjectid", subjectid);
                map.put("facultyid", myPref.getID());
                return map;


            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
}
