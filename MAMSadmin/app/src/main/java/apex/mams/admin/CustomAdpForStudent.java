package apex.mams.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LENOVO on 9/11/2017.
 */

public class CustomAdpForStudent extends BaseAdapter {
    Context context;
    List<HashMap<String, String>> ls;
    Getip getip;
    boolean check;
    static List<HashMap<String,String>> lsCheck=new ArrayList<HashMap<String, String>>();

    public CustomAdpForStudent(Context context, List<HashMap<String, String>> ls,boolean check) {
        this.context = context;
        this.ls = ls;
        this.check = check;
        getip=new Getip(context);

    }

    @Override

    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        return ls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final Viewholder viewholder;
        final HashMap<String, String> map = ls.get(position);

        if (convertView == null) {
            viewholder = new Viewholder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.student_data_design, null);
            viewholder.ln = (LinearLayout) convertView.findViewById(R.id.ln);
            viewholder.txt_studentEnRoll = (TextView) convertView.findViewById(R.id.txt_studentEnRoll);
            viewholder.txt_studentName = (TextView) convertView.findViewById(R.id.txt_studentName);
            viewholder.txt_studentMobNo = (TextView) convertView.findViewById(R.id.txt_studentMobNo);
            viewholder.txt_studentSem = (TextView) convertView.findViewById(R.id.txt_studentSem);
            viewholder.txt_studentEmailId = (TextView) convertView.findViewById(R.id.txt_studentEmailId);
            viewholder.txt_studentDob = (TextView) convertView.findViewById(R.id.txt_studentDob);
            viewholder.txt_studentDoj = (TextView) convertView.findViewById(R.id.txt_studentDoj);
            viewholder.txt_studentDol = (TextView) convertView.findViewById(R.id.txt_studentDol);
            viewholder.txt_studentAddress = (TextView) convertView.findViewById(R.id.txt_studentAddress);
            viewholder.btn_drop = (ImageView) convertView.findViewById(R.id.btn_drop);
            viewholder.img_profile = (ImageView) convertView.findViewById(R.id.img_profile);
            viewholder.ln.setVisibility(View.GONE);
            viewholder.cbforstud= (CheckBox) convertView.findViewById(R.id.cbforstud);

            convertView.setTag(viewholder);

        } else {
            viewholder = (Viewholder) convertView.getTag();
        }

        viewholder.txt_studentEnRoll.setText("En. No. : "+map.get("enrollNo"));
        viewholder.txt_studentName.setText(map.get("name"));
        viewholder.txt_studentMobNo.setText(map.get("mobNo"));
        viewholder.txt_studentSem.setText("Sem : "+map.get("sem"));
        viewholder.txt_studentEmailId.setText(map.get("emailId"));
        viewholder.txt_studentDob.setText(map.get("Dob"));
        viewholder.txt_studentDoj.setText(map.get("Doj"));
        viewholder.txt_studentDol.setText(map.get("Dol"));
        viewholder.txt_studentAddress.setText(map.get("Address"));
        viewholder.cbforstud.setVisibility(View.GONE);

        if(check==true)
        {
            viewholder.cbforstud.setVisibility(View.VISIBLE);
            viewholder.cbforstud.setChecked(check);

            HashMap<String,String> map3=new HashMap<String, String>();
            map3.put("studentid",map.get("studentid"));
            map3.put("stat","P");
            lsCheck.add(map3);
        }

        if (!map.get("dpurl").equals(""))
        {

            Glide.with(context).load(map.get("dpurl")).asBitmap().centerCrop().into(new BitmapImageViewTarget(viewholder.img_profile) {

                @Override
                protected void setResource(Bitmap resource) {
                    super.setResource(resource);
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    viewholder.img_profile.setImageDrawable(circularBitmapDrawable);
                }
            });


        }

        viewholder.cbforstud.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b==true)
                {
                    lsCheck.get(Integer.valueOf(map.get("location"))).put("stat","P");
                }
                else
                {
                    lsCheck.get(Integer.valueOf(map.get("location"))).put("stat","A");
                }
            }
        });

        viewholder.btn_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (viewholder.ln.getVisibility() == View.GONE) {
                    viewholder.ln.setVisibility(View.VISIBLE);

                } else {
                    viewholder.ln.setVisibility(View.GONE);

                }
                if(viewholder.btn_drop.getTag().equals("down"))
                {
                    viewholder.btn_drop.setTag("up");
                    viewholder.btn_drop.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else {
                    viewholder.btn_drop.setTag("down");
                    viewholder.btn_drop.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });
        this.notifyDataSetChanged();
        return convertView;
    }

    static class Viewholder {
        TextView txt_studentEnRoll, txt_studentName, txt_studentMobNo, txt_studentSem, txt_studentEmailId, txt_studentDob, txt_studentDoj, txt_studentDol, txt_studentAddress;
        public LinearLayout ln;
        ImageView btn_drop,img_profile;
        CheckBox cbforstud;

    }
}


