package com.ihsuraa.picmesh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.picker.gallery.model.GalleryVideo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class VideoChooserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GalleryVideo> mData,fData;
    private LayoutInflater mInflater;
    private Context context;
    //private CustomChooserAdapter.ItemClickListener mClickListener;
    private String date = "new date";
    public final int TYPE_DATE = 0;
    public final int TYPE_CONTENT = 1;
    public final int TYPE_PROGRESS = 2;
    private long day = 259200000;
    long currentTime = System.currentTimeMillis();
    long imageTime;
    ArrayList<Bitmap> cacheImage;
    int pos;
    Bitmap bitmap;
    String dateString = null , dat;
    Calendar calendar;
    long diffInDays;
    // data is passed into the constructor
    VideoChooserAdapter(Context context, List<GalleryVideo> data ,ArrayList<Bitmap> images) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        //this.fData = fdata;
        this.context = context;
        this.cacheImage = images;
     // getImages(data);



    }

    private void getImages(List<GalleryVideo> fdata) {
       for (GalleryVideo video : fdata){
           cacheImage.add(ThumbnailUtils.createVideoThumbnail(Uri.fromFile(new File(video.getDATA())).getPath(), MediaStore.Video.Thumbnails.MINI_KIND));
       }

    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView duration;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.dateItem);
            duration = itemView.findViewById(R.id.time2);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intent=new Intent();
            intent.putExtra("uri",mData.get(getAdapterPosition()).getDATA());
            ((Activity) context).setResult(21, intent);
            ((Activity) context).finish();
            //Toast.makeText(context,mData.get(getAdapterPosition()).getDATA(),Toast.LENGTH_LONG).show();
        }
    }

    public class ViewHolderDate extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date,duration;
        ImageView img;
        ViewHolderDate(View itemView){
            super(itemView);
            date = itemView.findViewById(R.id.date);
            img = itemView.findViewById(R.id.dateItem);
            duration = itemView.findViewById(R.id.time);
            img.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.putExtra("uri",mData.get(getAdapterPosition()).getDATA());
            ((Activity) context).setResult(21, intent);
            ((Activity) context).finish();
            //Toast.makeText(context,mData.get(getAdapterPosition()).getDATA(),Toast.LENGTH_LONG).show();
        }
    }

    public class ViewHolderProgress extends RecyclerView.ViewHolder  {

        ProgressBar progressBar;
        ViewHolderProgress(View itemView){
            super(itemView);
            progressBar = itemView.findViewById(R.id.chooserProgress);

        }


    }



    @Override
    public int getItemViewType(int position) {

        pos = position;

        if (mData.get(position).getALBUM_NAME().equals("showprogress")) {
            return TYPE_PROGRESS;
        }
        else {
            if (position == 0) {
                currentTime = Long.parseLong(mData.get(position).getDATE_ADDED());
                return TYPE_DATE;
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(Long.parseLong(mData.get(position).getDATE_ADDED()) * 1000));
                String dateString2 = formatter.format(new Date(Long.parseLong(mData.get(position - 1).getDATE_ADDED()) * 1000));
                if (dateString.equals(dateString2)) {
                    return TYPE_CONTENT;
                } else {
                    return TYPE_DATE;
                }
            }
        }





    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;


        switch (viewType) {

            case TYPE_CONTENT:
                View view = mInflater.inflate(R.layout.custom_chooser_item, parent, false);

                viewHolder = new ViewHolder(view);
                break;

            case TYPE_DATE:
                View view2 = mInflater.inflate(R.layout.custom_chooser_date, parent, false);

                viewHolder = new ViewHolderDate(view2);
                break;
            case TYPE_PROGRESS:
                View view3 = mInflater.inflate(R.layout.custom_chooser_progress, parent, false);

                viewHolder = new ViewHolderProgress(view3);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }

        return viewHolder;


    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Uri uri = Uri.fromFile(new File(mData.get(position).getDATA()));
        //bitmap = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
        switch (holder.getItemViewType()) {
            case TYPE_CONTENT:
                ViewHolder viewHolder = (ViewHolder)holder;

                //String sb = String.valueOf(uri);
                //sb = sb.substring(sb.length()-3,uri.toString().length());
                if (uri!=null){

                    Glide.with(context).asBitmap().load(cacheImage.get(position)).override(150,150).into(viewHolder.imageView);
                    //Picasso.get().load("file://" + uri).resize(150,150).into(viewHolder.imageView);
                    viewHolder.duration.setVisibility(View.VISIBLE);
                    viewHolder.duration.setText(getTime(mData.get(position).getDURATION()));

                }
                break;

            case TYPE_DATE:
                ViewHolderDate viewHolderDate = (ViewHolderDate) holder;
                long timestamp = Long.parseLong(mData.get(position).getDATE_ADDED());
                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                c2.add(Calendar.DAY_OF_YEAR, -1);


                String dateString = new java.text.SimpleDateFormat("dd MMM, yyyy").format(new java.util.Date(Long.parseLong(mData.get(position).getDATE_ADDED()) * 1000));
                String today = new java.text.SimpleDateFormat("dd MMM, yyyy").format(c1.getTimeInMillis());
                String yesterday = new java.text.SimpleDateFormat("dd MMM, yyyy").format(c2.getTimeInMillis());

                if (dateString.equals(today)){
                    viewHolderDate.date.setText("Today");
                }
                else if (dateString.equals(yesterday)){
                    viewHolderDate.date.setText("Yesterday");
                }
                else {
                    viewHolderDate.date.setText(dateString);
                }





                //Uri uri2 = Uri.fromFile(new File(mData.get(position).getDATA()));
                //String sb1 = String.valueOf(uri2);
               // sb1 = sb1.substring(sb1.length()-3,uri2.toString().length());
                if (uri!=null){
                    //MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    //mmr.setDataSource( context, uri2 );
                   // Bitmap bm = mmr.getFrameAtTime(2000, MediaMetadataRetriever.OPTION_NEXT_SYNC );
                   // Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, 150, 150, false);

                    //viewHolderDate.img.setImageBitmap(resizedBitmap);
                    Glide.with(context).asBitmap().load(cacheImage.get(position)).override(150,150).into(viewHolderDate.img);
                   // Picasso.get().load("file://" + uri2).resize(150,150).into(viewHolderDate.img);
                    viewHolderDate.duration.setVisibility(View.VISIBLE);
                    viewHolderDate.duration.setText(getTime(mData.get(position).getDURATION()));

                }
                break;
            case TYPE_PROGRESS:
                ViewHolderProgress viewHolderProgress = (ViewHolderProgress) holder;
                viewHolderProgress.progressBar.setIndeterminate(true);
                break;
            default:
                Toast.makeText(context,"No data",Toast.LENGTH_SHORT).show();
        }
    }



    private String getTime(String duration) {

        Date date = new Date(Integer.parseInt(duration));
        SimpleDateFormat formatter= new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        String formatted = formatter.format(date );

        return formatted;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}