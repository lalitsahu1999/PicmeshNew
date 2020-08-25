package com.ihsuraa.picmesh;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.picker.gallery.model.GalleryVideo;
import com.picker.gallery.model.interactor.GalleryPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentVideoChooser extends Fragment {
    private VideoChooserAdapter adapter;
    List<GalleryVideo> imageSub ;
    ArrayList<Bitmap> cacheImage;
    boolean loadmore=true;
    ArrayList<GalleryVideo> image ;
    protected Handler handler;
    GalleryVideo test;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_custom_chooser, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.chooser);
        int numberOfColumns = 3;
        image = new GalleryPicker(getContext()).getVideos();
        imageSub = new ArrayList<GalleryVideo>();
        cacheImage = new ArrayList<Bitmap>();
        Collections.reverse(image);
        imageSub = image.subList(0,10);

        handler = new Handler();
        getImagesByArray(imageSub);
        adapter = new VideoChooserAdapter(getContext(),imageSub ,cacheImage );
        //recyclerView.setNestedScrollingEnabled(false);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),numberOfColumns);
        //gridLayoutManager.setReverseLayout(true);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position)==0 || adapter.getItemViewType(position)==2 ){
                    return 3;
                }
                else {
                    return 1;
                }
            }
        } );


        recyclerView.setLayoutManager(gridLayoutManager);

        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


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
                if (lastVisiblePosition == imageSub.size()-1) {
                    if (loadmore) {
                        loadmore = false;
                        //Toast.makeText(getApplicationContext(),"last",Toast.LENGTH_SHORT).show();

                        addmoreItems();


                    }
                }


            }
        });


        return view;
    }
    private void getImagesByArray(List<GalleryVideo> fdata) {
        for (GalleryVideo video : fdata){
            cacheImage.add(ThumbnailUtils.createVideoThumbnail(Uri.fromFile(new File(video.getDATA())).getPath(), MediaStore.Video.Thumbnails.MINI_KIND));
        }

    }

    private void getImage(GalleryVideo fdata) {
            cacheImage.add(ThumbnailUtils.createVideoThumbnail(Uri.fromFile(new File(fdata.getDATA())).getPath(), MediaStore.Video.Thumbnails.MINI_KIND));
    }
    private void addmoreItems(){
        VideoChooserAdapter vc;

        loadmore=true;


        int currentSize = imageSub.size();
        int nextLimit = currentSize + 20;
        for (currentSize = imageSub.size();currentSize<nextLimit;currentSize++){
            image = new GalleryPicker(getContext()).getVideos();
            Collections.reverse(image);
            imageSub.add(image.get(currentSize));
            getImage(image.get(currentSize));
            //Toast.makeText(getApplicationContext(),image.get(currentSize).getDATA(),Toast.LENGTH_SHORT).show();
            adapter.notifyItemInserted(imageSub.size());
            if (imageSub.size() == image.size()){


                loadmore=false;
                break;
            }
        }



/*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //   remove progress item
                //imageSub.remove(imageSub.size() - 1);
                //adapter.notifyItemRemoved(imageSub.size());
                //add items one by one
                int currentSize = imageSub.size();
                int nextLimit = currentSize + 20;
                for (currentSize = imageSub.size();currentSize<nextLimit;currentSize++){
                    image = new GalleryPicker(getContext()).getVideos();
                    Collections.reverse(image);
                    imageSub.add(image.get(currentSize));
                    getImage(image.get(currentSize));
                    //Toast.makeText(getApplicationContext(),image.get(currentSize).getDATA(),Toast.LENGTH_SHORT).show();
                    adapter.notifyItemInserted(imageSub.size());
                    if (imageSub.size() == image.size()){

                        loadmore=false;
                        break;
                    }
                }

            }
        }, 2000);



 */



        adapter.notifyDataSetChanged();



    }
}