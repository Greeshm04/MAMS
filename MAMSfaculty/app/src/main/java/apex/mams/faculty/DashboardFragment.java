package apex.mams.faculty;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {
    RecyclerView list_notice;
    List<HashMap<String, String>> lsfornotice = new ArrayList<HashMap<String, String>>();
    Getip getip;
    MyPref myPref;
    SwipeRefreshLayout obswipeRefreshLayout;

    TextView txt_nodata;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        getip = new Getip(getActivity());
        myPref=new MyPref(getContext());

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ((FacultyDashboardActivity) getActivity()).setActionBarTitle("Notice Dashboard");
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        list_notice = view.findViewById(R.id.list_notice);
        txt_nodata = view.findViewById(R.id.txt_nodata);
        obswipeRefreshLayout=view.findViewById(R.id.SwipeContainer);
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

        list_notice.setLayoutManager(new GridLayoutManager(getContext(),1));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notice_master/select_faculty_notice_master.php", new Response.Listener<String>() {
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
                        map.put("noticeSem",jsonObject1.getString("notice_sem"));
                        map.put("noticeby", jsonObject1.getString("noticeby"));
                        map.put("noticefile", jsonObject1.getString("noticefile"));
                        map.put("notice_size", jsonObject1.getString("notice_size"));
                        if(jsonObject1.getString("notice_type").equals("FACULTY")){
                            map.put("notice_color","#fff176");
                        }else if(jsonObject1.getString("notice_type").equals("BOTH")){
                            map.put("notice_color","#90caf9");
                        }

                        lsfornotice.add(map);
                        list_notice.setAdapter(new CustomAdpForNotice(getActivity(), lsfornotice,1));
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
                Toast.makeText(getActivity(), "Error Occured" + error, Toast.LENGTH_SHORT).show();
                obswipeRefreshLayout.setRefreshing(false);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }
}
