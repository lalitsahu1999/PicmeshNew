package com.ihsuraa.picmesh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class galleryChooser extends AppCompatActivity {
    RecyclerView recyclerView;
    boolean loadmore=true;
    int numberOfColumns = 3;
    private int count;
    private ArrayList<Bitmap> thumbnails;
    private String[] videoLength;
    private String[] totalSecs;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private int[] typeMedia;
    Cursor imagecursor;
    private ImageAdapter imageAdapter;
    int limit = 30;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_chooser);
        recyclerView = findViewById(R.id.listview);

        String[] columns = { MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE,

        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
        Uri queryUri = MediaStore.Files.getContentUri("external");

        imagecursor = getApplicationContext().getContentResolver().query(queryUri,
                columns,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );



        this.count = imagecursor.getCount();
        this.thumbnails = new ArrayList<Bitmap>();
        this.arrPath = new String[this.count];
        this.typeMedia = new int[this.count];
        this.thumbnailsselection = new boolean[this.count];
        this.videoLength = new String[this.count];
        this.totalSecs = new String[this.count];


        if (this.count < limit){
            limit = this.count;
        }

        fetchThumbnails(0,limit);
        imageAdapter = new ImageAdapter(this, thumbnails);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this,numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(imageAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //Toast.makeText(getApplicationContext(),String.valueOf(newState),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = ((GridLayoutManager)recyclerView.getLayoutManager());

                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition > thumbnails.size()/2) {
                    if (loadmore) {
                        loadmore = false;
                        //Toast.makeText(getApplicationContext(),"last",Toast.LENGTH_SHORT).show();

                        addmoreItems();


                    }
                }


            }
        });
    }

    private void addmoreItems(){
        loadmore=true;
        int currentSize = thumbnails.size();
        int nextLimit = currentSize + 30;
        fetchThumbnails(currentSize,nextLimit);




    }

    private void fetchThumbnails(int init, int limit) {
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        for (int i = init; i < limit; i++) {
            String timeString;
            imagecursor.moveToPosition(i);
            int id = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 4;
            bmOptions.inPurgeable = true;
            int type = imagecursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int t = imagecursor.getInt(type);
            if(t == 1) {
                thumbnails.add( MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id,
                        MediaStore.Images.Thumbnails.MINI_KIND, bmOptions));
            }
            else if(t == 3) {
                thumbnails.add(MediaStore.Video.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id,
                        MediaStore.Video.Thumbnails.MINI_KIND, bmOptions));

                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(getApplicationContext(), Uri.parse(imagecursor.getString(dataColumnIndex)));


                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    retriever.release();
                    long totalSec = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(time));
                    long hours = totalSec / 3600;
                    long mins = (totalSec % 3600) / 60;
                    long secs = totalSec % 60;


                    if (hours == 0){
                        timeString = String.format("%02d:%02d", mins , secs);
                    }else {
                        timeString = String.format("%02d:%02d:%02d", hours , mins , secs);
                    }
                    videoLength[i] =  timeString;
                    totalSecs[i] = String.valueOf(totalSec);
                } catch (Exception e){
                    // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }




            }

            if ( imageAdapter!= null){
                imageAdapter.notifyItemInserted(thumbnails.size());
                imageAdapter.notifyDataSetChanged();

            }

            arrPath[i]= imagecursor.getString(dataColumnIndex);
            typeMedia[i] = imagecursor.getInt(type);

            if (thumbnails.size() == this.count){
                loadmore=false;
                imagecursor.close();
                break;
            }
        }

    }

    public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private LayoutInflater mInflater;
        private Context context;
        private ArrayList<Bitmap> bitmaps;

        ImageAdapter(Context context, ArrayList<Bitmap> data) {
            this.mInflater = LayoutInflater.from(context);
            this.bitmaps = data;
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imageview;
            ImageView videoICON;
            TextView videoTime;
            LinearLayout showVideoTime;
            int id;
            ViewHolder(View itemView) {
                super(itemView);
                imageview = (ImageView) itemView.findViewById(R.id.dateItem);
                videoICON = (ImageView) itemView.findViewById(R.id.video);
                videoTime = (TextView) itemView.findViewById(R.id.time) ;
                showVideoTime = (LinearLayout) itemView.findViewById(R.id.timer);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {

                Intent intent=new Intent();






                imagecursor.moveToPosition(id);
                String stringUrl = imagecursor.getString(imagecursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                intent.putExtra("uri",stringUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (videoLength[id]!=null){
                    intent.putExtra("videoLength",totalSecs[id]);
                }

                ((Activity) context).setResult(21, intent);
                ((Activity) context).finish();

            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            View view = mInflater.inflate(R.layout.custom_picker_item, parent, false);
            viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder)holder;
            viewHolder.imageview.setId(position);
            if(typeMedia[position] == 1)
                viewHolder.showVideoTime.setVisibility(View.GONE);
            else if(typeMedia[position] == 3)
            {
                viewHolder.videoTime.setText(videoLength[position]);
               viewHolder.showVideoTime.setVisibility(View.VISIBLE);
            }
            viewHolder.imageview.setImageBitmap(bitmaps.get(position));
            viewHolder.id = position;

        }

        @Override
        public int getItemCount() {
            return bitmaps.size();
        }
    }

    public void setAnimation(){
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.BOTTOM);
        slide.setDuration(400);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(galleryChooser.this,statusCamera.class);
        startActivity(intent);

    }
}
