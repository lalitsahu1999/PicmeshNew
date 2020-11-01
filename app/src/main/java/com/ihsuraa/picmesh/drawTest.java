package com.ihsuraa.picmesh;


import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.divyanshu.colorseekbar.ColorSeekBar;

public class drawTest extends AppCompatActivity {

    private CanvasView customCanvas;
    SeekBar seekBar;
    ColorSeekBar colorSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawtest);



        colorSeekBar = (ColorSeekBar) findViewById(R.id.color_seek_bar) ;
        customCanvas = (CanvasView) findViewById(R.id.custom_canvas);
        seekBar = (SeekBar) findViewById(R.id.seekBar_luminosite);
     
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                customCanvas.changePaintWidth(i);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String width = progress + "f";
                //customCanvas.changePaintWidth(Float.parseFloat(width));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void onClick(View view){
        customCanvas.clearCanvas();
    }

    @Override
    public void onBackPressed() {
        customCanvas.clearCanvas();
    }
}



