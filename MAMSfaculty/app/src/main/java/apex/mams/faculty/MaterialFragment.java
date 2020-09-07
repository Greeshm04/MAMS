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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MaterialFragment extends Fragment {

    public MaterialFragment() {
        // Required empty public constructor
    }

    View view;
    Getip getip;
    String filename, url;
    String delID;
    SwipeMenuListView list_material;
    List<HashMap<String, String>> lsformaterial = new ArrayList<>();

    SwipeRefreshLayout obswipeRefreshLayout;

    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    int pos;
    MenuItem editItem, deleteItem,cancleItem;
    TextView txt_nodata;

    AdapterView<?> PARENT;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_material, container, false);
        ((FacultyDashboardActivity) getActivity()).setActionBarTitle("Material");
        getip = new Getip(getContext());
        setHasOptionsMenu(true);

        list_material = view.findViewById(R.id.list_material);
        txt_nodata = view.findViewById(R.id.txt_nodata);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaterialFragment.this.getActivity(), addMaterialActivity.class);
                intent.putExtra("ch", "Insert");
                intent.putExtra("id", "");
                startActivity(intent);
            }
        });



        obswipeRefreshLayout = view.findViewById(R.id.SwipeContainer);
        obswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lsformaterial.clear();
                obswipeRefreshLayout.setRefreshing(true);
                fill_material();
            }
        });


        list_material.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                pos=position;

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
        lsformaterial.clear();
        obswipeRefreshLayout.setRefreshing(true);
        fill_material();

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
                Intent intent = new Intent(MaterialFragment.this.getActivity(), addMaterialActivity.class);
                intent.putExtra("id", lsformaterial.get(pos).get("materialid"));
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
                        delID = lsformaterial.get(pos).get("materialid");
                        filename = lsformaterial.get(pos).get("material_file");
                        if (filename.equals(""))
                            delete_material();
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

    public void fill_material() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "material_master/select_material_master_faculty.php", new Response.Listener<String>() {
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
                        map.put("materialid", jsonObject1.getString("materialid"));
                        map.put("subjectid", jsonObject1.getString("subjectid"));
                        map.put("material_title", jsonObject1.getString("material_title"));
                        map.put("material_description", jsonObject1.getString("material_description"));
                        map.put("material_file", jsonObject1.getString("material_file"));
                        map.put("material_sem", jsonObject1.getString("material_sem"));
                        map.put("material_size", jsonObject1.getString("material_size"));

                        if ( jsonObject1.getString("material_sem").equals("0"))
                            map.put("material_sem", "For All Sem");
                        else
                            map.put("material_sem", "Semester:- "+jsonObject1.getString("material_sem"));


                        map.put("material_status", jsonObject1.getString("material_status"));
                        map.put("upload_date", jsonObject1.getString("upload_date"));
                        map.put("materialby", jsonObject1.getString("materialby"));

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
                map.put("materialby", FacultyDashboardActivity.user);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void delete_material() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Deleting DATA");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "material_master/delete_material_master.php", new Response.Listener<String>() {
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
                    ft.detach(MaterialFragment.this).attach(MaterialFragment.this).commit();
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
                map.put("materialid", delID);

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
        storageReference = storageReference.child("Material/" + filename.toString());

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "File Deleted", Toast.LENGTH_SHORT).show();
                delete_material();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "May be File not exists", Toast.LENGTH_SHORT).show();
                delete_material();
            }
        });
    }


}
