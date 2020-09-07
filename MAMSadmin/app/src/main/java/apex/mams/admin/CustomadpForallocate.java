package apex.mams.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LENOVO on 8/28/2017.
 */

public class CustomadpForallocate extends BaseAdapter {

    Context context;
    List<HashMap<String,String>> ls;
    public CustomadpForallocate(Context context,List<HashMap<String,String>> ls)
    {
        this.context=context;
        this.ls=ls;
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
        viewHolder viewHolder=new viewHolder();

        if (convertView==null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.allocation_design, null);
            viewHolder.txt_viewallocation_faculty = (TextView) convertView.findViewById(R.id.txt_viewallocation_faculty);
            viewHolder.txt_viewallocation_subject = (TextView) convertView.findViewById(R.id.txt_viewallocation_subject);
            viewHolder.txt_viewallocation_sem = (TextView) convertView.findViewById(R.id.txt_viewallocation_sem);
            convertView.setTag(viewHolder);
        }else
        {
            viewHolder = (viewHolder) convertView.getTag();
        }

        viewHolder.txt_viewallocation_faculty.setText(ls.get(position).get("facultyid"));
        viewHolder.txt_viewallocation_subject.setText(ls.get(position).get("subjectid"));
        viewHolder.txt_viewallocation_sem.setText(ls.get(position).get("sem"));
        return convertView;
    }

    static class viewHolder
    {
        TextView txt_viewallocation_faculty,txt_viewallocation_subject,txt_viewallocation_sem;
    }
}
