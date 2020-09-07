package apex.mams.faculty;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addMaterialActivity extends AppCompatActivity {

    EditText material_title, material_description;
    EditText edt_FileName;
    Button btn_attachFile;
    Switch swi_status;
    Button btn_addmaterial;
    int selected = 0;
    String rdstatus;
    String ID;
    Getip getip;
    String materialby;
    String oldFile, newFile;
    String url;
    ProgressDialog progressDialog;
    String[] arrsemformaterial;
    MyPref myPref;

    String Selected_sem = "", fileSize = "0 kb";
    String Selected_sub = "";

    Spinner spin_semformaterial;
    List<String> ls_semformaterial = new ArrayList<String>();

    Spinner spin_subformaterial;
    List<String> ls_subformaterial = new ArrayList<String>();

    private static final int PICK_IMAGE_REQUEST = 234;
    public static final String DATABASE_PATH = "image";
    private Uri filePath;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_add_material);

        getip = new Getip(this);
        myPref = new MyPref(this);
        material_title = findViewById(R.id.material_title);
        material_description = findViewById(R.id.material_description);
        edt_FileName = findViewById(R.id.edt_FileName);
        btn_attachFile = findViewById(R.id.btn_attachFile);
        spin_semformaterial = findViewById(R.id.spin_semformaterial);
        spin_subformaterial = findViewById(R.id.spin_subformaterial);
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swi_status = findViewById(R.id.swi_status);

        btn_addmaterial = findViewById(R.id.btn_addmaterial);

        ls_semformaterial.add("Select Semester");
        ls_subformaterial.add("Select Subject");

        for (int i = 0; i < myPref.loadArray("MainSemester").length; i++) {
            ls_semformaterial.add(myPref.loadArray("MainSemester")[i]);
        }
        spin_semformaterial.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, ls_semformaterial));
        spin_subformaterial.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, ls_subformaterial));


        spin_semformaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    ls_subformaterial.clear();
                    ls_subformaterial.add("Select Subject");

                    spin_subformaterial.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ls_subformaterial));
                } catch (NullPointerException e) {
                }

                if (position != 0) {
                    Selected_sem = ls_semformaterial.get(position);

                    for (int i = 0; i < myPref.loadArray("MainSemester").length; i++) {
                        if (Selected_sem.equals(myPref.loadArray("SubjectSem")[i])) {
                            ls_subformaterial.add(myPref.loadArray("SubjectName")[i]);
                        }
                    }
                    spin_subformaterial.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ls_subformaterial));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spin_subformaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < myPref.loadArray("SubjectName").length; i++) {

                    if (myPref.loadArray("SubjectName")[i].equals(ls_subformaterial.get(position))) {
                        Selected_sub = myPref.loadArray("SubjectId")[i];
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (getIntent().getStringExtra("ch").equals("Update")) {
            ID = getIntent().getStringExtra("id");
            Log.e("###########ID", ID);
            selected = 1;
            fetch_material();
            btn_addmaterial.setText("Update Material");
        }


        btn_addmaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swi_status.isChecked())
                    rdstatus = "Active";
                else
                    rdstatus = "Deactive";

                if (material_title.getText().toString().equals("")) {
                    material_title.setError("Material Title Is Required");
                } else if (material_description.getText().toString().equals("")) {
                    material_description.setError("Material Description Is Required");
                } else if (Selected_sem.equals("")) {
                    Toast.makeText(addMaterialActivity.this, "Select Semester", Toast.LENGTH_SHORT).show();
                } else if (Selected_sub.equals("")) {
                    Toast.makeText(addMaterialActivity.this, "Select Subject", Toast.LENGTH_SHORT).show();
                } else if (edt_FileName.getText().toString().equals("")) {
                    Toast.makeText(addMaterialActivity.this, "Select File", Toast.LENGTH_SHORT).show();
                } else {

                    if (selected == 0)
                        uploadFile();
                    else
                        checkForUpdatedFile();

                }

            }
        });


        btn_attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFile();
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

            Log.e("FILEUPDATE", "UPLOAD");

            storageReference = FirebaseStorage.getInstance().getReference();
            storageReference = storageReference.child("Material/" + edt_FileName.getText().toString());
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
                            Toast.makeText(addMaterialActivity.this, "SUCCESSFULLY UPLOADED", Toast.LENGTH_SHORT).show();

                            ImageUpload imageUpload = new ImageUpload(edt_FileName.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                            String uploadId = databaseReference.push().getKey();
                            if (selected == 0)
                                addMaterial();
                            else
                                updateMaterial();
                            databaseReference.child(uploadId).setValue(imageUpload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            progressDialog.dismiss();
                            Toast.makeText(addMaterialActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void fetch_material() {
        final ProgressDialog progressDialog = new ProgressDialog(addMaterialActivity.this);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "material_master/select_material_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    int i;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (ID.equals(jsonObject1.getString("materialid"))) {
                            material_title.setText(jsonObject1.getString("material_title"));
                            material_description.setText(jsonObject1.getString("material_description"));
                            edt_FileName.setText(jsonObject1.getString("material_file"));
                            oldFile = jsonObject1.getString("material_file");
                            if (jsonObject1.getString("material_status").equals("Active")) {
                                swi_status.setChecked(true);
                            } else {
                                swi_status.setChecked(false);
                            }


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
                Toast.makeText(addMaterialActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void updateMaterial() {
        Log.e("FILEUPDATE", "UPDATE");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "material_master/update_material_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addMaterialActivity.this, "Material Updated Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    } else if (st == 0) {
                        Toast.makeText(addMaterialActivity.this, "Material can't Updated ", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(addMaterialActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    Log.e("#########Error", "ERROR" + e);

                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(addMaterialActivity.this, "Error Occured" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("#########Error", "Error Occured" + error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("materialid", ID);
                map.put("subjectid", Selected_sub);
                map.put("material_title", material_title.getText().toString());
                map.put("material_description", material_description.getText().toString());
                map.put("material_file", edt_FileName.getText().toString());
                map.put("material_status", rdstatus);
                map.put("material_sem", Selected_sem);
                map.put("materialby", FacultyDashboardActivity.user);

                Log.e("#############BY", "" + FacultyDashboardActivity.user);
                Log.e("#############BY", "" + ID);
                Log.e("#############BY", "" + Selected_sub);
                Log.e("#############BY", "" + material_title.getText().toString());
                Log.e("#############BY", "" + material_description.getText().toString());
                Log.e("#############BY", "" + edt_FileName.getText().toString());
                Log.e("#############BY", "" + rdstatus);
                Log.e("#############BY", "" + Selected_sem);

                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void addMaterial() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adding A Notice..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "material_master/insert_material_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    if (st == 1) {
                        Toast.makeText(addMaterialActivity.this, "Material Added Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    } else if (st == 0) {
                        Toast.makeText(addMaterialActivity.this, "Material can't Added", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(addMaterialActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                error.printStackTrace();
                Toast.makeText(addMaterialActivity.this, "Error Occured" + error, Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subjectid", Selected_sub);
                Log.e("%", Selected_sub);
                map.put("material_title", material_title.getText().toString());
                map.put("material_description", material_description.getText().toString());
                map.put("material_file", edt_FileName.getText().toString());
                map.put("material_status", rdstatus);
                map.put("material_sem", Selected_sem);
                map.put("material_size", fileSize);
                map.put("materialby", FacultyDashboardActivity.user);
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }

    public void checkForUpdatedFile() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating A Material..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        if (oldFile.equals(edt_FileName.getText().toString())) {
            updateMaterial();
            Log.e("FILE", "NULL");
        } else {
            fetchUrl();
        }
    }

    public void fetchUrl() {


        databaseReference = FirebaseDatabase.getInstance().getReference(addnoticeActivity.DATABASE_PATH);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    Log.e("FILEUPDATE", "FETCHURL");

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ImageUpload img = snapshot.getValue(ImageUpload.class);
                        String url2 = img.getUrl();
                        String name2 = img.getName();
                        Log.e("#####################", name2 + oldFile);
                        Log.e("#####################", url2);

                        if (name2.equals(oldFile)) {
                            url = url2;
                            Log.e("###########::::::::::::", oldFile);
                            Log.e("#############::::::::::", url);

                            deleteFile();
                            break;
                        }
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
        storageReference = storageReference.child("Material/" + oldFile);
        Log.e("FILEUPDATE", "DELETE");

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                uploadFile();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "May be File not exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
