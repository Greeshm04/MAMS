package apex.mams.student;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by LENOVO on 1/20/2018.
 */

public class DownloadFunction {
    Context context;

    public DownloadFunction(Context context) {
        this.context = context;
    }

    ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    String name, url;
    DownloadManager downloadManager;


    File path;
    File file;

    public void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {

            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            URL url = new URL(imageURL);
            file = new File(path + "/" + fileName);

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

            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(imageURL);

            DownloadManager.Request request = new DownloadManager.Request(uri);

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            Long aLong = downloadManager.enqueue(request);

            progressDialog.dismiss();

            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        } catch (Exception e) {
            Log.e("ImageManager", "Error: " + e.toString());
        }

//        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
//        startActivity(intent);

    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            try {

                VolleyApp.getVolleyApp().openFile(ctxt, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void fetchUrl(String fname) {
        this.name = fname;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Preparing For Download...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("image");

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

                            Log.e("###########::::::::::::", name);
                            Log.e("#############::::::::::", url);
                            break;
                        }


//                    fetchImage(url,name);
                    }
                    if (!url.equals("")) {
                        DownloadFromUrl(url, name);
                    } else {
                        progressDialog.dismiss();
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
