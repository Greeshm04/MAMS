package apex.mams.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    ProgressBar prb_profile;
    Button btn_attach;
    ImageView img_profile;
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    String old_password = "";
    String filename;
    String name, url;
    Getip getip;
    ProgressDialog progressDialog;
    MyPref myPref;

    EditText edt_fname, edt_lname, edt_addr, edt_email, edt_city; //edt_pass, edt_cpass;

    Button btn_update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        img_profile = findViewById(R.id.img_profile);
        getip = new Getip(this);
        myPref = new MyPref(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("fac");

        btn_update = findViewById(R.id.btn_update);
        edt_fname = findViewById(R.id.edt_fname);
        edt_lname = findViewById(R.id.edt_lname);
        edt_addr = findViewById(R.id.edt_addr);
        edt_email = findViewById(R.id.edt_email);
        edt_city = findViewById(R.id.edt_city);
        prb_profile = findViewById(R.id.prb_profile);
        prb_profile.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        edt_pass = findViewById(R.id.edt_pass);
//        edt_cpass = findViewById(R.id.edt_cpass);

        fetch_Student_profile();
        Log.e("######PROFILEidd:", String.valueOf(myPref.getID().toString()));

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFile();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!edt_pass.getText().toString().equals(edt_cpass.getText().toString()) && !edt_pass.getText().toString().equals("") && edt_cpass.getText().toString().equals("")) {
//                    edt_cpass.setError("Password Must be Same");
//                } else


                update_student_master_profile();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public void fetch_Student_profile() {
        final ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setMessage("Fetching Profile Picture");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/select_student_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int i = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (jsonObject1.getString("studentid").equals(myPref.getID().toString())) {
                            url = jsonObject1.getString("dpurl");
                            edt_fname.setText(jsonObject1.getString("firstname"));
                            edt_lname.setText(jsonObject1.getString("lastname"));
                            edt_email.setText(jsonObject1.getString("emailid"));
                            edt_addr.setText(jsonObject1.getString("address"));
                            edt_city.setText(jsonObject1.getString("city"));
                            old_password = jsonObject1.getString("password");
                            Log.e("######PROFILEurl:", url);
                            Log.e("######PROFILEid:", String.valueOf(myPref.getID().toString()));

                        }
                    }

                    Glide.with(UpdateProfileActivity.this).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(img_profile) {

                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            img_profile.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    prb_profile.setVisibility(View.GONE);


                    if (i == jsonArray.length()) {
                        progressDialog.dismiss();
                    }


                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.e("##############PROFILE", "ERROR" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e("##############PROFILE", "ERROR OCCUERED" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("studentid", String.valueOf(myPref.getID().toString()));
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void showFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select A Document"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            filename = filePath.getLastPathSegment() + ".jpeg";
            Log.e("FILENAME", filename);
            if (!filename.equals("")) {
                prb_profile.setVisibility(View.VISIBLE);
                uploadFile();
            }
        }
    }

    private void uploadFile() {
        if (filePath != null) {


            storageReference = FirebaseStorage.getInstance().getReference();
            storageReference = storageReference.child("Student/" + filename);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();
            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(UpdateProfileActivity.this, "SUCCESSFULLY UPLOADED", Toast.LENGTH_SHORT).show();
                            ImageUpload imageUpload = new ImageUpload(filename, taskSnapshot.getDownloadUrl().toString());
                            String uploadId = databaseReference.push().getKey();
                            Log.e("#############URL:-", taskSnapshot.getDownloadUrl().toString());
                            databaseReference.child(uploadId).setValue(imageUpload);

                            fetchUrl(filename);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            progressDialog.dismiss();

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

        }
    }

    public void fetchUrl(String fname) {
        name = fname;
        progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setMessage("Preparing For Download...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("fac");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ImageUpload img = snapshot.getValue(ImageUpload.class);
                        String url2 = img.getUrl();
                        String name2 = img.getName();

                        Log.e("#####################", name2 + name);
                        Log.e("#####################", url2);

                        if (name2.equals(name.toString())) {
                            url = url2;


                            progressDialog.dismiss();
                            Log.e("###########::::::::::::", name);
                            Log.e("#############::::::::::", url);

                            update_faculty_master_profile_pic();
                            break;
                        }


                    }


                } catch (Exception e) {
                    Toast.makeText(UpdateProfileActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void update_student_master_profile() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating An Account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/update_profile.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");

                    progressDialog.dismiss();

                    if (st == 1) {
                        Toast.makeText(UpdateProfileActivity.this, "Student Updated Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateProfileActivity.this, StudentDashboardActivity.class);
                        startActivity(intent);


                    } else if (st == 0) {
                        Toast.makeText(UpdateProfileActivity.this, "Student can't Updated ", Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("##########", "ERROR" + e.getMessage());
                    progressDialog.dismiss();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("##########", "Error Occured" + error.getMessage());
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("studentid", String.valueOf(myPref.getID().toString()));
                map.put("firstname", edt_fname.getText().toString());
                map.put("lastname", edt_lname.getText().toString());
                map.put("address", edt_addr.getText().toString());
                map.put("emailid", edt_email.getText().toString());
                map.put("city", edt_city.getText().toString());
                //   if (edt_cpass.getText().toString().equals(""))
                map.put("password", old_password);
//                else
//                    map.put("password", edt_pass.getText().toString());
                return map;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void update_faculty_master_profile_pic() {


        Log.e("URL:-", url);


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating An Account...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/update_url.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int st = jsonObject.getInt("status");


                    if (st == 1) {
                        Toast.makeText(UpdateProfileActivity.this, "Student URL Updated Successfully", Toast.LENGTH_SHORT).show();
                        Glide.with(UpdateProfileActivity.this).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(img_profile) {

                            @Override
                            protected void setResource(Bitmap resource) {
                                super.setResource(resource);
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                img_profile.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                        prb_profile.setVisibility(View.GONE);

                        progressDialog.dismiss();
                    } else if (st == 0) {
                        Toast.makeText(UpdateProfileActivity.this, "Student URL can't Updated ", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("##########", "ERROR" + e.getMessage());
                    progressDialog.dismiss();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("##########", "Error Occured" + error.getMessage());
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("studentid", String.valueOf(myPref.getID().toString()));
                map.put("dpurl", url);
                return map;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
