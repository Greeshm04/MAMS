package apex.mams.faculty;

import android.app.Service;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by admin on 12-02-2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    Getip getip;
    static String token;

    @Override
    public void onTokenRefresh() {
        
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d("@@@@@", token);
    }
}
