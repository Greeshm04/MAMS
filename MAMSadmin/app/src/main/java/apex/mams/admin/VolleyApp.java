package apex.mams.admin;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;

/**
 * Created by LENOVO on 7/3/2017.
 */
public class VolleyApp extends Application {
    private static VolleyApp volleyApp;
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        volleyApp=this;
        requestQueue= Volley.newRequestQueue(this);

    }

    public synchronized static VolleyApp getVolleyApp() {
        return volleyApp;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }



    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        try {
            File file = url;
//            Uri uri = Uri.fromFile(file);



            Uri uri= FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",file);
            Log.e("FILE ", String.valueOf(file));
            Log.e("ImageManager", String.valueOf(uri));
            Intent intent = new Intent(Intent.ACTION_VIEW);

            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file

            if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("doc") || file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("ppt") || file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("xls") || file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("zip") || file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("wav") || file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("jpg") ||
                    file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("jpeg") ||
                    file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("3gp") ||
                    file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("mpg") ||
                    file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("mpeg") ||
                    file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("mpe") ||
                    file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("mp4") ||
                    file.toString().substring(file.toString().lastIndexOf(".") + 1).equalsIgnoreCase("avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            }

            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Log.e("Open","Error",e);
        }
    }

    public static boolean IsNotEmpty(EditText[] edt) {

        boolean flag=true;
        for (int i=0;i<edt.length;i++){
            if (edt[i].getText().toString().equals("")){
                flag=false;
                edt[i].setError("Empty Field");
            }
        }
        return flag;
    }
}
