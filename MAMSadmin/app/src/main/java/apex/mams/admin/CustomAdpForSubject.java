package apex.mams.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LENOVO on 9/14/2017.
 */

public class CustomAdpForSubject extends BaseAdapter {



    Context context;
    List<HashMap<String,String>> ls;
    Getip getip;
    String delSId;



    public CustomAdpForSubject(Context context, List<HashMap<String, String>> ls) {
        this.context = context;
        this.ls = ls;
        getip=new Getip(context);

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
        Viewholder viewholder = new Viewholder();
        final HashMap<String, String> map = ls.get(position);

        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.subject_data_design, null);
            viewholder.txt_subjectCode= (TextView) convertView.findViewById(R.id.txt_subjectCode);
            viewholder.txt_subjectName= (TextView) convertView.findViewById(R.id.txt_subjectName);
            viewholder.txt_subjectFaculty= (TextView) convertView.findViewById(R.id.txt_subjectFaculty);
            viewholder.subject_edit= (ImageView) convertView.findViewById(R.id.subject_edit);
            viewholder.subject_delete= (ImageView) convertView.findViewById(R.id.subject_delete);
            viewholder.subject_allocate= (ImageView) convertView.findViewById(R.id.subject_allocate);

            convertView.setTag(viewholder);
        }
        else
        {
            viewholder = (Viewholder) convertView.getTag();
        }

        viewholder.txt_subjectCode.setText(map.get("subjectCode"));
        viewholder.txt_subjectName.setText(map.get("subjectName"));
        viewholder.txt_subjectFaculty.setText(map.get("subjectFaculty"));

        viewholder.subject_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,addsubjectActivity.class);
                intent.putExtra("subjectsid",map.get("subjectId"));
                intent.putExtra("status","update");
                context.startActivity(intent);

            }
        });

        viewholder.subject_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.ic_warning_black_24dp);
                builder.setMessage("Are You Sure To Delete?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delSId=map.get("subjectId");
                        delete_subject();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });


        viewholder.subject_allocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,addallocationActivity.class);
                intent.putExtra("subjectName",map.get("subjectName"));
                intent.putExtra("subjectFaculty",map.get("subjectFaculty"));
                intent.putExtra("subjectId",map.get("subjectId"));
                intent.putExtra("facultyId",map.get("facultyId"));
                context.startActivity(intent);
            }
        });


        return convertView;
    }
    static class Viewholder {
        TextView txt_subjectCode,txt_subjectName, txt_subjectFaculty;
        ImageView subject_edit,subject_allocate,subject_delete;
    }

    public void delete_subject()
    {

        final ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Deleting DATA");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, getip.getUrl() + "subject_master/delete_subject_master.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject=new JSONObject(response);
                    int i=jsonObject.getInt("status");
                    if (i==0)
                    {
                        Toast.makeText(context, "Not Deleted", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else if (i==1)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent(context,SubjectFragment.class);
                        context.startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Error Occured"+error, Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<String, String>();
                map.put("subjectid",delSId);

                return map;
            }
        };

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

}
