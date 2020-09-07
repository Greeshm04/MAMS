package apex.mams.admin;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
public class SubjectFragment extends Fragment {


    Spinner spn_SubjectSem;
    ListView list_Subject;
    Getip getip;
    String sem;
    List<String> lsforsem=new ArrayList<String>();
    List<HashMap<String,String>> lsforSubject=new ArrayList<HashMap<String, String>>();
    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    TextView txt_nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_subject, container, false);
        getip=new Getip(getContext());
        ((AdminDashboardActivity)getActivity()).setActionbarTitle("Subjects");
        spn_SubjectSem= (Spinner) view.findViewById(R.id.spn_SubjectSem);
        list_Subject= (ListView) view.findViewById(R.id.list_Subject);
        txt_nodata= (TextView) view.findViewById(R.id.txt_nodata);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.SwipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lsforSubject.clear();
                fetch_subject();
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        FloatingActionButton fab= (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SubjectFragment.this.getActivity(),addsubjectActivity.class);
                intent.putExtra("subjectsid","");
                intent.putExtra("status","insert");
                startActivity(intent);
            }
        });


        lsforsem.add("Select Semester");
        lsforsem.add("1");
        lsforsem.add("2");
        lsforsem.add("3");
        lsforsem.add("4");
        lsforsem.add("5");
        lsforsem.add("6");


        spn_SubjectSem.setAdapter(new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,lsforsem));


        spn_SubjectSem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lsforSubject.clear();
                if (i==0) {


                }
                else {
                    sem = lsforsem.get(i);
                    fetch_subject();
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
        lsforSubject.clear();
        fetch_subject();
    }

    public void fetch_subject()
    {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "subject_master/select_subject_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    int i=0;
                    int flag=0;
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("response");

                    for (i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                        if (jsonObject1.getString("subject_sem").equals(sem))
                        {


                            String facultyName=jsonObject1.getString("firstname")+" "+jsonObject1.getString("lastname");

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("subjectId", jsonObject1.getString("subjectid"));
                            map.put("subjectCode", jsonObject1.getString("subject_code"));
                            map.put("subjectName", jsonObject1.getString("subject_name"));
                            map.put("facultyId", jsonObject1.getString("facultyid"));
                            if (facultyName.equals("null null"))
                            {
                                map.put("subjectFaculty","");
                            }
                            else
                            {
                                map.put("subjectFaculty",facultyName);
                            }


                            Log.d("##################","NAme:="+jsonObject1.getString("subject_name"));
                            Log.d("##################","Faculty:="+jsonObject1.getString("firstname")+" "+jsonObject1.getString("lastname"));
                            lsforSubject.add(map);
                            list_Subject.setAdapter(new CustomAdpForSubject(getActivity(), lsforSubject));

                            flag=1;

                        }


                    }


                    if (flag==0)
                    {
                        try {
                            lsforSubject.clear();
                            list_Subject.setAdapter(new CustomAdpForSubject(getActivity(), lsforSubject));
                        }catch (Exception e){}

                        txt_nodata.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        txt_nodata.setVisibility(View.GONE);

                    }
                    if (i==jsonArray.length())
                    {
                        swipeRefreshLayout.setRefreshing(false);
//
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(view,"ERROR",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                    swipeRefreshLayout.setRefreshing(false);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(view,"Error Occured",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

}
