package apex.mams.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LENOVO on 7/9/2017.
 */
public class CustomAdp extends BaseAdapter {

    Context context;
    List<HashMap<String, String>> ls;
    Getip getip;

    public CustomAdp(Context context, List<HashMap<String, String>> ls) {
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final Viewholder viewholder;
        final HashMap<String, String> map = ls.get(position);

        if (convertView == null) {
            viewholder = new Viewholder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.faculty_data_design, null);
            viewholder.ln = (LinearLayout) convertView.findViewById(R.id.ln);

            viewholder.txt_facultyName = (TextView) convertView.findViewById(R.id.txt_facultyName);
            viewholder.txt_facultymobNo = (TextView) convertView.findViewById(R.id.txt_facultymobNo);
            viewholder.txt_facultyemailId = (TextView) convertView.findViewById(R.id.txt_facultyemailId);
            viewholder.txt_facultyDob = (TextView) convertView.findViewById(R.id.txt_facultyDob);
            viewholder.txt_facultyDoj = (TextView) convertView.findViewById(R.id.txt_facultyDoj);
            viewholder.txt_facultyAddress = (TextView) convertView.findViewById(R.id.txt_facultyAddress);
            viewholder.btn_drop = (ImageView) convertView.findViewById(R.id.btn_drop);
            viewholder.img_profile = (ImageView) convertView.findViewById(R.id.img_profile);

            viewholder.ln.setVisibility(View.GONE);

            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }

        viewholder.txt_facultyName.setText(map.get("firstName") + " " + map.get("lastName"));
        viewholder.txt_facultymobNo.setText(map.get("mobNo"));
        viewholder.txt_facultyemailId.setText(map.get("emailId"));
        viewholder.txt_facultyDob.setText(map.get("Dob"));
        viewholder.txt_facultyDoj.setText(map.get("Doj"));
        viewholder.txt_facultyAddress.setText(map.get("Address"));


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
        TextView txt_facultyName, txt_facultymobNo, txt_facultyemailId, txt_facultyDob, txt_facultyDoj, txt_facultyAddress, txt_facultyCity;
        public LinearLayout ln;
        ImageView btn_drop,img_profile;
    }




}
