package apex.mams.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
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
import android.widget.Toolbar;

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

    EditText notice_title, notice_discription, edt_FileName;
    Switch swi_status;
    Spinner notice_type;
    List<String> ls = new ArrayList<String>();
    ArrayAdapter adp;
    Button btn_addnotice, btn_attachFile;
    Integer selectedType = 0;
    String noticeType, ID, rdstatus, notice_by = "By Admin";
    Integer selected = 0;


    Spinner spin_sem_notice;
    String oldFile,oldSize;
    List<String> ls_sem_notice = new ArrayList<String>();
    String Selected_sem = "0", fileSize = "0 kb";

    Getip getip;
    View parentLayout;


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

        getip = new Getip(getApplicationContext());
        notice_title = (EditText) findViewById(R.id.notice_title);
        notice_discription = (EditText) findViewById(R.id.notice_discription);
        edt_FileName = (EditText) findViewById(R.id.edt_FileName);
        swi_status = (Switch) findViewById(R.id.swi_status);
        notice_type = (Spinner) findViewById(R.id.notice_type);
        btn_addnotice = (Button) findViewById(R.id.btn_addnotice);
        btn_attachFile = (Button) findViewById(R.id.btn_attachFile);
        spin_sem_notice = (Spinner) findViewById(R.id.spin_sem_notice);
        parentLayout = findViewById(android.R.id.content);
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);


        ls.add("For Student Only");
        ls.add("For Faculty Only");
        ls.add("For Both(Student&Faculty)");


        ls_sem_notice.add("For All Sem");
        ls_sem_notice.add("1");
        ls_sem_notice.add("2");
        ls_sem_notice.add("3");
        ls_sem_notice.add("4");
        ls_sem_notice.add("5");
        ls_sem_notice.add("6");
        spin_sem_notice.setAdapter(new ArrayAdapter(addnoticeActivity.this, android.R.layout.simple_list_item_1, ls_sem_notice));
        adp = new ArrayAdapter(addnoticeActivity.this, android.R.layout.simple_list_item_1, ls);
        notice_type.setAdapter(adp);

        swi_status.setChecked(true);

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
        notice_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = i;

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

        if (getIntent().getStringExtra("ch").equals("Update")) {
            ID = getIntent().getStringExtra("id");
            selected = 1;
            fetch_notice();
            btn_addnotice.setText("Update Notice");
        }


        btn_addnotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagforerror = 0;

                if (notice_title.getText().toString().equals("")) {
                    notice_title.setError("Notice Title Is Required");
                    flagforerror = 1;
                }

                if (notice_discription.getText().toString().equals("")) {
                    notice_discription.setError("Notice Discription Is Required");
                    flagforerror = 1;
                }


                if (flagforerror == 0) {
                    if (selectedType == 0) {
                        noticeType = "STUDENT";
                    } else if (selectedType == 1) {
                        noticeType = "FACULTY";
                    } else if (selectedType == 2) {
                        noticeType = "BOTH";
                    }


                    if (swi_status.isChecked())
                        rdstatus = "Active";
                    else
                        rdstatus = "Deactive";


                    if (selected == 0) {


                        if (edt_FileName.getText().toString().equals("")) {
                            add_notice();

                            if (noticeType.equals("FACULTY")) {
                                notification();
                            } else if (noticeType.equals("STUDENT")) {
                                notificationStud();
                            } else {
                                notification();
                                notificationStud();
                            }
                        } else {
                            uploadFile(0);

                            if (noticeType.equals("FACULTY")) {
                                notification();
                            } else if (noticeType.equals("STUDENT")) {
                                notificationStud();
                            } else {
                                notification();
                                notificationStud();
                            }

                        }

                    } else if (selected == 1) {
//                        update_notice();
//                        if (noticeType.equals("FACULTY")) {
//                            notification();
//                        } else if (noticeType.equals("STUDENT")) {
//                            notificationStud();
//                        } else {
//                            notification();
//                            notificationStud();
//                        }

                        if (edt_FileName.getText().toString().equals(oldFile)) {
                            update_notice(0);
                            if (noticeType.equals("FACULTY")) {
                                notification();
                            } else if (noticeType.equals("STUDENT")) {
                                notificationStud();
                            } else {
                                notification();
                                notificationStud();
                            }
                        } else {
                            uploadFile(1);

                            if (noticeType.equals("FACULTY")) {
                                notification();
                            } else if (noticeType.equals("STUDENT")) {
                                notificationStud();
                            } else {
                                notification();
                                notificationStud();
                            }

                        }
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
            Log.e("FILE PATHURI::-", pathFile);
            Log.e("FILE PATHURI::-", getApplicationContext().getPackageName());


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


    private void uploadFile(final int ch) {
        if (filePath != null) {


            storageReference = FirebaseStorage.getInstance().getReference();
            storageReference = storageReference.child("Notice/" + edt_FileName.getText().toString());
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();
            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ImageUpload imageUpload = new ImageUpload(edt_FileName.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                            String uploadId = databaseReference.push().getKey();

                            databaseReference.child(uploadId).setValue(imageUpload);

                            if (ch==0)
                                add_notice();
                            else
                                update_notice(1);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            progressDialog.dismiss();
                            Toast.makeText(addnoticeActivity.this, "FILE INSERTION ERROR   "+exception.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
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

//                        notification();
                        Toast.makeText(addnoticeActivity.this, "Notice added successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        finish();
                    } else if (st == 0) {
                        Toast.makeText(addnoticeActivity.this, "Notice can't added ", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(parentLayout, "ERROR", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(parentLayout, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("notice_title", notice_title.getText().toString());
                map.put("notice_discription", notice_discription.getText().toString());
                map.put("notice_status", rdstatus);
                map.put("notice_type", noticeType);
                map.put("noticeby", "ADMIN");
                map.put("notice_sem", Selected_sem);
                map.put("notice_size", fileSize);


                if (edt_FileName.getText().toString().equals("")) {
                    map.put("noticefile", "");

                } else {
                    map.put("noticefile", edt_FileName.getText().toString());
                }
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
                            edt_FileName.setText(jsonObject1.getString("noticefile"));

                            if (jsonObject1.getString("notice_status").equals("Active")) {
                                swi_status.setChecked(true);
                            } else {
                                swi_status.setChecked(false);
                            }
                            if (jsonObject1.getString("notice_type").equals("STUDENT")) {
                                notice_type.setSelection(0);
                            } else if (jsonObject1.getString("notice_type").equals("FACULTY")) {
                                notice_type.setSelection(1);
                            } else if (jsonObject1.getString("notice_type").equals("BOTH")) {
                                notice_type.setSelection(2);
                            }


                            oldFile=jsonObject1.getString("noticefile");
                            oldSize=jsonObject1.getString("notice_size");
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
                Snackbar.make(parentLayout, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                progressDialog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void update_notice(final int chForFile) {
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
                    Snackbar.make(parentLayout, "ERROR" + e.getMessage(), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(parentLayout, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("noticeid", ID);
                map.put("notice_title", notice_title.getText().toString());
                map.put("notice_discription", notice_discription.getText().toString());
                map.put("notice_status", rdstatus);
                map.put("notice_type", noticeType);
                map.put("notice_sem", Selected_sem);

                if (chForFile==0)
                {
                    map.put("notice_size", oldSize);
                    map.put("noticefile", oldFile);

                }
                else
                {
                    map.put("notice_size", fileSize);
                    map.put("noticefile", edt_FileName.getText().toString());
                }
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void notification() {


        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("msg", "By Admin").build();

        final okhttp3.Request request = new okhttp3.Request.Builder().url(getip.getUrl() + "notification/SendNotificationfac.php?msg=" + "By Admin").post(body).build();

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


        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("msg", "By Admin").build();

        final okhttp3.Request request = new okhttp3.Request.Builder().url(getip.getUrl() + "notification/SendNotificationStud.php?msg=" + "By Admin").post(body).build();

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
}
