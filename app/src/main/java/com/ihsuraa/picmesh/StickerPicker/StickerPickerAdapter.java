package com.ihsuraa.picmesh.StickerPicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ihsuraa.picmesh.R;
import com.ihsuraa.picmesh.statusCamera;

import java.util.ArrayList;
import java.util.List;

public class StickerPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int[] mData;
    private Snackbar mSnackbar;
    private LayoutInflater mInflater;
    private Context context;
    private FrameLayout drawingView;
    private  ImageView imageView;
    TextView undoDraw;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    private float oldDist = 1f;
    private int mode = NONE;
    float d = 0f;
    float newRot = 0f;
    float[] lastEvent = null;
    ArrayList<View> all = new ArrayList<View>();
    //private CustomChooserAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
   public StickerPickerAdapter(Context context, int[] data , Snackbar snackbar , FrameLayout view , ArrayList<View> views , TextView undo) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.mSnackbar = snackbar;
        this.drawingView = view;
        this.all = views;
        this.undoDraw = undo;
    }





    public class StickersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        StickersViewHolder(View itemView){
            super(itemView);
            img = itemView.findViewById(R.id.sticky);
            img.setOnClickListener(this);

        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onClick(View v) {
            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            }
             imageView = new ImageView(context);
            imageView.setImageResource(mData[getAdapterPosition()]);
            imageView.setMaxHeight(80);
            imageView.setMaxWidth(80);
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);

            params.x = 0;
            params.y = 0;
            params.height = 200;
            params.width = 200;
            params.gravity = Gravity.CENTER;
            final FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            params1.gravity = Gravity.CENTER;
            params1.height = 200;
            params1.width = 200;
            imageView.setLayoutParams(params1);

            if (undoDraw.getVisibility() == View.INVISIBLE){
                undoDraw.setVisibility(View.VISIBLE);
            }


            drawingView.addView(imageView);
            all.add(imageView);
           // Toast.makeText(context,mData[getAdapterPosition()],Toast.LENGTH_SHORT).show();
            imageView.setOnTouchListener(new View.OnTouchListener() {

                private FrameLayout.LayoutParams updatedParams = params1;
                int x,y;
                float touchedX , touchedY;


                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    /*
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            x = updatedParams.leftMargin;
                            y = updatedParams.topMargin;

                            touchedX = event.getRawX();
                            touchedY = event.getRawY();

                            break;

                        case MotionEvent.ACTION_MOVE:

                            updatedParams.leftMargin = (int) (x + (event.getRawX() - touchedX));
                            updatedParams.topMargin = (int) (y + (event.getRawY() - touchedY));
                            drawingView.updateViewLayout(v,updatedParams);

                        default:
                            break;
                    }

                     */

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:

                            x = updatedParams.leftMargin;
                            y = updatedParams.topMargin;

                            touchedX = event.getRawX();
                            touchedY = event.getRawY();
                            mode = DRAG;
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            oldDist = spacing(event);
                            if(oldDist > 10f){
                                mode = ZOOM;
                            }
                            lastEvent = new float[4];
                            lastEvent[0] = event.getX(0);
                            lastEvent[1] = event.getX(1);
                            lastEvent[2] = event.getY(0);
                            lastEvent[3] = event.getY(1);
                            d = rotation(event);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            mode = NONE;
                            lastEvent = null;
                            break;

                        case MotionEvent.ACTION_OUTSIDE:
                            mode = NONE;
                            lastEvent = null;

                        case MotionEvent.ACTION_MOVE:
                            if(mode == DRAG){
                                updatedParams.leftMargin = (int) (x + (event.getRawX() - touchedX));
                                updatedParams.topMargin = (int) (y + (event.getRawY() - touchedY));
                                drag(event , v , updatedParams);
                            } else if(mode == ZOOM){
                                float newDist = spacing(event);
                                if (newDist > 10f) {

                                    float scale = newDist / oldDist * imageView.getScaleX();
                                    imageView.setScaleX(scale);
                                    imageView.setScaleY(scale);
                                   // zoom(event);
                                }
                                if (lastEvent != null) {
                                    newRot = rotation(event);
                                    imageView.setRotation((float) (imageView.getRotation() + (newRot - d)));
                                }
                            }
                            break;
                    }

                    return true;
                }
            });
            mSnackbar.dismiss();
        }
    }

    private void drag(MotionEvent event, View v, FrameLayout.LayoutParams updatedParams) {
        drawingView.updateViewLayout(v,updatedParams);

    }



    public void zoom(MotionEvent event){
        //matrix.getValues(matrixValues);

        float newDist = spacing(event);
        Toast.makeText(context,String.valueOf(newDist),Toast.LENGTH_SHORT).show();


    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float)Math.sqrt(x * x + y * y);
    }



    @Override
    public int getItemViewType(int position) {


       return position;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.sticker_chooser_layout, parent, false);
        return (new StickersViewHolder(view));


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

                StickersViewHolder myHolder = (StickersViewHolder) holder;

                myHolder.img.setImageResource(mData[position]);


    }

    @Override
    public int getItemCount() {
        return mData.length;
    }


}