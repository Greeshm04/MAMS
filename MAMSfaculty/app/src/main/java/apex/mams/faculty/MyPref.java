package apex.mams.faculty;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ADMIN on 17-Nov-17.
 */

public class MyPref {
    String currip="";
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String KEY_ID="id";
    private String ID="userid";
    private String MOB_ID="mobid";
    private String EMAIL_ID="emailid";
    private String PASS_ID="passid";
    private String NAME_ID="nameid";
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

    public void setMobile(String id) {

        editor.putString(MOB_ID,id);
        editor.commit();
    }
    public void setEmail(String id) {

        editor.putString(EMAIL_ID,id);
        editor.commit();
    }

    public void setName(String id) {

        editor.putString(NAME_ID,id);
        editor.commit();
    }

    public void setPassID(String userid) {
        editor.putString(PASS_ID,userid);
        editor.commit();
    }

    public String getKEY_ID() {
        return sharedPreferences.getString(KEY_ID,null);
    }

    public String getMobile() {
        return sharedPreferences.getString(MOB_ID,null);
    }

    public String getPASS_ID() {
        return sharedPreferences.getString(PASS_ID,null);
    }


    public String getEmail() {
        return sharedPreferences.getString(EMAIL_ID,null);
    }

    public String getName() {
        return sharedPreferences.getString(NAME_ID,null);
    }


    public String getID() {
        return sharedPreferences.getString(ID,null);
    }


    

    public boolean saveArray(String[] array, String arrayName) {
        editor.putInt(arrayName +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    public String[] loadArray(String arrayName) {
        int size = sharedPreferences.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = sharedPreferences.getString(arrayName + "_" + i, null);
        return array;
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
        editor.putString(MYIP,ip);
        editor.commit();
    }




}
