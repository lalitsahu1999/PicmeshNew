package com.ihsuraa.picmesh;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.picker.gallery.model.GalleryImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImageChooserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GalleryImage> mData;
    private LayoutInflater mInflater;
    private Context context;
    //private CustomChooserAdapter.ItemClickListener mClickListener;
    private String date = "new date";
    public final int TYPE_DATE = 0;
    public final int TYPE_CONTENT = 1;
    private long day = 259200000;
    long currentTime = System.currentTimeMillis();
    long imageTime;
    String dateString = null , dat;
    Calendar calendar;
    long diffInDays;
    // data is passed into the constructor
    ImageChooserAdapter(Context context, List<GalleryImage> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
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
            Toast.makeText(context,mData.get(getAdapterPosition()).getDATA(),Toast.LENGTH_LONG).show();
        }
    }

    public class ViewHolderDate extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date;
        TextView duration;
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
            Toast.makeText(context,mData.get(getAdapterPosition()).getDATA(),Toast.LENGTH_LONG).show();
        }
    }



        @Override
        public int getItemViewType(int position) {


            if (position==0){
                currentTime = Long.parseLong(mData.get(position).getDATE_ADDED());
                return TYPE_DATE;
            }

            else {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(Long.parseLong(mData.get(position).getDATE_ADDED())*1000));
                String dateString2 = formatter.format(new Date(Long.parseLong(mData.get(position-1).getDATE_ADDED())*1000));
                if (dateString.equals(dateString2)){
                    return  TYPE_CONTENT;
                }
                else {
                    return TYPE_DATE;
                }
            }




/*
            if (position==0){

                Toast.makeText(context,value,Toast.LENGTH_SHORT).show();
                return TYPE_DATE;
            }
            else {
                return TYPE_CONTENT;
            }


 */


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
                default:
                    throw new IllegalStateException("Unexpected value: " + viewType);
            }
            return viewHolder;





        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            switch (holder.getItemViewType()) {
                case TYPE_CONTENT:
                    ViewHolder viewHolder = (ViewHolder)holder;
                    Uri uri = Uri.fromFile(new File(mData.get(position).getDATA()));
                    String sb = String.valueOf(uri);
                    sb = sb.substring(sb.length()-3,uri.toString().length());
                    if (uri!=null){
                        Picasso.get().load("file://" + uri).resize(150,150).into(viewHolder.imageView);
                          //Toast.makeText(context,mData.get(position).getDATE_MODIFIED(),Toast.LENGTH_SHORT).show();

                        //Toast.makeText(context,dateString,Toast.LENGTH_SHORT).show();
                        if (sb.equals("gif")){
                            viewHolder.duration.setText("gif");
                            viewHolder.duration.setVisibility(View.VISIBLE);
                        }
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



                    Uri uri2 = Uri.fromFile(new File(mData.get(position).getDATA()));
                    String sb1 = String.valueOf(uri2);
                    sb1 = sb1.substring(sb1.length()-3,uri2.toString().length());
                    if (uri2!=null){
                        Picasso.get().load("file://" + uri2).resize(150,150).into(viewHolderDate.img);

                        if (sb1.equals("gif")){
                            viewHolderDate.duration.setText("gif");
                            viewHolderDate.duration.setVisibility(View.VISIBLE);
                        }
                    }
                    break;

                default:
                    Toast.makeText(context,"No data",Toast.LENGTH_SHORT).show();
            }
        }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}