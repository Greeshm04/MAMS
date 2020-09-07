package apex.mams.student;

import android.content.Context;
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
 * Created by LENOVO on 2/8/2018.
 */

public class CustomAdpForMaterial extends RecyclerView.Adapter<CustomAdpForMaterial.Viewholder> {
    Context context;
    DownloadFunction downloadFunction;
    String name;
    HashMap<String, String> map;
    List<HashMap<String,String>> ls=new ArrayList<HashMap<String, String>>();
    public CustomAdpForMaterial(Context context, List<HashMap<String, String>> ls) {
        this.context = context;
        Log.e("############","11111");
        this.downloadFunction=new DownloadFunction(context);
        this.ls = ls;
    }
    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        Log.e("################","1");
        View view=inflater.inflate(R.layout.data_notice_design,null);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder viewholder, int position) {
        map = ls.get(position);
        Log.e("################",map.get("material_file"));
        viewholder.txt_noticeTitle.setText(map.get("material_title"));
        viewholder.txt_noticeDate.setText(map.get("upload_date"));
        viewholder.txt_noticeby.setText("By " + map.get("materialby"));
        viewholder.txt_notice_description.setText(map.get("material_description"));
        viewholder.txt_filename.setText(map.get("material_file"));
        viewholder.txt_fileSize.setText(map.get("material_size"));

        setFileLogo(map.get("material_file"),viewholder);
        viewholder.side_bar.setBackgroundColor(Color.parseColor("#9575cd"));
        viewholder.side_bar_2.setBackgroundColor(Color.parseColor("#9575cd"));
        viewholder.card_sub.setCardBackgroundColor(Color.parseColor("#ffffff"));


    }


    @Override
    public int getItemCount() {
        Log.e("#############", String.valueOf(ls.size()));
        return ls.size();
    }
    class Viewholder extends RecyclerView.ViewHolder{
        TextView txt_noticeTitle, txt_noticeDate, txt_noticeby, txt_notice_description, txt_filename, txt_blank,txt_fileSize;
        LinearLayout ln, side_bar, ln_drop_button, side_bar_2;
        ImageView btn_downFile, btn_drop,img_filetype;
        CardView card_sub, card_main;
        int position;
        public Viewholder(View convertView) {
            super(convertView);
            Log.e("################","3");
            txt_noticeTitle = (TextView) convertView.findViewById(R.id.txt_noticeTitle);
            txt_noticeDate = (TextView) convertView.findViewById(R.id.txt_noticeDate);
            txt_notice_description = (TextView) convertView.findViewById(R.id.txt_notice_Description);
            txt_noticeby = (TextView) convertView.findViewById(R.id.txt_noticeby);
            txt_filename = (TextView) convertView.findViewById(R.id.txt_filename);
            txt_fileSize = (TextView) convertView.findViewById(R.id.txt_fileSize);
            txt_blank = (TextView) convertView.findViewById(R.id.txt_blank);
            side_bar = (LinearLayout) convertView.findViewById(R.id.side_bar);
            side_bar_2 = (LinearLayout) convertView.findViewById(R.id.side_bar_2);
            ln_drop_button = (LinearLayout) convertView.findViewById(R.id.ln_drop_button);
            card_sub = (CardView) convertView.findViewById(R.id.card_sub);
            card_main = (CardView) convertView.findViewById(R.id.card_main);
            btn_drop = (ImageView) convertView.findViewById(R.id.btn_drop);
            btn_downFile = (ImageView) convertView.findViewById(R.id.btn_downFile);
            img_filetype = (ImageView) convertView.findViewById(R.id.img_filetype);
            ln = (LinearLayout) convertView.findViewById(R.id.ln);

            ln.setVisibility(View.GONE);



            btn_drop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position=getPosition();


                    if (txt_filename.getText().equals("")) {
                        card_sub.setVisibility(View.GONE);
                    }

                    if (ln.getVisibility() == View.GONE) {
                        txt_blank.setVisibility(View.VISIBLE);
                        ln.setVisibility(View.VISIBLE);
                        side_bar.setVisibility(View.GONE);


                    } else {
                        txt_blank.setVisibility(View.GONE);
                        ln.setVisibility(View.GONE);
                        side_bar.setVisibility(View.VISIBLE);


                    }

                    if (btn_drop.getTag().equals("expand_arrow_down")) {
                        btn_drop.setTag("expand_arrow_up");
                        btn_drop.setImageResource(R.drawable.expand_arrow_up);

                        ln.setBackgroundColor(Color.parseColor("#9575cd"));
                        ln_drop_button.setBackgroundColor(Color.parseColor("#9575cd"));

                    } else {
                        btn_drop.setTag("expand_arrow_down");
                        btn_drop.setImageResource(R.drawable.expand_arrow_down);

                        ln_drop_button.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
            });

            btn_downFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = txt_filename.getText().toString();

                    downloadFunction.fetchUrl(name);
                }
            });


        }
    }


    public void setFileLogo(String filename,Viewholder viewholder) {



        if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("doc") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("docx")) {
            // Word document
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_doc);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("pdf")) {
            // PDF file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_pdf);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("ppt") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("pptx")) {
            // Powerpoint file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_ppt);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("xls") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("xlsx")) {
            // Excel file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_xls);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("zip") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("rar")) {
            // ZIP file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_rar);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("rtf")) {
            // RTF file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_rtf);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("wav")) {
            // WAV audio file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_wav);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mp3")) {
            //MP3 file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_mp3);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("gif")) {
            // GIF file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_gif);
        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("jpg") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("jpeg") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("png")) {
            // JPG file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_img);

        } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("txt")) {
            // Text file
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_txt);
        } else if (filename.substring(filename.toString().lastIndexOf(".") + 1).equalsIgnoreCase("3gp") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpg") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpeg") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpe") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mp4") ||
                filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("avi")) {
            // Video files
            viewholder.img_filetype.setImageResource(R.drawable.imgtype_vid);
        }
    }
}
