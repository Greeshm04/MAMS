package apex.mams.student;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    RecyclerView rv_attendance;
    Spinner spin_sub_forAttendance;
    List<String> ls_forsub = new ArrayList<String>();
    List<HashMap<String, String>> lsforattendance = new ArrayList<HashMap<String, String>>();
    LinearLayout ln_attendancetable;

    int fd = 0, fm = 0, fy = 2018;
    int td = 0, tm = 0, ty = 2018;

    MyPref myPref;
    Getip getip;
    TableLayout table_viewAttendance;
    TextView txt_Atsrno, txt_Atdate, txt_Atstatus, txt_nodata;
    TextView txt_attendanceCounter, txt_attendancePercentage;
    TextView edt_fromDate, edt_toDate;

    String selected_sub;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        myPref = new MyPref(getContext());
        getip = new Getip(getContext());

        rv_attendance = view.findViewById(R.id.rv_attendance);
        txt_attendanceCounter = view.findViewById(R.id.txt_attendanceCounter);
        txt_attendancePercentage = view.findViewById(R.id.txt_attendancePercentage);
        spin_sub_forAttendance = view.findViewById(R.id.spin_sub_forAttendance);
        edt_fromDate = view.findViewById(R.id.edt_fromDate);
        edt_toDate = view.findViewById(R.id.edt_toDate);
        ln_attendancetable = view.findViewById(R.id.ln_attendancetable);
        txt_nodata = view.findViewById(R.id.txt_nodata);

        edt_fromDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
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
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
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


        ls_forsub.add("Select Subject");
        for (int i = 0; i < myPref.loadArray("SubjectName").length; i++) {
            ls_forsub.add(myPref.loadArray("SubjectName")[i]);
        }

        ArrayAdapter adp = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, ls_forsub);
        spin_sub_forAttendance.setAdapter(adp);

        spin_sub_forAttendance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {

                    for (int i = 0; i < myPref.loadArray("SubjectName").length; i++) {
                        if (myPref.loadArray("SubjectName")[i].equals(ls_forsub.get(position))) {

                            selected_sub = myPref.loadArray("SubjectId")[i];
                            fill_attendance();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;

    }


    private void fill_attendance() {

        lsforattendance.clear();
        rv_attendance.setLayoutManager(new GridLayoutManager(getContext(), 1));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/select_attendance.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int cnt = 0;
                    int Presentcnt = 0;

                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() < 1) {
                        ln_attendancetable.setVisibility(View.GONE);
                        txt_attendanceCounter.setVisibility(View.GONE);
                        txt_attendancePercentage.setVisibility(View.GONE);
                        txt_nodata.setVisibility(View.VISIBLE);
                    } else {
                        ln_attendancetable.setVisibility(View.VISIBLE);
                        txt_attendanceCounter.setVisibility(View.VISIBLE);
                        txt_attendancePercentage.setVisibility(View.VISIBLE);
                        txt_nodata.setVisibility(View.GONE);
                    }
                    Log.e("&", String.valueOf(jsonArray.length()));
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        JSONArray jsonArray1 = jsonObject.getJSONArray("Idate");
                        for (int i = 0; i < jsonArray1.length(); i++) {

                            JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                            cnt++;
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("srno", String.valueOf(cnt));
                            map.put("date", jsonObject1.getString("Idate") + "-" + jsonObject.getString("Imonth") + "-" + jsonObject.getString("Iyear"));
                            map.put("status", jsonObject1.getString("status"));
                            if (jsonObject1.getString("status").equals("P")) {
                                Presentcnt++;
                            }

                            lsforattendance.add(map);
                            rv_attendance.setAdapter(new CustomAdpforAttendance(getContext(), lsforattendance));

                        }
                    }

                    try {
                        txt_attendanceCounter.setText(Presentcnt + "/" + cnt);
                        txt_attendancePercentage.setText(((Presentcnt * 100) / cnt) + "%");
                    } catch (Exception e) {

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
                map.put("studid", StudentDashboardActivity.userId);
                map.put("subid", selected_sub);

                map.put("fd", String.valueOf(fd));
                map.put("fm", String.valueOf(fm));
                map.put("fy", String.valueOf(fy));

                map.put("td", String.valueOf(td));
                map.put("tm", String.valueOf(tm));
                map.put("ty", String.valueOf(ty));

                if (String.valueOf(td).equals("0") && String.valueOf(fd).equals("0")) {
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
