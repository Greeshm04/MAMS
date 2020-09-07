package apex.mams.faculty;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ADMIN on 17-Nov-17.
 */

public class Getip {

    Context context;
    String url;
    MyPref myPref;

    public Getip(Context context) {
        this.context = context;
        myPref=new MyPref(this.context);
        this.url = "http://"+myPref.getIP()+"/mams/";
    }

    public String getUrl() {
        return url;

    }
}
