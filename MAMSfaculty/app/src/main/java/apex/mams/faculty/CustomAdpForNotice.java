package apex.mams.faculty;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LENOVO on 11/17/2017.
 */
public class CustomAdpForNotice extends RecyclerView.Adapter<CustomAdpForNotice.Viewholder> {
    Context context;
    DownloadFunction downloadFunction;
    String name;
    HashMap<String, String> map;
    List<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();
    int ch;

    public CustomAdpForNotice(Context context, List<HashMap<String, String>> ls,int ch) {
        this.context = context;
        Log.e("############", "11111");
        this.downloadFunction = new DownloadFunction(context);
        this.ls = ls;
        this.ch=ch;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.e("################", "1");
        View view = inflater.inflate(R.layout.data_notice_design, null);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder viewholder, int position) {
        map = ls.get(position);
        Log.e("################", map.get("noticefile"));
        viewholder.txt_noticeTitle.setText(map.get("notice_title"));
        viewholder.txt_noticeDate.setText(map.get("notice_date"));
        viewholder.txt_noticeby.setText("By " + map.get("noticeby"));
        viewholder.txt_notice_description.setText(map.get("notice_discription"));
        viewholder.txt_filename.setText(map.get("noticefile"));
        viewholder.txt_fileSize.setText(map.get("notice_size"));
        viewholder.txt_noticeSem.setVisibility(View.GONE);



        viewholder.side_bar.setBackgroundColor(Color.parseColor(map.get("notice_color")));
        viewholder.side_bar_2.setBackgroundColor(Color.parseColor(map.get("notice_color")));
        //viewholder.card_sub.setCardBackgroundColor(Color.parseColor(map.get("notice_color")));

        viewholder.setFileLogo(map.get("noticefile"));
    }

    @Override
    public int getItemCount() {
        Log.e("#############", String.valueOf(ls.size()));
        return ls.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView txt_noticeTitle, txt_noticeDate, txt_noticeby, txt_notice_description, txt_filename, txt_blank,txt_noticeSem,txt_fileSize;
        LinearLayout ln, side_bar, ln_drop_button, side_bar_2;
        ImageView btn_downFile, btn_drop, img_filetype;
        CardView card_sub, card_main;
        int position;

        public Viewholder(View convertView) {
            super(convertView);
            Log.e("################", "3");
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
            img_filetype = (ImageView) convertView.findViewById(R.id.img_filetype);
            btn_downFile = (ImageView) convertView.findViewById(R.id.btn_downFile);
            ln = (LinearLayout) convertView.findViewById(R.id.ln);
            txt_noticeSem=convertView.findViewById(R.id.txt_noticeSem);

            ln.setVisibility(View.GONE);


            btn_drop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getPosition();


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
                        btn_drop.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);

                        ln.setBackgroundColor(Color.parseColor(ls.get(position).get("notice_color")));
                        ln_drop_button.setBackgroundColor(Color.parseColor(ls.get(position).get("notice_color")));

                    } else {
                        btn_drop.setTag("expand_arrow_down");
                        btn_drop.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);

                        ln_drop_button.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        side_bar.setBackgroundColor(Color.parseColor(ls.get(position).get("notice_color")));
                        side_bar_2.setBackgroundColor(Color.parseColor(ls.get(position).get("notice_color")));
                    }
                }
            });

            btn_downFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = txt_filename.getText().toString();
                    Toast.makeText(context, "Fetching url", Toast.LENGTH_SHORT).show();
                    downloadFunction.fetchUrl(name);
//                fetchUrl();
                }
            });


//        notifyDataSetChanged();

        }

        public void setFileLogo(String filename) {


            if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("doc") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("docx")) {
                // Word document
                img_filetype.setImageResource(R.drawable.imgtype_doc);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("pdf")) {
                // PDF file
                img_filetype.setImageResource(R.drawable.imgtype_pdf);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("ppt") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("pptx")) {
                // Powerpoint file
                img_filetype.setImageResource(R.drawable.imgtype_ppt);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("xls") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("xlsx")) {
                // Excel file
                img_filetype.setImageResource(R.drawable.imgtype_xls);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("zip") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("rar")) {
                // ZIP file
                img_filetype.setImageResource(R.drawable.imgtype_rar);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("rtf")) {
                // RTF file
                img_filetype.setImageResource(R.drawable.imgtype_rtf);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("wav")) {
                // WAV audio file
                img_filetype.setImageResource(R.drawable.imgtype_wav);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mp3")) {
                //MP3 file
                img_filetype.setImageResource(R.drawable.imgtype_mp3);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("gif")) {
                // GIF file
                img_filetype.setImageResource(R.drawable.imgtype_gif);
            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("jpg") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("jpeg") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("png")) {
                // JPG file
                img_filetype.setImageResource(R.drawable.imgtype_img);

            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("txt")) {
                // Text file
                img_filetype.setImageResource(R.drawable.imgtype_txt);
            } else if (filename.substring(filename.toString().lastIndexOf(".") + 1).equalsIgnoreCase("3gp") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpg") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpeg") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpe") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mp4") ||
                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("avi")) {
                // Video files
                img_filetype.setImageResource(R.drawable.imgtype_vid);
            }
        }
    }
}



//
//import android.app.DownloadManager;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Environment;
//import android.support.v7.widget.CardView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.io.BufferedInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//
///**
// * Created by ADMIN on 17-Nov-17.
// */
//
//public class CustomAdpForNotice extends BaseAdapter {
//
//    String typeofNotice = "";
//    String colorofNotice = "";
//    DownloadFunction downloadFunction;
//    String name;
//    Context context;
//    List<HashMap<String, String>> ls = new ArrayList<HashMap<String, String>>();
//    Getip getip = new Getip();
//
//    //String delID;
//    public CustomAdpForNotice(Context context, List<HashMap<String, String>> ls) {
//        this.context = context;
//        downloadFunction = new DownloadFunction(context);
//        this.ls = ls;
//    }
//
//    @Override
//    public int getCount() {
//        return ls.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return ls.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup viewGroup) {
//
//        final Viewholder viewholder;
//        final HashMap<String, String> map = ls.get(position);
//
//        if (convertView == null) {
//            viewholder = new Viewholder();
//            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.data_notice_design, null);
//            viewholder.txt_noticeTitle = (TextView) convertView.findViewById(R.id.txt_noticeTitle);
//            viewholder.txt_noticeDate = (TextView) convertView.findViewById(R.id.txt_noticeDate);
//            viewholder.txt_notice_description = (TextView) convertView.findViewById(R.id.txt_notice_Description);
//            viewholder.txt_noticeby = (TextView) convertView.findViewById(R.id.txt_noticeby);
//            viewholder.txt_filename = convertView.findViewById(R.id.txt_filename);
//            viewholder.txt_blank = convertView.findViewById(R.id.txt_blank);
//            viewholder.side_bar = convertView.findViewById(R.id.side_bar);
//            viewholder.side_bar_2 = convertView.findViewById(R.id.side_bar_2);
//            viewholder.ln_drop_button = convertView.findViewById(R.id.ln_drop_button);
//            viewholder.card_sub = convertView.findViewById(R.id.card_sub);
//            viewholder.img_filetype = convertView.findViewById(R.id.img_filetype);
//            viewholder.card_main = convertView.findViewById(R.id.card_main);
//            viewholder.btn_drop = (ImageView) convertView.findViewById(R.id.btn_drop);
//            viewholder.btn_downFile = convertView.findViewById(R.id.btn_downFile);
//
//            viewholder.ln = (LinearLayout) convertView.findViewById(R.id.ln);
//
//            viewholder.ln.setVisibility(View.GONE);
//
////            viewholder.notice_edit.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    Intent intent = new Intent(context, addnoticeActivity.class);
////                    intent.putExtra("ch","Update");
////                    intent.putExtra("id",map.get("noticeid"));
////                    context.startActivity(intent);
////                }
////            });
//
//
//            convertView.setTag(viewholder);
//        } else {
//            viewholder = (Viewholder) convertView.getTag();
//        }
//
//
//        viewholder.txt_noticeTitle.setText(map.get("notice_title"));
//        viewholder.txt_noticeDate.setText(map.get("notice_date"));
//        viewholder.txt_noticeby.setText("By " + map.get("noticeby"));
//        viewholder.txt_notice_description.setText(map.get("notice_discription"));
//        viewholder.txt_filename.setText(map.get("noticefile"));
//
//        viewholder.side_bar.setBackgroundColor(Color.parseColor(map.get("notice_color")));
//        viewholder.side_bar_2.setBackgroundColor(Color.parseColor(map.get("notice_color")));
//        viewholder.card_sub.setCardBackgroundColor(Color.parseColor(map.get("notice_color")));
//
//        viewholder.btn_drop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (map.get("noticefile").equals("")) {
//                    viewholder.card_sub.setVisibility(View.GONE);
//                }else {
//                    viewholder.setFileLogo(map.get("noticefile"));
//                }
//
//                if (viewholder.ln.getVisibility() == View.GONE) {
//                    viewholder.txt_blank.setVisibility(View.VISIBLE);
//                    viewholder.ln.setVisibility(View.VISIBLE);
//                    viewholder.side_bar.setVisibility(View.GONE);
//
//                } else {
//                    viewholder.txt_blank.setVisibility(View.GONE);
//                    viewholder.ln.setVisibility(View.GONE);
//                    viewholder.side_bar.setVisibility(View.VISIBLE);
//                }
//
//                if (viewholder.btn_drop.getTag().equals("expand_arrow_down")) {
//                    viewholder.btn_drop.setTag("expand_arrow_up");
//                    viewholder.btn_drop.setImageResource(R.drawable.expand_arrow_up);
//
//                    viewholder.ln.setBackgroundColor(Color.parseColor(map.get("notice_color")));
//                    viewholder.ln_drop_button.setBackgroundColor(Color.parseColor(map.get("notice_color")));
//
//                } else {
//                    viewholder.btn_drop.setTag("expand_arrow_down");
//                    viewholder.btn_drop.setImageResource(R.drawable.expand_arrow_down);
//
//                    viewholder.ln_drop_button.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                    viewholder.side_bar.setBackgroundColor(Color.parseColor(map.get("notice_color")));
//                    viewholder.side_bar_2.setBackgroundColor(Color.parseColor(map.get("notice_color")));
//                }
//            }
//        });
//
//        viewholder.btn_downFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                name = viewholder.txt_filename.getText().toString();
//                Toast.makeText(context, "Fetching url", Toast.LENGTH_SHORT).show();
//                downloadFunction.fetchUrl(name);
////                fetchUrl();
//            }
//        });
//
//        this.notifyDataSetChanged();
//        return convertView;
//
//    }
//
//    static class Viewholder {
//        TextView txt_noticeTitle, txt_noticeDate, txt_noticeby, txt_notice_description, txt_filename, txt_blank;
//        LinearLayout ln, side_bar, ln_drop_button, side_bar_2;
//        ImageView btn_downFile, btn_drop, img_filetype;
//        CardView card_sub, card_main;
//
//        // ImageView notice_delete;
//
//        public void setFileLogo(String filename) {
//
//            if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("doc") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("docx")) {
//                // Word document
//                img_filetype.setImageResource(R.drawable.imgtype_doc);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("pdf")) {
//                // PDF file
//                img_filetype.setImageResource(R.drawable.imgtype_pdf);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("ppt") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("pptx")) {
//                // Powerpoint file
//                img_filetype.setImageResource(R.drawable.imgtype_ppt);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("xls") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("xlsx")) {
//                // Excel file
//                img_filetype.setImageResource(R.drawable.imgtype_xls);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("zip") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("rar")) {
//                // ZIP file
//                img_filetype.setImageResource(R.drawable.imgtype_rar);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("rtf")) {
//                // RTF file
//                img_filetype.setImageResource(R.drawable.imgtype_rtf);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("wav")) {
//                // WAV audio file
//                img_filetype.setImageResource(R.drawable.imgtype_wav);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mp3")) {
//                //MP3 file
//                img_filetype.setImageResource(R.drawable.imgtype_mp3);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("gif")) {
//                // GIF file
//                img_filetype.setImageResource(R.drawable.imgtype_gif);
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("jpg") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("jpeg") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("png")) {
//                // JPG file
//                img_filetype.setImageResource(R.drawable.imgtype_img);
//
//            } else if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("txt")) {
//                // Text file
//                img_filetype.setImageResource(R.drawable.imgtype_txt);
//            } else if (filename.substring(filename.toString().lastIndexOf(".") + 1).equalsIgnoreCase("3gp") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpg") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpeg") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mpe") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("mp4") ||
//                    filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("avi")) {
//                // Video files
//                img_filetype.setImageResource(R.drawable.imgtype_vid);
//            }
//        }
//    }
//
//
//}