package apex.mams.student;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by LENOVO on 2/13/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    static String token;
    @Override
    public void onTokenRefresh() {


        token= FirebaseInstanceId.getInstance().getToken();
        Log.d("@@@@@",token);

    }

    public static void registertoken(String token)
    {
        OkHttpClient client=new OkHttpClient();
        RequestBody body=new FormBody.Builder().add("Token",token).build();
        Request request=new Request.Builder().url("http://192.168.43.44/mams/notification/RegisterTokenStud.php").post(body).build();
        try {
            Log.e("########","1");
            client.newCall(request).execute();
            Log.e("########","2");

        }catch (IOException e){
            Log.e("########","3"+e.getMessage());
            e.printStackTrace();
        }


    }
}
