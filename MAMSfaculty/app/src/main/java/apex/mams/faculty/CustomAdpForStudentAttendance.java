package apex.mams.faculty;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LENOVO on 3/12/2018.
 */

public class CustomAdpForStudentAttendance extends BaseAdapter {

    Context context;
    List<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();
    static List<HashMap<String,String>> lsAttendance=new ArrayList<>();

    //String delID;
    public CustomAdpForStudentAttendance(Context context, List<HashMap<String, String>> ls) {
        this.context = context;
        this.ls = ls;
    }

    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int i) {
        return ls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final Viewholder viewholder;
        final HashMap<String, String> map = ls.get(position);

        if (convertView == null) {
            viewholder = new Viewholder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.student_data_design, null);
            viewholder.txt_Enrollmentno = convertView.findViewById(R.id.txt_Enrollment);
            viewholder.txt_Name = convertView.findViewById(R.id.txt_Name);
            viewholder.student_Check=convertView.findViewById(R.id.student_Check);

            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }


        viewholder.txt_Enrollmentno.setText(map.get("studentEnroll"));
        viewholder.txt_Name.setText(map.get("studentName"));
        viewholder.student_Check.setChecked(true);

        HashMap<String,String> map2=new HashMap<String, String>();
        map2.put("studentid",map.get("studentid"));
        map2.put("studentName",map.get("studentName"));
        map2.put("studentEnroll",map.get("studentEnroll"));
        map2.put("status","P");
        lsAttendance.add(map2);


        viewholder.student_Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b!=true)
                {
                    lsAttendance.get(Integer.parseInt(map.get("id"))).put("status","A");
                    Log.e("##REMOVETAG",lsAttendance.get(Integer.parseInt(map.get("id"))).get("status"));
                    Log.e("##REMOVETAGID",lsAttendance.get(Integer.parseInt(map.get("id"))).get("studentid"));
                }
                else
                {
                    lsAttendance.get(Integer.parseInt(map.get("id"))).put("status","P");
                    Log.e("##REMOVETAG",lsAttendance.get(Integer.parseInt(map.get("id"))).get("status"));
                    Log.e("##REMOVETAGID",lsAttendance.get(Integer.parseInt(map.get("id"))).get("studentid"));
                }
            }
        });


        this.notifyDataSetChanged();
        return convertView;

    }

    static class Viewholder {
        TextView txt_Enrollmentno,txt_Name;
        CheckBox student_Check;
    }

    }
