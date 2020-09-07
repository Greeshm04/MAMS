package apex.mams.faculty;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment {


    public AttendanceFragment() {
        // Required empty public constructor
    }

    View view;
    Getip getip;
    MyPref myPref;
    RecyclerView list_attendance;
    Spinner spn_Sem, spn_Subject;
    List<HashMap<String, String>> lsforattendance = new ArrayList<HashMap<String, String>>();
    SwipeRefreshLayout obswipeRefreshLayout;

    List<String> lsSem = new ArrayList<String>();
    List<String> lsSubject = new ArrayList<String>();
    List<String> lsSubjectID = new ArrayList<String>();

    int posSem, posSubject;
    String subjectid, subjectName, SEM;
    TextView txt_nodata;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getip = new Getip(getActivity());
        myPref = new MyPref(getContext());
        view = inflater.inflate(R.layout.fragment_attendance, container, false);
        ((FacultyDashboardActivity) getActivity()).setActionBarTitle("Attendance");
        list_attendance = view.findViewById(R.id.list_attendance);
        spn_Sem = view.findViewById(R.id.spn_Sem);
        txt_nodata = view.findViewById(R.id.txt_nodata);
        spn_Subject = view.findViewById(R.id.spn_Subject);
        obswipeRefreshLayout = view.findViewById(R.id.SwipeContainer);
        obswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lsSem.clear();
                lsSubject.clear();
                lsforattendance.clear();
                lsSubjectID.clear();

                lsSem.add("Select Sem");
                lsSubject.add("Select Subject");

                for (int i = 0; i < myPref.loadArray("MainSemester").length; i++)
                    lsSem.add(myPref.loadArray("MainSemester")[i]);
                spn_Sem.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lsSem));
                spn_Subject.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lsSubject));
                list_attendance.setAdapter(new CustomAdpForAttendance(getContext(), lsforattendance));
                obswipeRefreshLayout.setRefreshing(false);
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceFragment.this.getActivity(), addAttendanceActivity.class);
                startActivity(intent);
            }
        });


        spn_Sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


                posSem = position;
                //to clear List
                try {
                    lsSubject.clear();
                    lsSubject.add("Select Subject");
                    spn_Subject.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lsSubject));

                    lsSubjectID.clear();
                    lsSubjectID.add("Selected SubjectId");


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
                    spn_Subject.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lsSubject));


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


                if (position != 0) {
                    if (position != 0 && posSem != 0) {
                        //fill Attendance
                        subjectid = lsSubjectID.get(posSubject);
                        subjectName = lsSubject.get(posSubject);
                        SEM = lsSem.get(posSem);


                        obswipeRefreshLayout.setRefreshing(true);
                        fill_Attendance();


                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        lsSem.clear();
        lsSubject.clear();
        lsforattendance.clear();
        lsSubjectID.clear();


        lsSem.add("Select Sem");
        lsSubject.add("Select Subject");


        //Add Main semester for select
        for (int i = 0; i < myPref.loadArray("MainSemester").length; i++)
            lsSem.add(myPref.loadArray("MainSemester")[i]);
        spn_Sem.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lsSem));
        spn_Subject.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lsSubject));

    }

    public void fill_Attendance() {


        lsforattendance.clear();
        list_attendance.setLayoutManager(new GridLayoutManager(getContext(), 1));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/select_attendance.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    if (jsonArray.length()<1)
                    {
                        txt_nodata.setVisibility(View.VISIBLE);
                    }
                    else {
                        txt_nodata.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Log.e("SUBJECTIDBYFETCH:-", jsonObject1.getString("subjectid"));
                        Log.e("SUBJECTNAMEBYFETCH:-", jsonObject1.getString("subjectname"));
                        Log.e("MONTHBYFETCH:-", jsonObject1.getString("imonth"));
                        Log.e("YEARBYFETCH:-", jsonObject1.getString("iyear"));
                        Log.e("TIMEBYFETCH:-", jsonObject1.getString("itime"));

                        JSONArray jsonArray1 = jsonObject1.getJSONArray("IDATE");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                            Log.e("DATEBYFETCH:-", jsonObject2.getString("date"));
                            Log.e("STATUSBYFETCH:-", jsonObject2.getString("status"));

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("DATE", jsonObject2.getString("date"));
                            map.put("MONTH", jsonObject1.getString("imonth"));
                            map.put("YEAR", jsonObject1.getString("iyear"));
                            map.put("TIME", jsonObject1.getString("itime"));
                            map.put("SUBJECTID", jsonObject1.getString("subjectid"));
                            map.put("SUBJECTNAME", jsonObject1.getString("subjectname"));
                            map.put("SEM", SEM);


                            lsforattendance.add(map);
                            list_attendance.setAdapter(new CustomAdpForAttendance(getContext(), lsforattendance));


                        }

                    }
                    obswipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {
                    obswipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                obswipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Error Occured" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Error Occured", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subjectid", subjectid);
                map.put("facultyid", myPref.getID());
                map.put("subjectname", subjectName);

                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }


}
