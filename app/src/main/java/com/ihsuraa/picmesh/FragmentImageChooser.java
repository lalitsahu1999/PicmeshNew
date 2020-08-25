package com.ihsuraa.picmesh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.picker.gallery.model.GalleryImage;
import com.picker.gallery.model.interactor.GalleryPicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentImageChooser extends Fragment {
    private ImageChooserAdapter adapter;
    List<GalleryImage> imageSub ;
    boolean loadmore=true;
    ArrayList<GalleryImage> image ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_custom_chooser, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.chooser);
        int numberOfColumns = 3;
        image = new GalleryPicker(getContext()).getImages();
        imageSub = new ArrayList<GalleryImage>();

        Collections.reverse(image);
        imageSub = image.subList(0,20);

        adapter = new ImageChooserAdapter(getContext(), imageSub);
        //recyclerView.setNestedScrollingEnabled(false);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),numberOfColumns);
        //gridLayoutManager.setReverseLayout(true);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position)==0){
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

    private void addmoreItems(){
        loadmore=true;
        int currentSize = imageSub.size();
        int nextLimit = currentSize + 20;
        for (currentSize = imageSub.size();currentSize<nextLimit;currentSize++){
            image = new GalleryPicker(getContext()).getImages();
            Collections.reverse(image);
            imageSub.add(image.get(currentSize));
            //Toast.makeText(getApplicationContext(),image.get(currentSize).getDATA(),Toast.LENGTH_SHORT).show();
            adapter.notifyItemInserted(imageSub.size());
            if (imageSub.size() == image.size()){
                loadmore=false;
                break;
            }
        }
        adapter.notifyDataSetChanged();



    }
}