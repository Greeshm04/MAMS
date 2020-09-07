package apex.mams.admin;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
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
public class FacultyFragment extends Fragment {

    SwipeMenuListView list_faculty;
    Getip getip;
    List<HashMap<String, String>> lsforfaculty = new ArrayList<HashMap<String, String>>();
    List<HashMap<String, String>> lsforSearchedfaculty = new ArrayList<HashMap<String, String>>();

    SwipeRefreshLayout obswipeRefreshLayout;
    View view;
    String delFId;
    MenuItem editItem, deleteItem, cancleItem;

    AdapterView<?> PARENT;
    int pos;

    TextView txt_nodata;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_faculty, container, false);


        ((AdminDashboardActivity) getActivity()).setActionbarTitle("Faculties");

        getip = new Getip(getContext());
        list_faculty = (SwipeMenuListView) view.findViewById(R.id.list_faculty);
        txt_nodata = (TextView) view.findViewById(R.id.txt_nodata);
        setHasOptionsMenu(true);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        obswipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeContainer);
        obswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                editItem.setVisible(false);
                deleteItem.setVisible(false);
                cancleItem.setVisible(false);

                fill_faculty_data();
                obswipeRefreshLayout.setRefreshing(true);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FacultyFragment.this.getActivity(), addfacultyActivity.class);
                intent.putExtra("facultysid", "");
                intent.putExtra("status", "insert");
                startActivity(intent);
            }
        });


        list_faculty.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.actionbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);


        editItem = menu.findItem(R.id.action_edit);
        menu.findItem(R.id.action_edit).getActionView();

        cancleItem = menu.findItem(R.id.action_cancle);
        menu.findItem(R.id.action_edit).getActionView();

        deleteItem = menu.findItem(R.id.action_delete);
        menu.findItem(R.id.action_delete).getActionView();
        editItem.setVisible(false);
        deleteItem.setVisible(false);
        cancleItem.setVisible(false);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                obswipeRefreshLayout.setRefreshing(true);
                search_faculty_data("", false);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                lsforSearchedfaculty.clear();
                search_faculty_data(query, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lsforSearchedfaculty.clear();
                search_faculty_data(newText, true);
                return false;
            }
        });


        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(FacultyFragment.this.getActivity(), addfacultyActivity.class);
                intent.putExtra("facultysid", lsforfaculty.get(pos).get("facultyid"));
                intent.putExtra("status", "update");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.ic_warning_black_24dp);
                builder.setMessage("Are You Sure To Delete?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delFId = lsforfaculty.get(pos).get("facultyid");
                        delete_faculty();
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

    @Override
    public void onResume() {
        super.onResume();
        fill_faculty_data();
    }

    public void delete_faculty() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting Faculty..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/delete_faculty_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int i = jsonObject.getInt("status");
                    if (i == 1) {
                        Toast.makeText(getContext(), "Faculty Deleted", Toast.LENGTH_SHORT).show();
                        lsforfaculty.clear();
                        fill_faculty_data();
                        progressDialog.dismiss();

                    } else if (i == 0) {
                        Toast.makeText(getContext(), "Faculty Not Deleted", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();


                        getFragmentManager().beginTransaction().detach(FacultyFragment.this).attach(FacultyFragment.this).commit();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "ERROR" + e, Toast.LENGTH_SHORT).show();
                    Log.d("####################", e.toString());
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error Occured", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("facultyid", delFId);
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void fill_faculty_data() {

        lsforfaculty.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/select_faculty_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    obswipeRefreshLayout.setRefreshing(false);
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
                        map.put("facultyid", jsonObject1.getString("facultyid"));
                        map.put("firstName", jsonObject1.getString("firstname"));
                        map.put("lastName", jsonObject1.getString("lastname"));
                        map.put("mobNo", jsonObject1.getString("mobno"));
                        map.put("emailId", jsonObject1.getString("emailid"));
                        map.put("Dob", jsonObject1.getString("dob"));
                        map.put("Doj", jsonObject1.getString("doj"));
                        map.put("Address", jsonObject1.getString("address"));
                        map.put("City", jsonObject1.getString("city"));
                        map.put("status", jsonObject1.getString("status"));
                        map.put("dpurl", jsonObject1.getString("dpurl"));

                        lsforfaculty.add(map);

                        list_faculty.setAdapter(new CustomAdp(getActivity(), lsforfaculty));
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
                Snackbar.make(view, "ERROR", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                obswipeRefreshLayout.setRefreshing(false);

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void search_faculty_data(final String searchText, boolean search) {

        if (search == true) {
            for (int i = 0; i < lsforfaculty.size(); i++) {
                HashMap<String, String> map = lsforfaculty.get(i);

                if ((map.get("firstName").toLowerCase() + " " + map.get("lastName").toUpperCase()).contains(searchText.toLowerCase()) ||
                        (map.get("firstName").toLowerCase() + " " + map.get("lastName").toUpperCase()).contains(searchText.toUpperCase())) {
                    lsforSearchedfaculty.add(lsforfaculty.get(i));
                }
            }

            list_faculty.setAdapter(new CustomAdp(getActivity(), lsforSearchedfaculty));

            obswipeRefreshLayout.setRefreshing(false);
        } else {
            list_faculty.setAdapter(new CustomAdp(getActivity(), lsforfaculty));
            obswipeRefreshLayout.setRefreshing(false);
        }
    }

}
