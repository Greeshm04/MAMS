package apex.mams.faculty;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LENOVO on 3/17/2018.
 */

public class CustomAdpForAttendance extends RecyclerView.Adapter<CustomAdpForAttendance.Viewholder> {
    Context context;
    String name;
    HashMap<String, String> map;
    List<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();
    MyPref myPref;

    public CustomAdpForAttendance(Context context, List<HashMap<String, String>> ls) {
        this.context = context;
        this.ls = ls;
        myPref=new MyPref(context);
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.data_attendance_design_faculty, null);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder viewholder, int position) {
        map = ls.get(position);
        viewholder.txt_Attendance_date.setText(map.get("DATE")+"/"+map.get("MONTH")+"/"+map.get("YEAR"));
        viewholder.txt_Attendance_time.setText(map.get("TIME"));



    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
      TextView txt_Attendance_date,txt_Attendance_time;
        int position;

        public Viewholder(final View convertView) {
            super(convertView);
            txt_Attendance_date = (TextView) convertView.findViewById(R.id.txt_Attendance_date);
            txt_Attendance_time = (TextView) convertView.findViewById(R.id.txt_Attendance_time);



            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position=getPosition();

//                    Toast.makeText(context, "SUBJECTID  "+map.get("SUBJECTID"), Toast.LENGTH_LONG).show();
//                    Toast.makeText(context, "MONTH  "+map.get("MONTH"), Toast.LENGTH_LONG).show();
//                    Toast.makeText(context, "YEAR  "+map.get("YEAR"), Toast.LENGTH_LONG).show();
//                    Toast.makeText(context, "DATE  "+map.get("DATE"), Toast.LENGTH_LONG).show();
//                    Toast.makeText(context, "FACULTYID  "+myPref.getID(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(context, "SUBJECTNAME  "+map.get("SUBJECTNAME"), Toast.LENGTH_LONG).show();


                    Intent intent=new Intent(context,viewAttendanceActivity.class);
                    intent.putExtra("SUBJECTID",map.get("SUBJECTID"));
                    intent.putExtra("MONTH",map.get("MONTH"));
                    intent.putExtra("YEAR",map.get("YEAR"));
                    intent.putExtra("DATE",map.get("DATE"));
                    context.startActivity(intent);


                }
            });

        }


    }

}
