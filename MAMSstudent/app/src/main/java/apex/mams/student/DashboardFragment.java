package apex.mams.student;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
public class DashboardFragment extends Fragment {

    SwipeRefreshLayout obswipeRefreshLayout;
    MyPref myPref;

    TextView txt_nodata;
    public DashboardFragment() {
        // Required empty public constructor
    }

    RecyclerView list_notice;
    List<HashMap<String, String>> lsfornotice = new ArrayList<HashMap<String, String>>();
    Getip getip;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ((StudentDashboardActivity) getActivity()).setActionBarTitle("Notice Dashboard");
        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        list_notice = view.findViewById(R.id.list_notice);
        txt_nodata = view.findViewById(R.id.txt_nodata);
        myPref = new MyPref(getContext());

        getip = new Getip(getContext());

        obswipeRefreshLayout = view.findViewById(R.id.SwipeContainer);
        obswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lsfornotice.clear();
                obswipeRefreshLayout.setRefreshing(true);
                fill_Dashboard();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        lsfornotice.clear();
        obswipeRefreshLayout.setRefreshing(true);
        fill_Dashboard();

    }


    public void fill_Dashboard() {

        list_notice.setLayoutManager(new GridLayoutManager(getContext(), 1));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notice_master/select_student_notice_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    int i;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    if (jsonArray.length()<1)
                    {
                        txt_nodata.setVisibility(View.VISIBLE);
                    }
                    else {
                        txt_nodata.setVisibility(View.GONE);
                    }
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("noticeid", jsonObject1.getString("noticeid"));
                        map.put("notice_title", jsonObject1.getString("notice_title"));
                        map.put("notice_discription", jsonObject1.getString("notice_discription"));
                        map.put("notice_date", jsonObject1.getString("notice_date"));
                        map.put("notice_status", jsonObject1.getString("notice_status"));
                        map.put("notice_type", jsonObject1.getString("notice_type"));
                        map.put("noticeby", jsonObject1.getString("noticeby"));
                        map.put("noticefile", jsonObject1.getString("noticefile"));
                        map.put("notice_size", jsonObject1.getString("notice_size"));
                        if (jsonObject1.getString("notice_type").equals("FACULTY")) {
                            map.put("notice_color", "#fff176");
                        } else if (jsonObject1.getString("notice_type").equals("BOTH")) {
                            map.put("notice_color", "#90caf9");
                        } else if (jsonObject1.getString("notice_type").equals("STUDENT")) {
                            map.put("notice_color", "#aed581");
                        }


                        lsfornotice.add(map);
                        list_notice.setAdapter(new CustomAdpForNotice(getContext(), lsfornotice));
                    }
                    if (i == jsonArray.length()) {
                        obswipeRefreshLayout.setRefreshing(false);
                    }


                } catch (JSONException e) {
                    obswipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(view, "Error Occured", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("notice_sem", myPref.getCurrent_sem());
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

}
