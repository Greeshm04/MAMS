package apex.mams.faculty;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
public class AttendanceReportFragment extends Fragment {

    RecyclerView rv_attendance;
    List<HashMap<String, String>> lsforattendance = new ArrayList<HashMap<String, String>>();

    Spinner spin_sem_forAttendance;
    List<String> ls_forsem = new ArrayList<String>();

    LinearLayout ln_attendancetable;
    int fd = 0, fm = 0, fy = 2018;
    int td = 0, tm = 0, ty = 2018;

    MyPref myPref;
    Getip getip;
    TextView sub1, sub2, sub3, sub4, sub5, sub6, sub7, sub8;
    TextView[] sub;
    EditText edt_fromDate, edt_toDate;
    TextView txt_srno, txt_enno;
    TextView txt_nodata;

    String selected_sem;

    public AttendanceReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_report, container, false);
        myPref = new MyPref(getContext());
        getip = new Getip(getContext());

        rv_attendance = (RecyclerView) view.findViewById(R.id.rv_attendance);
        spin_sem_forAttendance = (Spinner) view.findViewById(R.id.spin_sem_forAttendance);
        edt_fromDate = (EditText) view.findViewById(R.id.edt_fromDate);
        edt_toDate = (EditText) view.findViewById(R.id.edt_toDate);
        sub1 = (TextView) view.findViewById(R.id.sub1);
        sub2 = (TextView) view.findViewById(R.id.sub2);
        sub3 = (TextView) view.findViewById(R.id.sub3);
        sub4 = (TextView) view.findViewById(R.id.sub4);
        sub5 = (TextView) view.findViewById(R.id.sub5);
        sub6 = (TextView) view.findViewById(R.id.sub6);
        sub7 = (TextView) view.findViewById(R.id.sub7);
        sub8 = (TextView) view.findViewById(R.id.sub8);

        txt_nodata = view.findViewById(R.id.txt_nodata);
        ln_attendancetable = view.findViewById(R.id.ln_attendancetable);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        txt_srno = (TextView) view.findViewById(R.id.txt_srno);
        txt_enno = (TextView) view.findViewById(R.id.txt_enno);

        txt_srno.measure(0, 0);
        txt_enno.measure(0, 0);

        sub = new TextView[]{sub1, sub2, sub3, sub4, sub5, sub6, sub7, sub8};

        for (int i = 0; i < sub.length; i++) {
            sub[i].measure(0, 0);
        }

        ls_forsem.add("Select Semester");
        ls_forsem.add("1");
        ls_forsem.add("2");
        ls_forsem.add("3");
        ls_forsem.add("4");
        ls_forsem.add("5");
        ls_forsem.add("6");

        ArrayAdapter adp = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, ls_forsem);
        spin_sem_forAttendance.setAdapter(adp);

        spin_sem_forAttendance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sub1.setVisibility(View.GONE);
                sub2.setVisibility(View.GONE);
                sub3.setVisibility(View.GONE);
                sub4.setVisibility(View.GONE);
                sub5.setVisibility(View.GONE);
                sub6.setVisibility(View.GONE);
                sub7.setVisibility(View.GONE);
                sub8.setVisibility(View.GONE);

                txt_srno.setVisibility(View.GONE);
                txt_enno.setVisibility(View.GONE);


                if (position != 0) {
                    selected_sem = ls_forsem.get(position);

                    fill_attendance();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edt_fromDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            fy = i;
                            fm = i1 + 1;
                            fd = i2;
                            edt_fromDate.setText(fd + "-" + fm + "-" + fy);
                        }
                    };

                    new DatePickerDialog(getContext(), mDateSetListener, fy, fm, fd).show();
                }
                return false;
            }
        });

        edt_toDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener mDateSetListener
                            = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            ty = i;
                            tm = i1 + 1;
                            td = i2;
                            edt_toDate.setText(td + "-" + tm + "-" + ty);
                        }
                    };

                    new DatePickerDialog(getContext(), mDateSetListener, ty, tm, td).show();
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void fill_attendance() {

        lsforattendance.clear();
        rv_attendance.setLayoutManager(new GridLayoutManager(getContext(), 1));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/select_attendance_report_faculty.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() < 2) {
                        txt_nodata.setVisibility(View.VISIBLE);
                        ln_attendancetable.setVisibility(View.GONE);
                    } else {
                        txt_nodata.setVisibility(View.GONE);
                        ln_attendancetable.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        txt_srno.setVisibility(View.VISIBLE);
                        txt_enno.setVisibility(View.VISIBLE);

                        if (i == 0) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            JSONArray jsonArray1 = jsonObject.getJSONArray("Subject_list");

                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                sub[j].setText(jsonObject1.getString("SubjectName"));
                                sub[j].measure(0, 0);
                                sub[j].setVisibility(View.VISIBLE);
                            }


                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("srno", String.valueOf(i));
                            txt_srno.measure(0, 0);
                            map.put("wsrno", String.valueOf(txt_srno.getMeasuredWidth()));
                            map.put("enno", jsonObject.getString("Enrollmentno"));
                            txt_enno.measure(0, 0);
                            map.put("wenno", String.valueOf(txt_enno.getMeasuredWidth()));

                            JSONArray jsonArray1 = jsonObject.getJSONArray("attendance");
                            map.put("sublength", String.valueOf(jsonArray1.length()));

                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(j);

                                map.put("sub" + String.valueOf(j + 1), Math.round(Float.parseFloat(jsonObject1.getString("Percentage"))) + "%");
                                sub[j].measure(0, 0);
                                map.put("wsub" + String.valueOf(j + 1), String.valueOf(sub[j].getMeasuredWidth()));
                            }

                            lsforattendance.add(map);
                            rv_attendance.setAdapter(new CustomAdpforAttendanceReport(getContext(), lsforattendance));
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error : " + e, Toast.LENGTH_SHORT).show();
                    Log.e("&", String.valueOf(e));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "" + error, Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("facultyid", FacultyDashboardActivity.userId);
                map.put("sem", selected_sem);

                map.put("fd", String.valueOf(fd));
                map.put("fm", String.valueOf(fm));
                map.put("fy", String.valueOf(fy));

                map.put("td", String.valueOf(td));
                map.put("tm", String.valueOf(tm));
                map.put("ty", String.valueOf(ty));

                if (String.valueOf(fd).equals("0") && String.valueOf(td).equals("0")) {
                    map.put("data", "ALL");
                } else {
                    map.put("data", "LIMITED");
                }
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

}


