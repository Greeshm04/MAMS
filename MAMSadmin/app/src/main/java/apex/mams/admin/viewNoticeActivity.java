package apex.mams.admin;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class viewNoticeActivity extends AppCompatActivity {

    TextView txt_notice_id, txt_notice_Title, txt_notice_Description, txt_notice_Date, txt_notice_Type, txt_notice_status;
    Button btn_downFile;
    String noticeid = "";
    Getip getip;
    Toolbar toolbar;
    View parentLayout;


    ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    String name, url;
    DownloadManager downloadManager;
    int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notice);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        Intent intent = getIntent();
        noticeid = intent.getStringExtra("id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getip = new Getip(this);
        parentLayout = findViewById(android.R.id.content);

        txt_notice_id = (TextView) findViewById(R.id.txt_notice_id);
        txt_notice_Title = (TextView) findViewById(R.id.txt_notice_Title);
        txt_notice_Description = (TextView) findViewById(R.id.txt_notice_Description);
        txt_notice_Date = (TextView) findViewById(R.id.txt_notice_Date);
        txt_notice_Type = (TextView) findViewById(R.id.txt_notice_Type);
        txt_notice_status = (TextView) findViewById(R.id.txt_notice_status);
        btn_downFile = (Button) findViewById(R.id.btn_downFile);

        fill_notice_data();

        btn_downFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(viewNoticeActivity.this, "" + url + name, Toast.LENGTH_SHORT).show();
                DownloadFromUrl(url, name);

            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("###############", "BACK");
        url = null;
        name = null;

        finish();
    }


    public void fetchUrl() {
        progressDialog = new ProgressDialog(viewNoticeActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference(addnoticeActivity.DATABASE_PATH);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        progressDialog.dismiss();
                        ImageUpload img = snapshot.getValue(ImageUpload.class);
                        String url2 = img.getUrl();
                        String name2 = img.getName();
//                    Toast.makeText(ImageListActivity.this, ""+name, Toast.LENGTH_SHORT).show();
                        Log.e("#####################", name2 + name);
                        Log.e("#####################", url2);

                        if (name2.equals(name.toString())) {
                            url = url2;
                            Log.e("###########::::::::::::", name);
                            Log.e("#############::::::::::", url);
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

    public void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {

            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            URL url = new URL(imageURL);
            File file = new File(path + "/" + fileName);

            long startTime = System.currentTimeMillis();

            Log.e("ImageManager", "download begining");
            Log.e("ImageManager", "download url:" + imageURL);
            Log.e("ImageManager", "downloaded file name:" + fileName);
                        /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();



                        /*
                         * Define InputStreams to read from the URLConnection.
                         */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

                        /*
                         * Read bytes to the Buffer until there is nothing more to read(-1).
                         */
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[50];

//            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read(data, 0, data.length)) != -1) {
//                baf.append((byte) current);
                buffer.write(data, 0, current);
            }

                        /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer.toByteArray());
            fos.close();
            Log.d("ImageManager", "download ready in"
                    + ((System.currentTimeMillis() - startTime) / 1000)
                    + " sec");

            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(imageURL);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            Long aLong = downloadManager.enqueue(request);

        } catch (Exception e) {
            Log.e("ImageManager", "Error: " + e.toString());
        }


//        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
//        startActivity(intent);

    }

    public void fill_notice_data() {
        final ProgressDialog progressDialog = new ProgressDialog(viewNoticeActivity.this);
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
                    for (i = 0; i <= jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if (noticeid.equals(jsonObject1.getString("noticeid"))) {

                            txt_notice_id.setText("Notice ID := " + jsonObject1.getString("noticeid"));
                            txt_notice_Title.setText("Title := " + jsonObject1.getString("notice_title"));
                            txt_notice_Description.setText("Description := " + jsonObject1.getString("notice_discription"));
                            txt_notice_Date.setText("Date := " + jsonObject1.getString("notice_date"));
                            txt_notice_Type.setText("Type := " + jsonObject1.getString("notice_type"));
                            txt_notice_status.setText("Status := " + jsonObject1.getString("notice_status"));
                            Toast.makeText(viewNoticeActivity.this, "#####" + jsonObject1.getString("noticefile"), Toast.LENGTH_SHORT).show();
                            Log.e("########", jsonObject1.getString("noticefile"));
                            name = jsonObject1.getString("noticefile");
                            progressDialog.dismiss();

                            flag = 1;
                            if (!name.equals("")) {
                                fetchUrl();

                            } else {
                                btn_downFile.setVisibility(View.GONE);

                            }
                            break;
                        }
                    }

                    if (i == jsonArray.length()) {
                        progressDialog.dismiss();
                    }


                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Snackbar.make(parentLayout, "ERROR", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(parentLayout, "Error Occured", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);

    }


}