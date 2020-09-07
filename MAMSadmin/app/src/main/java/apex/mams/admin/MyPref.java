package apex.mams.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by LENOVO on 8/24/2017.
 */

public class MyPref {
    String currip="";
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String KEY_ID="id";
    private String PASS_ID="passid";
    private String ID="userid";
    private String MYIP="myip";

    public MyPref(Context context) {
        this.context = context;
        sharedPreferences=this.context.getSharedPreferences("mypref",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void setKEY_ID(String id) {

        editor.putString(KEY_ID,id);
        editor.commit();
    }



    public void setID(String userid) {
        editor.putString(ID,userid);
        editor.commit();
    }

    public void setPassID(String userid) {
        editor.putString(PASS_ID,userid);
        editor.commit();
    }

    public String getKEY_ID() {
        return sharedPreferences.getString(KEY_ID,null);
    }

    public String getPASS_ID() {
        return sharedPreferences.getString(PASS_ID,null);
    }

    public String getID() {
        return sharedPreferences.getString(ID,null);
    }

    public void ClearAll()
    {
        currip=getIP();
        editor.clear();
        setIP(currip);
        getIP();
        editor.commit();
    }

    public String getIP(){
        return sharedPreferences.getString(MYIP,null);
    }

    public void setIP(String ip){
        Toast.makeText(context, ""+ip, Toast.LENGTH_SHORT).show();
        editor.putString(MYIP,ip);
        editor.commit();
    }
}
