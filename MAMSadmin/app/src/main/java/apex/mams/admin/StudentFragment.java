package apex.mams.admin;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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
public class StudentFragment extends Fragment {
    Spinner spn_StudentAcademic;
    String strstatus;

    SwipeMenuListView list_Student;
    List<String> lsforacademic = new ArrayList<String>();
    Getip getip;
    View view;
    List<HashMap<String, String>> lsforstudent = new ArrayList<HashMap<String, String>>();
    List<HashMap<String, String>> lsforSearchedstudent = new ArrayList<HashMap<String, String>>();
    String academic, academicId, delId;
    SwipeRefreshLayout obswipeRefreshLayout;
    CheckBox cbforall;
    EditText edt_stud_new_sem, edt_stud_new_ldate;
    TextView txtselectall;
    Button btn_update_all, btn_delete_all;
    int totalstudent;
    int a = 2017, a1 = 2, a2 = 21, ja = 2017, ja1 = 2, ja2 = 21;
    Switch swch_stud_status;

    MenuItem editItem, deleteItem, cancleItem;

    Adapter adp;
    AdapterView<?> PARENT;
    int pos;

    TextView txt_nodata;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_student, container, false);
        cbforall = (CheckBox) view.findViewById(R.id.cbforall);
        list_Student = (SwipeMenuListView) view.findViewById(R.id.list_Student);
        setHasOptionsMenu(true);
        txtselectall = (TextView) view.findViewById(R.id.txtselectall);


        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentFragment.this.getActivity(), addstudentActivity.class);
                intent.putExtra("studentsid", "");
                intent.putExtra("status", "insert");
                startActivity(intent);
            }
        });

        spn_StudentAcademic = (Spinner) view.findViewById(R.id.spn_StudentAcademic);
        txt_nodata = (TextView) view.findViewById(R.id.txt_nodata);
        spn_StudentAcademic.setVisibility(View.GONE);
        ((AdminDashboardActivity) getActivity()).setActionbarTitle("Students");
        getip = new Getip(getContext());
        obswipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeContainer);
        obswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFragmentManager().beginTransaction().detach(StudentFragment.this).attach(StudentFragment.this).commit();

                editItem.setVisible(false);
                deleteItem.setVisible(false);
                cancleItem.setVisible(false);

                obswipeRefreshLayout.setRefreshing(true);
            }
        });

        adp = new CustomAdpForStudent(getActivity(), lsforstudent, false);

        btn_update_all = (Button) view.findViewById(R.id.btn_update_all);
        btn_delete_all = (Button) view.findViewById(R.id.btn_delete_all);
        txtselectall.setVisibility(View.GONE);
        cbforall.setVisibility(View.GONE);
        btn_update_all.setVisibility(View.GONE);
        btn_delete_all.setVisibility(View.GONE);

        btn_update_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final JSONArray jsonArray = new JSONArray();


                Log.e("####TOTLA STUDENT:-", String.valueOf(totalstudent));

                for (int i = 0; i <totalstudent-1; i++) {

                    if (CustomAdpForStudent.lsCheck.get(i).get("stat").equals("P")) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("studentid", CustomAdpForStudent.lsCheck.get(i).get("studentid"));
                            Log.e("####studentid", CustomAdpForStudent.lsCheck.get(i).get("studentid"));
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }


                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Login");


                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View view1 = layoutInflater.inflate(R.layout.dialogdesign, null);
                edt_stud_new_sem = (EditText) view1.findViewById(R.id.edt_stud_new_sem);
                edt_stud_new_ldate = (EditText) view1.findViewById(R.id.edt_stud_new_ldate);
                swch_stud_status = (Switch) view1.findViewById(R.id.swch_stud_status);
                swch_stud_status.setChecked(true);
                edt_stud_new_ldate.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            DatePickerDialog.OnDateSetListener mDateSetListener
                                    = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                    a = i;
                                    a1 = i1;
                                    a2 = i2;
                                    int month = a1 + 1;
                                    edt_stud_new_ldate.setText("" + a2 + "-" + month + "-" + a);

                                }
                            };

                            new DatePickerDialog(getContext(), mDateSetListener, a, a1, a2).show();
                        }
                        return false;
                    }
                });

                if (swch_stud_status.isChecked()) {
                    strstatus = "Active";
                } else {
                    strstatus = "Deactive";
                }
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        String strnewsem = edt_stud_new_sem.getText().toString();
                        String strldate = edt_stud_new_ldate.getText().toString();

                        update_student_all(jsonArray, strnewsem, strldate, strstatus);
                        cbforall.setChecked(false);
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                builder.setView(view1);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        btn_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final JSONArray jsonArray = new JSONArray();
                Log.e("####TOTLA STUDENT:-", String.valueOf(totalstudent));

                for (int i = 0; i < totalstudent; i++) {


                    if (CustomAdpForStudent.lsCheck.get(i).get("stat").equals("P")) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("studentid", CustomAdpForStudent.lsCheck.get(i).get("studentid"));
                            Log.e("####studentid", CustomAdpForStudent.lsCheck.get(i).get("studentid"));
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }


                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.ic_warning_black_24dp);
                builder.setMessage("Are You Sure To Delete?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                builder.create().show();
            }
        });
        cbforall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbforall.isChecked() == true) {
                    list_Student.setAdapter(new CustomAdpForStudent(getActivity(), lsforstudent, true));

                } else {
                    list_Student.setAdapter(new CustomAdpForStudent(getActivity(), lsforstudent, false));
                }
            }
        });


        fetch_academic();



        spn_StudentAcademic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                lsforstudent.clear();
                new CustomAdpForStudent(getContext(), lsforstudent, false).notifyDataSetChanged();
                list_Student.setAdapter(new CustomAdpForStudent(getActivity(), lsforstudent, false));
                if (i == 0) {
                    academic = null;
                    txtselectall.setVisibility(View.GONE);
                    cbforall.setVisibility(View.GONE);
                    btn_update_all.setVisibility(View.GONE);
                    btn_delete_all.setVisibility(View.GONE);

                } else {

                    academic = lsforacademic.get(i);
                    fetch_academicId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        list_Student.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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


        btn_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final JSONArray jsonArray = new JSONArray();
                Log.e("####TOTLA STUDENT:-", String.valueOf(totalstudent));

                for (int i = 0; i < totalstudent; i++) {
                    if (CustomAdpForStudent.lsCheck.get(i).get("stat").equals("P")) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("studentid", CustomAdpForStudent.lsCheck.get(i).get("studentid"));
                            Log.e("####studentid", CustomAdpForStudent.lsCheck.get(i).get("studentid"));
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Log.e("#######JSONARRAY:-", String.valueOf(jsonArray));


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.ic_warning_black_24dp);
                builder.setMessage("Are You Sure To Delete?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete_all_student(jsonArray);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

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
                lsforSearchedstudent.clear();
                search_faculty_data(query, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lsforSearchedstudent.clear();
                search_faculty_data(newText, true);
                return false;
            }
        });


        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(StudentFragment.this.getActivity(), addstudentActivity.class);
                intent.putExtra("studentsid", lsforstudent.get(pos).get("studentid"));
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
                        delId = lsforstudent.get(pos).get("studentid");
                        delete_student();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        cbforall.setChecked(false);
        spn_StudentAcademic.setSelection(0);
        cbforall.setVisibility(View.GONE);
        btn_update_all.setVisibility(View.GONE);
        txtselectall.setVisibility(View.GONE);


    }

    public void delete_student() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Deleting DATA");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/delete_student_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int i = jsonObject.getInt("status");

                    if (i == 0) {
                        Toast.makeText(getContext(), "Not Deleted", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else if (i == 1) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();



                        getFragmentManager().beginTransaction().detach(StudentFragment.this).attach(StudentFragment.this).commit();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
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
                map.put("studentid", delId);

                return map;
            }
        };

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void delete_all_student(final JSONArray jsonArray) {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Deleting DATA");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/delete_all_student.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int i = jsonObject.getInt("status");

                    if (i == 0) {
                        Toast.makeText(getContext(), "Not Deleted", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else if (i == 1) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();

                        getFragmentManager().beginTransaction().detach(StudentFragment.this).attach(StudentFragment.this).commit();


                    }


                    getFragmentManager().beginTransaction().detach(StudentFragment.this).attach(StudentFragment.this).commit();


                } catch (JSONException e) {
                    e.printStackTrace();
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
                map.put("studentobj", String.valueOf(jsonArray));

                return map;
            }
        };

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void update_student_all(final JSONArray jsonArray, final String strnewsem, final String strldate, final String strstatus) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/update_all_student.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    Log.d("resposeee", response);

                    JSONObject jsonObject = new JSONObject(response);

                    getFragmentManager().beginTransaction().detach(StudentFragment.this).attach(StudentFragment.this).commit();





                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Snackbar.make(getView(), "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("sem", strnewsem);
                map.put("ldate", strldate);
                map.put("status", strstatus);
                map.put("studentobj", String.valueOf(jsonArray));
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void fetch_academicId() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "academic_year_master/select_academic_year.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int i = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (academic.equals(jsonObject1.get("academic_text"))) {
                            academicId = jsonObject1.getString("academicid");
                            fetch_Student();
                        }


                    }


                    if (i == jsonArray.length()) {
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Snackbar.make(view, "ERROR", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(view, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void fetch_academic() {


        lsforacademic.clear();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "academic_year_master/select_academic_year.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int i = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    if (jsonArray.length()<1)
                    {
                        txt_nodata.setVisibility(View.VISIBLE);
                    }
                    else {
                        txt_nodata.setVisibility(View.GONE);
                    }

                    lsforacademic.add("Select Academic Year");

                    spn_StudentAcademic.setVisibility(View.VISIBLE);
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        lsforacademic.add(jsonObject1.getString("academic_text"));
                        spn_StudentAcademic.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lsforacademic));
                    }


                    if (i == jsonArray.length()) {
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Snackbar.make(view, "ERROR", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(view, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);


    }

    public void fetch_Student() {

        lsforstudent.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/select_student_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int i = 0, j = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    if (jsonArray.length()<1)
                    {
                        txt_nodata.setVisibility(View.VISIBLE);
                    }
                    else {
                        txt_nodata.setVisibility(View.GONE);
                    }
                    for (i = 0, j = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (academicId.equals(jsonObject1.getString("academicid"))) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("location", String.valueOf(j));
                            map.put("studentid", jsonObject1.getString("studentid"));
                            map.put("enrollNo", jsonObject1.getString("enrollmentno"));
                            map.put("name", jsonObject1.getString("firstname") + " " + jsonObject1.getString("lastname"));
                            map.put("mobNo", jsonObject1.getString("mobno"));
                            map.put("sem", jsonObject1.getString("semester"));
                            map.put("emailId", jsonObject1.getString("emailid"));
                            map.put("Dob", jsonObject1.getString("dob"));
                            map.put("Doj", jsonObject1.getString("doj"));
                            map.put("Dol", jsonObject1.getString("dol"));
                            map.put("City", jsonObject1.getString("city"));
                            map.put("Address", jsonObject1.getString("address"));
                            map.put("status", jsonObject1.getString("status"));
                            map.put("dpurl", jsonObject1.getString("dpurl"));

                            j++;
                            totalstudent = j;
                            lsforstudent.add(map);
                            // list_Student.setAdapter((ListAdapter) adp);
                            list_Student.setAdapter(new CustomAdpForStudent(getActivity(), lsforstudent, false));

                        }



                    }


                    if (j!=0)
                    {
                        txtselectall.setVisibility(View.VISIBLE);
                        cbforall.setVisibility(View.VISIBLE);
                        btn_update_all.setVisibility(View.VISIBLE);
                        btn_delete_all.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        txtselectall.setVisibility(View.GONE);
                        cbforall.setVisibility(View.GONE);
                        btn_update_all.setVisibility(View.GONE);
                        btn_delete_all.setVisibility(View.GONE);
                    }

                    if (i == jsonArray.length()) {
                        obswipeRefreshLayout.setRefreshing(false);
                    }


                } catch (JSONException e) {
                    obswipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(view, "ERROR" + e, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                obswipeRefreshLayout.setRefreshing(false);
                Snackbar.make(view, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void search_faculty_data(final String searchText, boolean search) {

        if (search == true) {
            for (int i = 0; i < lsforstudent.size(); i++) {
                HashMap<String, String> map = lsforstudent.get(i);

                if ((map.get("name").toLowerCase()).contains(searchText.toLowerCase()) ||
                        (map.get("name").toUpperCase()).contains(searchText.toUpperCase())) {
                    lsforSearchedstudent.add(lsforstudent.get(i));
                }
            }

            list_Student.setAdapter(new CustomAdpForStudent(getActivity(), lsforSearchedstudent, false));

            obswipeRefreshLayout.setRefreshing(false);
        } else {
            list_Student.setAdapter(new CustomAdpForStudent(getActivity(), lsforSearchedstudent, false));
            obswipeRefreshLayout.setRefreshing(false);
        }
    }
}
