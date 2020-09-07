package apex.mams.student;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ADMIN on 17-Mar-18.
 */

public class CustomAdpforAttendance extends RecyclerView.Adapter<CustomAdpforAttendance.Viewholder> {

    HashMap<String, String> map;
    Context context;
    List<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();

    public CustomAdpforAttendance(Context context, List<HashMap<String, String>> ls) {
        this.context = context;
        this.ls = ls;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.design_attendance, null);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder viewholder, int position) {

        map = ls.get(position);

        viewholder.txt_Atsrno.setText(map.get("srno"));
        viewholder.txt_Atdate.setText(map.get("date"));
        viewholder.txt_Atstatus.setText(map.get("status"));
    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView txt_Atsrno, txt_Atdate, txt_Atstatus;

        public Viewholder(View convertView) {
            super(convertView);

            txt_Atsrno = convertView.findViewById(R.id.txt_Atsrno);
            txt_Atdate = convertView.findViewById(R.id.txt_Atdate);
            txt_Atstatus = convertView.findViewById(R.id.txt_Atstatus);

        }
    }
}