package apex.mams.faculty;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadNoticeFragment extends Fragment {

    Getip getip;
    //    ListView list_facultynotice;
    SwipeMenuListView list_facultynotice;
    List<HashMap<String, String>> lsforfacultynotice = new ArrayList<HashMap<String, String>>();
    String delID;
    int pos;

    SwipeRefreshLayout obswipeRefreshLayout;
    String filename, url;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    MenuItem editItem, deleteItem,cancleItem;

    AdapterView<?> PARENT;

    TextView txt_nodata;

    public UploadNoticeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_upload_notice, container, false);
        ((FacultyDashboardActivity) getActivity()).setActionBarTitle("Notice Dashboard");
        getip = new Getip(getActivity());
        list_facultynotice = view.findViewById(R.id.list_facultynotice);
        txt_nodata = view.findViewById(R.id.txt_nodata);
        setHasOptionsMenu(true);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadNoticeFragment.this.getActivity(), addnoticeActivity.class);
                intent.putExtra("ch", "Insert");
                intent.putExtra("id", "");
                startActivity(intent);
            }
        });

        obswipeRefreshLayout = view.findViewById(R.id.SwipeContainer);
        obswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lsforfacultynotice.clear();
                obswipeRefreshLayout.setRefreshing(true);
                fill_Dashboard();
            }
        });




        list_facultynotice.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;

                    PARENT=parent;
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

        lsforfacultynotice.clear();
        obswipeRefreshLayout.setRefreshing(true);
        fill_Dashboard();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_menu, menu);


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
                Intent intent = new Intent(UploadNoticeFragment.this.getActivity(), addnoticeActivity.class);
                intent.putExtra("id", lsforfacultynotice.get(pos).get("noticeid"));
                intent.putExtra("ch", "Update");
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
                builder.setTitle("Are you sure to Delete");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delID = lsforfacultynotice.get(pos).get("noticeid");
                        filename = lsforfacultynotice.get(pos).get("noticefile");
                        if (filename.equals(""))
                            delete_notice();
                        else
                            fetchUrl();
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

    public void fill_Dashboard() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notice_master/select_faculty_upload.php", new Response.Listener<String>() {
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
                        map.put("notice_size", jsonObject1.getString("notice_size"));

                        if ( jsonObject1.getString("notice_sem").equals("0"))
                            map.put("noticeSem", "For All Sem");
                        else
                            map.put("noticeSem", "Semester:- "+jsonObject1.getString("notice_sem"));


                        map.put("notice_status", jsonObject1.getString("notice_status"));
                        map.put("noticeby", "You");
                        map.put("noticefile", jsonObject1.getString("noticefile"));
                        map.put("notice_color", "#aed581");

                        lsforfacultynotice.add(map);
                    }
                    list_facultynotice.setAdapter(new CustomAdpForFacultyNotice(getActivity(), lsforfacultynotice));

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
                map.put("user", FacultyDashboardActivity.user);
                Log.e("&&",FacultyDashboardActivity.user);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void delete_notice() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Deleting DATA");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notice_master/delete_notice_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int i = jsonObject.getInt("status");

                    if (i == 0) {
                        Toast.makeText(getActivity(), "Not Deleted", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else if (i == 1) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                    }

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(UploadNoticeFragment.this).attach(UploadNoticeFragment.this).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("noticeid", delID);

                return map;
            }
        };

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void fetchUrl() {


        databaseReference = FirebaseDatabase.getInstance().getReference(addnoticeActivity.DATABASE_PATH);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Deleting..");
        progressDialog.setMessage("File is Deleting..");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ImageUpload img = snapshot.getValue(ImageUpload.class);
                        String url2 = img.getUrl();
                        String name2 = img.getName();
//                    Toast.makeText(ImageListActivity.this, ""+name, Toast.LENGTH_SHORT).show();
                        Log.e("#####################", name2 + filename);
                        Log.e("#####################", url2);

                        if (name2.equals(filename.toString())) {
                            url = url2;
                            Log.e("###########::::::::::::", filename);
                            Log.e("#############::::::::::", url);
                            deleteFile();
                            break;
                        }
//                    Toast.makeText(ImageListActivity.this, ""+img.getUrl(), Toast.LENGTH_SHORT).show();

//                    fetchImage(url,name);
                    }


                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void deleteFile() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference = storageReference.child("Notice/" + filename.toString());

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "File Deleted", Toast.LENGTH_SHORT).show();
                delete_notice();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "May be File not exists", Toast.LENGTH_SHORT).show();
                delete_notice();
            }
        });
    }

}
