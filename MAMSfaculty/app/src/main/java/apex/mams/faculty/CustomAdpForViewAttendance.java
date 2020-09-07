package apex.mams.faculty;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LENOVO on 3/18/2018.
 */

public class CustomAdpForViewAttendance extends RecyclerView.Adapter<CustomAdpForViewAttendance.Viewholder> {

    Context context;
    String name;
    HashMap<String, String> map;
    List<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();
    MyPref myPref;

    public CustomAdpForViewAttendance(Context context, List<HashMap<String, String>> ls) {
        this.context = context;
        this.ls = ls;
        myPref=new MyPref(context);
    }
    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.data_attendance_view_design, null);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder viewholder, int position) {

        map = ls.get(position);

        viewholder.txt_srNo.setText(map.get("srNo"));
        viewholder.txt_StudentName.setText(map.get("studentName"));
        viewholder.txt_StudentEnroll.setText(map.get("studentEnroll"));
        viewholder.txt_AttendanceStatus.setText(map.get("attendanceStatus"));

    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView txt_srNo,txt_StudentName,txt_StudentEnroll,txt_AttendanceStatus;
        int position;

        public Viewholder(final View convertView) {
            super(convertView);
            txt_srNo = (TextView) convertView.findViewById(R.id.txt_srNo);
            txt_StudentName = (TextView) convertView.findViewById(R.id.txt_StudentName);
            txt_StudentEnroll = (TextView) convertView.findViewById(R.id.txt_StudentEnroll);
            txt_AttendanceStatus = (TextView) convertView.findViewById(R.id.txt_AttendanceStatus);





        }


    }

}
