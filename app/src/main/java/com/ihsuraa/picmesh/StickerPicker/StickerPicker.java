package com.ihsuraa.picmesh.StickerPicker;

import android.graphics.Color;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ihsuraa.picmesh.R;

public class StickerPicker extends AppCompatActivity {
    private StickerPickerAdapter adapter;
    private int[] images = { R.drawable.sticky1, R.drawable.sticky2, R.drawable.female, R.drawable.male };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAnimation();
        setContentView(R.layout.activity_sticker_chooser);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.Sticker_chooser);
        int numberOfColumns = 3;
        //adapter = new StickerPickerAdapter(getApplicationContext(), images);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void setAnimation(){
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.BOTTOM);
        slide.setDuration(400);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);
    }
}
