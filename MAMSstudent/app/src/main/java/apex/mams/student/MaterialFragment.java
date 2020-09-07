package apex.mams.student;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class MaterialFragment extends Fragment {


    SwipeRefreshLayout obswipeRefreshLayout;
    RecyclerView list_material;
    List<HashMap<String, String>> lsformaterial = new ArrayList<HashMap<String, String>>();
    Getip getip;
    MyPref myPref;
    View view;

    TextView txt_nodata;
    public MaterialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_material, container, false);
        ((StudentDashboardActivity) getActivity()).setActionBarTitle("Material");
        list_material = view.findViewById(R.id.list_material);
        txt_nodata = view.findViewById(R.id.txt_nodata);
        getip = new Getip(getContext());
        myPref=new MyPref(getContext());

        obswipeRefreshLayout = view.findViewById(R.id.SwipeContainer);
        obswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lsformaterial.clear();
                obswipeRefreshLayout.setRefreshing(true);
                fill_material();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        lsformaterial.clear();
        obswipeRefreshLayout.setRefreshing(true);
        fill_material();
    }

    public void fill_material() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "material_master/select_material_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    int i;
                    list_material.setLayoutManager(new GridLayoutManager(getContext(), 1));

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
                        map.put("materialid", jsonObject1.getString("materialid"));
                        map.put("subjectid", jsonObject1.getString("subjectid"));
                        map.put("material_title", jsonObject1.getString("material_title"));
                        map.put("material_description", jsonObject1.getString("material_description"));
                        map.put("material_file", jsonObject1.getString("material_file"));
                        map.put("material_status", jsonObject1.getString("material_status"));
                        map.put("upload_date", jsonObject1.getString("upload_date"));
                        map.put("materialby", jsonObject1.getString("materialby"));
                        map.put("material_size", jsonObject1.getString("material_size"));

                        lsformaterial.add(map);
                    }
                    list_material.setAdapter(new CustomAdpForMaterial(getActivity(), lsformaterial));

                    if (i == jsonArray.length()) {
                        obswipeRefreshLayout.setRefreshing(false);
                    }


                } catch (JSONException e) {
                    obswipeRefreshLayout.setRefreshing(false);
                    Log.e("#############", e.getMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error Occured" + error, Toast.LENGTH_SHORT).show();
                obswipeRefreshLayout.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("material_sem",myPref.getCurrent_sem());

                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

}
