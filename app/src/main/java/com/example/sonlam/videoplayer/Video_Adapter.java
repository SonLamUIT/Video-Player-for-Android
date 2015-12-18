package com.example.sonlam.videoplayer;

import android.widget.Filterable;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.sonlam.videoplayer.R;


/**
 * Created by Son Lam on 12/18/2015.
 */
public class Video_Adapter extends ArrayAdapter<Video_Info> implements Filterable {
    private Context mContext;
    private List<Video_Info> listData, myArrAllFilter;
    private ImageView imgVideo;
    private TextView tvTitle;
    private contactViewFilter filter;
    public Video_Adapter(Context context, List<Video_Info> objects){
        super(context, R.layout.video_item, objects);
        myArrAllFilter = new ArrayList<>();
        myArrAllFilter.addAll(objects);
        listData = objects;
        mContext = context;

    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new contactViewFilter();
        }
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.video_item, null);

        }
        tvTitle =(TextView)convertView.findViewById(R.id.videoTitle);
        tvTitle.setText(listData.get(position).getTitle());
        ViewHolder vholder = new ViewHolder();
        vholder.image = (ImageView)convertView.findViewById(R.id.videoThumbnal);
        vholder.filepath = listData.get(position).getUrl();


        //if ()
        new ThumbnailTask(vholder).execute();
        return convertView;
    }
    private class ThumbnailTask extends AsyncTask {
        private ViewHolder viewHolder = null;
        private Bitmap bitmap = null;
        ThumbnailTask(ViewHolder vHolder) {
            viewHolder = vHolder;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            bitmap = ThumbnailUtils.createVideoThumbnail(viewHolder.filepath, MediaStore.Video.Thumbnails.MINI_KIND);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 75, 50);

            //System.out.println("do BackGround ...");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            viewHolder.image.setImageBitmap(bitmap);
        }
    }

    private class ViewHolder {
        ImageView image;

        String filepath;

    }
    //Dung trong tim kiem
    private class contactViewFilter extends Filter {



        @Override

        protected FilterResults performFiltering(CharSequence constraint) {

            // TODO Auto-generated method stub

            constraint = constraint.toString().toLowerCase();

            FilterResults result = new FilterResults();

            if(constraint != null && constraint.toString().length() > 0)

            {

                List<Video_Info> myArrFilter = new ArrayList<>();



                for(int i = 0; i < myArrAllFilter.size(); i++)

                {





                    if(myArrAllFilter.get(i).getTitle().toLowerCase(Locale.getDefault()).contains(constraint))

                    {

                        // if(myArrAllFilter.get(i).getVoca().toLowerCase().indexOf((String.valueOf(constraint)).toLowerCase().trim())==0)

                        myArrFilter.add(myArrAllFilter.get(i));





                    }

                }

                result.count = myArrFilter.size();

                result.values = myArrFilter;

            }

            else

            {

                synchronized(this)

                {

                    result.values = myArrAllFilter;

                    result.count = myArrAllFilter.size();

                }

            }

            return result;

        }

        @SuppressWarnings("unchecked")

        @Override

        protected void publishResults(CharSequence constraint,

                                      FilterResults results) {

            // TODO Auto-generated method stub



            listData = (ArrayList<Video_Info>)results.values;

            notifyDataSetChanged();

            clear();

            for(int i = 0, l = listData.size(); i < l; i++)

                add(listData.get(i));

            notifyDataSetInvalidated();

        }



    }

}
