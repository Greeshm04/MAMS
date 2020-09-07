package apex.mams.admin;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

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
public class AcademicFragment extends Fragment {


    View view;
    SwipeMenuListView list_academic;
    Getip getip;
    String delAId;
    List<HashMap<String,String>> lsforacademic=new ArrayList<HashMap<String, String>>();
    SwipeRefreshLayout swipeRefreshLayout;
    MenuItem editItem, deleteItem,cancleItem;

    AdapterView<?> PARENT;
    int pos;
    TextView txt_nodata;


    public AcademicFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_academic, container, false);
        ((AdminDashboardActivity)getActivity()).setActionbarTitle("Academic Year");

        list_academic= (SwipeMenuListView) view.findViewById(R.id.list_academic);
        txt_nodata= (TextView) view.findViewById(R.id.txt_nodata);


        setHasOptionsMenu(true);

        getip=new Getip(getContext());
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.SwipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                editItem.setVisible(false);
                deleteItem.setVisible(false);
                cancleItem.setVisible(false);

                lsforacademic.clear();
                fill_academic_data();
                swipeRefreshLayout.setRefreshing(true);

            }
        });
        FloatingActionButton fab= (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AcademicFragment.this.getActivity(), addacademicActivity.class);
                intent.putExtra("academicid","");
                intent.putExtra("status","insert");
                startActivity(intent);
            }
        });




       list_academic.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
               pos = position;
               PARENT=adapterView;
               PARENT.getChildAt(pos).setBackgroundColor(Color.parseColor("#d9b3ff"));

               editItem.setVisible(true);
               deleteItem.setVisible(true);
               cancleItem.setVisible(true);
               return false;
           }
       });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        lsforacademic.clear();
        fill_academic_data();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.actionbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        editItem = menu.findItem(R.id.action_edit);
        menu.findItem(R.id.action_edit).getActionView();

        cancleItem = menu.findItem(R.id.action_cancle);
        menu.findItem(R.id.action_edit).getActionView();

        deleteItem = menu.findItem(R.id.action_delete);
        menu.findItem(R.id.action_delete).getActionView();

        editItem.setVisible(false);
        deleteItem.setVisible(false);
        cancleItem.setVisible(false);

        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(AcademicFragment.this.getActivity(), addacademicActivity.class);
                intent.putExtra("academicid",lsforacademic.get(pos).get("academicid"));
                intent.putExtra("status","update");
                startActivity(intent);

                editItem.setVisible(false);
                deleteItem.setVisible(false);
                cancleItem.setVisible(false);

                return false;
            }
        });


        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.ic_warning_black_24dp);
                builder.setMessage("Are You Sure To Delete?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delAId=lsforacademic.get(pos).get("academicid");
                        delete_academic();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

                editItem.setVisible(false);
                deleteItem.setVisible(false);
                cancleItem.setVisible(false);

                return false;
            }
        });

        cancleItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                PARENT.getChildAt(pos).setBackgroundColor(Color.parseColor("#FAFAFA"));

                cancleItem.setVisible(false);
                editItem.setVisible(false);
                deleteItem.setVisible(false);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void fill_academic_data() {

        lsforacademic.clear();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,getip.getUrl()+"academic_year_master/select_academic_year.php", new Response.Listener<String>() {
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
                        HashMap<String,String> map=new HashMap<String, String>();
                        map.put("academicid",jsonObject1.getString("academicid"));
                        map.put("academic_text",jsonObject1.getString("academic_text"));
                        map.put("start_date",jsonObject1.getString("start_date"));
                        map.put("end_date",jsonObject1.getString("end_date"));
                        lsforacademic.add(map);

                        list_academic.setAdapter( new CustomAdpForAcademic(getActivity(),lsforacademic));
                    }
                    if (i == jsonArray.length()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }


                } catch (JSONException e) {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e("ERROR",e.getMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(view,"ERROR",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                Log.e("Error Occured",error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void delete_academic() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting Academic Year..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "academic_year_master/delete_academic_year.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int i = jsonObject.getInt("status");
                    if (i == 1) {
                        Toast.makeText(getContext(), "Academic Year Deleted", Toast.LENGTH_SHORT).show();
                        fill_academic_data();
                        progressDialog.dismiss();

                    } else if (i == 0) {
                        Toast.makeText(getContext(), "Academic Year Not Deleted", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "ERROR" + e, Toast.LENGTH_LONG).show();
                    Log.d("####################", e.toString());
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("academicid", delAId);
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }


}
