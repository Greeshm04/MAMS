package apex.mams.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LENOVO on 1/16/2018.
 */

public class CustomAdpForAcademic extends BaseAdapter {

    Context context;
    List<HashMap<String, String>> ls;

    public CustomAdpForAcademic(Context context, List<HashMap<String, String>> ls) {
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
            convertView = layoutInflater.inflate(R.layout.academic_data_design, null);

            viewholder.txt_academic = (TextView) convertView.findViewById(R.id.txt_academic);
            viewholder.txt_start = (TextView) convertView.findViewById(R.id.txt_start);
            viewholder.txt_end = (TextView) convertView.findViewById(R.id.txt_end);

            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }

        viewholder.txt_academic.setText(map.get("academic_text"));
        viewholder.txt_start.setText(map.get("start_date"));
        viewholder.txt_end.setText(map.get("end_date"));


         this.notifyDataSetChanged();
        return convertView;
    }
    static class Viewholder {
        TextView txt_academic, txt_start, txt_end;

    }
}
