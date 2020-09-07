package apex.mams.admin;

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
 * Created by ADMIN on 20-Mar-18.
 */

public class CustomAdpforAttendanceReport extends RecyclerView.Adapter<CustomAdpforAttendanceReport.Viewholder> {

    HashMap<String, String> map;
    Context context;
    List<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();

    public CustomAdpforAttendanceReport(Context context, List<HashMap<String, String>> ls) {
        this.context = context;
        this.ls = ls;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.design_attendancereport, null);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder viewholder, int position) {

        for (int j = 0; j < viewholder.sub.length; j++) {
            viewholder.sub[j].setVisibility(View.GONE);
        }
        map = ls.get(position);

        viewholder.txt_Atsrno.setText(map.get("srno"));
        viewholder.txt_Atsrno.setWidth(Integer.parseInt(map.get("wsrno")));

        viewholder.txt_Atenno.setText(map.get("enno"));
        viewholder.txt_Atenno.setWidth(Integer.parseInt(map.get("wenno")));


        for (int j = 0; j < Integer.parseInt(map.get("sublength")); j++) {
            viewholder.sub[j].setText(map.get("sub" + String.valueOf(j + 1)));
            viewholder.sub[j].setVisibility(View.VISIBLE);
            viewholder.sub[j].measure(0,0);
            viewholder.sub[j].setWidth(Integer.parseInt(map.get("wsub"+String.valueOf(j+1))));
            viewholder.sub[j].measure(0,0);
        }

    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView txt_Atsrno, txt_Atenno, txt_sub1, txt_sub2, txt_sub3, txt_sub4, txt_sub5, txt_sub6, txt_sub7, txt_sub8;
        TextView[] sub;

        public Viewholder(View convertView) {
            super(convertView);

            txt_Atsrno = (TextView) convertView.findViewById(R.id.txt_Atsrno);
            txt_Atenno = (TextView) convertView.findViewById(R.id.txt_Atenno);

            txt_Atsrno.measure(0, 0);
            txt_Atenno.measure(0, 0);

            txt_sub1 = (TextView) convertView.findViewById(R.id.txt_sub1);
            txt_sub2 = (TextView) convertView.findViewById(R.id.txt_sub2);
            txt_sub3 = (TextView) convertView.findViewById(R.id.txt_sub3);
            txt_sub4 = (TextView) convertView.findViewById(R.id.txt_sub4);
            txt_sub5 = (TextView) convertView.findViewById(R.id.txt_sub5);
            txt_sub6 = (TextView) convertView.findViewById(R.id.txt_sub6);
            txt_sub7 = (TextView) convertView.findViewById(R.id.txt_sub7);
            txt_sub8 = (TextView) convertView.findViewById(R.id.txt_sub8);

            sub = new TextView[]{txt_sub1, txt_sub2, txt_sub3, txt_sub4, txt_sub5, txt_sub6, txt_sub7, txt_sub8};

            for (int i = 0; i < sub.length; i++) {
                sub[i].measure(0, 0);
            }

        }
    }
}
