package apex.mams.admin;


import android.content.Context;

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
