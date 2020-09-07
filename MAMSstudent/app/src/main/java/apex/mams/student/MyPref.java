package apex.mams.student;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by LENOVO on 11/17/2017.
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
    private String NAME_ID="nameid";
    private String PASS_ID="passid";
    private String MYIP="myip";


    public MyPref(Context context) {
        this.context = context;
        sharedPreferences=this.context.getSharedPreferences("mypref",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void setCurrent_Sem(String sem) {

        editor.putString("Current_sem",sem);
        editor.commit();
    }

    public String getCurrent_sem() {
        return sharedPreferences.getString("Current_sem",null);
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

    public String getID() {
        return sharedPreferences.getString(ID,null);
    }

    public String getPASS_ID() {
        return sharedPreferences.getString(PASS_ID,null);
    }

    public String getMobile() {
        return sharedPreferences.getString(MOB_ID,null);
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL_ID,null);
    }

    public String getName() {
        return sharedPreferences.getString(NAME_ID,null);
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
}
