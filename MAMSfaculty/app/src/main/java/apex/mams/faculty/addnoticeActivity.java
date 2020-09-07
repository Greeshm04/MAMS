package apex.mams.faculty;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class addnoticeActivity extends AppCompatActivity {

    EditText notice_title, notice_discription;
    Switch swi_status;
    Button btn_addnotice;
    String noticeType = "STUDENT", ID, rdstatus;
    Integer selected = 0;
    Getip getip;
    EditText edt_FileName;
    Button btn_attachFile;
    MyPref myPref;

    Spinner spin_sem_notice;
    List<String> ls_sem_notice = new ArrayList<String>();
    String Selected_sem = "0", fileSize = "0 kb";

    String oldFile,oldSize;
    private static final int PICK_IMAGE_REQUEST = 234;
    public static final String DATABASE_PATH = "image";
    private Uri filePath;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotice);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getip = new Getip(this);
        myPref = new MyPref(this);
        notice_title = (EditText) findViewById(R.id.notice_title);
        notice_discription = (EditText) findViewById(R.id.notice_discription);
        btn_addnotice = (Button) findViewById(R.id.btn_addnotice);
        swi_status = (Switch) findViewById(R.id.swi_status);
        edt_FileName = (EditText) findViewById(R.id.edt_FileName);
        btn_attachFile = (Button) findViewById(R.id.btn_attachFile);
        spin_sem_notice = findViewById(R.id.spin_sem_notice);
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        swi_status.setChecked(true);
        edt_FileName.setEnabled(false);
        if (getIntent().getStringExtra("ch").equals("Update")) {
            ID = getIntent().getStringExtra("id");
            Log.e("###########ID", ID);
            selected = 1;
            fetch_notice();
            btn_addnotice.setText("Update Notice");
        }

        ls_sem_notice.add("For All Semester");

        ls_sem_notice.add("1");
        ls_sem_notice.add("2");
        ls_sem_notice.add("3");
        ls_sem_notice.add("4");
        ls_sem_notice.add("5");
        ls_sem_notice.add("6");

        spin_sem_notice.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, ls_sem_notice));

        spin_sem_notice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {
                    Selected_sem = ls_sem_notice.get(position);

                } else {
                    Selected_sem = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFile();
            }
        });


        btn_addnotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swi_status.isChecked())
                    rdstatus = "Active";
                else
                    rdstatus = "Deactive";

                if (notice_title.getText().toString().equals("")) {
                    notice_title.setError("Notice Title Is Required");
                } else if (notice_discription.getText().toString().equals("")) {
                    notice_discription.setError("Notice Discription Is Required");
                } else {

                    if (selected == 0) {
                        if (edt_FileName.getText().toString().equals(""))
                            add_notice();
                        else
                            uploadFile();

                        notificationStud();
                        notification();

                    } else if (selected == 1) {
                        update_notice();

                        notificationStud();
                        notification();
                    }
                }

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showFile() {
        Intent intent = new Intent();
        intent.setType("doc/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select A Document"), PICK_IMAGE_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {


            filePath = data.getData();
            String pathFile = FilePath.getPath(this, data.getData());


            File file = new File(pathFile);


            if (file.exists()) {


                if (file.length() / 1024 > 1000) {
                    fileSize = String.valueOf((file.length() / 1024) / 1024) + " mb";
                } else {
                    fileSize = String.valueOf(file.length() / 1024) + " kb";
                }
                edt_FileName.setText(filePath.getLastPathSegment());
            }
        }
    }


    private void uploadFile() {
        if (filePath != null) {


            storageReference = FirebaseStorage.getInstance().getReference();
            storageReference = storageReference.child("Notice/" + edt_FileName.getText().toString());
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            add_notice();
                            ImageUpload imageUpload = new ImageUpload(edt_FileName.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                            String uploadId = databaseReference.push().getKey();

                            databaseReference.child(uploadId).setValue(imageUpload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            progressDialog.dismiss();
                            Toast.makeText(addnoticeActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage(((int) progress) + "% Uploaded..");
                        }
                    })
            ;
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    public void add_notice() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adding A Notice..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notice_master/insert_notice_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addnoticeActivity.this, "Notice Added Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    } else if (st == 0) {
                        Toast.makeText(addnoticeActivity.this, "Notice can't Added", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(addnoticeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(addnoticeActivity.this, "Error Occured" + error, Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("notice_title", notice_title.getText().toString());
                map.put("notice_discription", notice_discription.getText().toString());
                map.put("notice_status", rdstatus);
                map.put("notice_sem", Selected_sem);
                map.put("notice_type", noticeType);
                map.put("noticeby", FacultyDashboardActivity.user);
                map.put("noticefile", edt_FileName.getText().toString());
                map.put("notice_size", fileSize);
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void fetch_notice() {
        final ProgressDialog progressDialog = new ProgressDialog(addnoticeActivity.this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notice_master/select_notice_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    int i;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (ID.equals(jsonObject1.getString("noticeid"))) {
                            notice_title.setText(jsonObject1.getString("notice_title"));
                            notice_discription.setText(jsonObject1.getString("notice_discription"));
                            if (jsonObject1.getString("notice_status").equals("Active")) {
                                swi_status.setChecked(true);
                            } else {
                                swi_status.setChecked(false);
                            }
                            edt_FileName.setText(jsonObject1.getString("noticefile"));
                            spin_sem_notice.setSelection(Integer.parseInt(jsonObject1.getString("notice_sem")));
                        }
                    }
                    if (i == jsonArray.length()) {
                        progressDialog.dismiss();
                    }


                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(addnoticeActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void update_notice() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating A Notice..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "notice_master/update_notice_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addnoticeActivity.this, "Notice Updated Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    } else if (st == 0) {
                        Toast.makeText(addnoticeActivity.this, "Notice can't Updated ", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(addnoticeActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(addnoticeActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("noticeid", ID);
                map.put("notice_title", notice_title.getText().toString());
                map.put("notice_discription", notice_discription.getText().toString());
                map.put("notice_sem", Selected_sem);
                map.put("notice_status", rdstatus);
                map.put("notice_type", noticeType);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void notification() {

        String by = "by " + FacultyDashboardActivity.user;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("msg", by).build();

        final okhttp3.Request request = new okhttp3.Request.Builder().url(getip.getUrl() + "notification/SendNotification.php?msg=" + by).post(body).build();

        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("###########on failure", "" + e);

                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    Log.e("###########on response", "" + response);
                }
            });

        } catch (Exception e) {
            Toast.makeText(addnoticeActivity.this, "error.", Toast.LENGTH_SHORT).show();
            Log.d("#########exception", "" + e);

            e.printStackTrace();
        }

    }

    public void notificationStud() {

        String by = "by " + FacultyDashboardActivity.user;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("msg", by).build();

        final okhttp3.Request request = new okhttp3.Request.Builder().url(getip.getUrl() + "notification/SendNotificationStud.php?msg=" + by).post(body).build();

        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("###########on failure", "" + e);

                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    Log.e("###########on response", "" + response);
                }
            });

        } catch (Exception e) {
            Toast.makeText(addnoticeActivity.this, "ERROR:" + e, Toast.LENGTH_SHORT).show();
            Log.d("#########exception", "" + e);
            e.printStackTrace();
        }

    }

}

