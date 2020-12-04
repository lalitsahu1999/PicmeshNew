package com.ihsuraa.picmesh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divyanshu.colorseekbar.ColorSeekBar;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.ihsuraa.picmesh.StickerPicker.StickerPicker;
import com.ihsuraa.picmesh.StickerPicker.StickerPickerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class statusCamera extends AppCompatActivity {
    TextureView textureView ;
    TextureView videoview;
    Surface videoSurface;
    Snackbar stickerSnackbar;
    ImageButton btnCapture, btnRotate, btnFlash, imgGallery, btnSticker , btnTrim;
    ToggleButton btnBrush;
    FrameLayout frameLayout, drawLayout , statusImageLayout;
    RangeSlider rangeSlider;
    ImageView seekbarBall,intentImageview;
    View textureBackground;
    long mediaPlayerStartPosition;

    LinearLayout ballLayout;
    public ArrayList<View> allViews;
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";
    private boolean isFlashSupported;
    private boolean isTorchOn, isClicked = true , isTrim = false;
    boolean mManualFocusEngaged;
    SurfaceHolder mHolder;
    Paint paint;
    public float finger_spacing = 0;
    public int zoom_level = 1;
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    //check state oreintation of output image
    List<Surface> surfaces;
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    File mCurrentFile;

    private String cameraId;
    private String frontCamAvailable = null;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension, videoSize;
    private ImageReader imageReader;
    private int mSensorOrientation;
    private boolean imageCaptured = false, videoCaptured = false, drawView = false;
    //save to file
    CardView captureBack;
    SurfaceView surfaceView;
    boolean playingVideo = false;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported, mIsRecordingVideo, isGalIntent = false, isStickerIntent = false;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    CameraManager manager;
    CameraCharacteristics characteristics;
    MeteringRectangle focusAreaTouch;
    Canvas canvas;
    ConstraintLayout constraintLayout;
    ProgressBar videoCaptureProgress;
    TextView undoDrawing , cancel ,done;
    DrawingView dv;
    Bitmap myBitmap, selectedBitmap;
    private Paint mPaint;
    ColorSeekBar colorSeekBar;
    SeekBar seekBar;
    TextView seekText;
    String fileUri;
    float y1, y2;
    long videoLengthInSecs;
    static final int MIN_DISTANCE = 150;
    private StickerPickerAdapter adapter;
    static final int NONE = 0;
    static final int DRAG = 1;
    Boolean BOTH = true;
    static final int ZOOM = 2;
    private float oldDist = 1f;
    private int mode = NONE;
    float d = 0f;
    float newRot = 0f;
    float current = 0f;
    float[] lastEvent = null;
    private int[] images = {R.drawable.sticky1, R.drawable.sticky2, R.drawable.female, R.drawable.male};
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    TextureView.SurfaceTextureListener videoTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            videoSurface = new Surface(surface);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_camera);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);


        constraintLayout = (ConstraintLayout) findViewById(R.id.constraint);
        captureBack = (CardView) findViewById(R.id.cardView);
        textureView = (TextureView) findViewById(R.id.view_finder);
        surfaceView = (SurfaceView) findViewById(R.id.video_texture);
        btnCapture = (ImageButton) findViewById(R.id.imgCapture);
        btnRotate = (ImageButton) findViewById(R.id.rotate_camera);
        btnFlash = (ImageButton) findViewById(R.id.flash);
        imgGallery = (ImageButton) findViewById(R.id.imgGallery);
        btnBrush = (ToggleButton) findViewById(R.id.brush);
        btnSticker = (ImageButton) findViewById(R.id.sticker);
        btnTrim = (ImageButton) findViewById(R.id.trim);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        statusImageLayout = (FrameLayout) findViewById(R.id.statusImageLayout);
        colorSeekBar = (ColorSeekBar) findViewById(R.id.color_seek_bar);
        seekBar = (SeekBar) findViewById(R.id.seekBar_luminosite);
        seekbarBall = (ImageView) findViewById(R.id.seekbar_ball);
        intentImageview = (ImageView) findViewById(R.id.statusImage);
        textureBackground = (View) findViewById(R.id.textureBackground);
        ballLayout = (LinearLayout) findViewById(R.id.linear);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cameraId = CAMERA_BACK;
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        videoCaptureProgress = (ProgressBar) findViewById(R.id.videoprogress);
        rangeSlider =  findViewById(R.id.rangeSlider);
        undoDrawing = (TextView) findViewById(R.id.undo);
        cancel = (TextView) findViewById(R.id.cancel);
        done = (TextView) findViewById(R.id.done);

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

                if (!Permissions.Check_RECORD_AUDIO(statusCamera.this)) {

                    Permissions.Request_RECORD_AUDIO(statusCamera.this, 100);
                } else {
                    if (!isGalIntent) {
                        openCamera();
                    }
                }


            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                videoSurface = holder.getSurface();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });


                btnCapture.setOnTouchListener(new View.OnTouchListener() {
                    private long firstTouchTS = 0;

                    @Override
                    public boolean onTouch(View v, final MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            firstTouchTS = System.currentTimeMillis();
                            new CountDownTimer(300, 100) {

                                public void onTick(long millisUntilFinished) {

                                }

                                public void onFinish() {

                                    if (event.getAction() != MotionEvent.ACTION_UP) {
                                        isClicked = false;
                                        videoCaptured = true;
                                        toggleVideoIcons();
                                        videoCaptureProgress.setVisibility(View.VISIBLE);
                                        // Toast.makeText(getApplicationContext(),"video",Toast.LENGTH_SHORT).show();
                                        startRecordingVideo();
                                    }


                                }

                            }.start();


                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            if (isClicked) {
                                takePicture();
                            } else {

                                videoCaptureProgress.setVisibility(View.INVISIBLE);
                                Toast.makeText(statusCamera.this, ((System.currentTimeMillis() - this.firstTouchTS) / 1000) + " seconds", Toast.LENGTH_LONG).show();
                                isClicked = true;
                                try {
                                    stopRecordingVideo();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                        return true;
                    }
                });

        btnBrush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {


                    dv = new DrawingView(statusCamera.this);
                    frameLayout.addView(dv);
                    allViews.add(dv);

                    drawView = true;
                    colorSeekBar.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    // frameLayout.removeAllViews();

                    dv.setDrawingCacheEnabled(true);

                    undoDrawing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (allViews.size() > 0) {
                                frameLayout.removeView(allViews.get(allViews.size() - 1));
                                allViews.remove(allViews.size() - 1);
                            } else {

                                frameLayout.removeAllViews();
                                dv = new DrawingView(statusCamera.this);
                                frameLayout.addView(dv);


                                //  Toast.makeText(getApplicationContext(),String.valueOf(allViews.size()),Toast.LENGTH_SHORT).show();
                            }


                            //dv.onClickUndo();
                            /*
                            if (dv.paths.size()>0) {
                                dv.paths.remove(dv.paths.size() - 1);
                                dv.mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                                for (Path p : dv.paths) {
                                    Toast.makeText(getApplicationContext(),"hii",Toast.LENGTH_SHORT).show();
                                    dv.mCanvas.drawPath(p, mPaint);
                                }
                                dv.invalidate();
                            }

                             */
                        }


                    });


                    colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
                        @Override
                        public void onColorChangeListener(int i) {
                            mPaint.setColor(i);
                        }
                    });

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            String width = progress / 2 + "f";
                            mPaint.setStrokeWidth(Float.parseFloat(width));
                            int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(progress / 2, progress / 2);
                            if (!(progress > 90)) {
                                seekbarBall.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                            }

                            seekbarBall.setLayoutParams(parms);
                            //dv.strokes.add(Float.parseFloat(width));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            ballLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            ballLayout.setVisibility(View.GONE);
                        }
                    });

                } else {

                    drawView = false;

                    //undoDrawing.setVisibility(View.INVISIBLE);
                    // Canvas canvas = textureView.lockCanvas();
                    // Bitmap newV = dv.getDrawingCache();
                    // Bitmap newB = Bitmap.createScaledBitmap(selectedBitmap,canvas.getWidth(),canvas.getHeight(),true);

                    // Bitmap bmOverlay = Bitmap.createBitmap(newB.getWidth(), newB.getHeight(), newB.getConfig());

                    // canvas.drawBitmap(bmp2, 0, 0, null);
                    // canvas.drawBitmap(newB,0,0,null);
                    // canvas.drawBitmap( newV, new Matrix(), null);
                    //canvas.drawPath( dv.mPath,  mPaint);
                    //canvas.drawPath( dv.circlePath,  dv.circlePaint);
                    // textureView.unlockCanvasAndPost(canvas);
                    colorSeekBar.setVisibility(View.INVISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    //frameLayout.removeView(dv);
                }
            }
        });

        btnSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stickerIntent = new Intent(statusCamera.this, StickerPicker.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(statusCamera.this);
                // startActivityForResult(stickerIntent,25,options.toBundle());
                ViewGroup view = (ViewGroup) findViewById(android.R.id.content);


                stickerSnackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) stickerSnackbar.getView();
                layout.setMinimumHeight(1200);
                View snackView = getLayoutInflater().inflate(R.layout.activity_sticker_chooser, null);

                RecyclerView recyclerView = (RecyclerView) snackView.findViewById(R.id.Sticker_chooser);
                int numberOfColumns = 3;
                adapter = new StickerPickerAdapter(getApplicationContext(), images, stickerSnackbar, frameLayout, allViews, undoDrawing);
                final GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), numberOfColumns);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(adapter);
                layout.setPadding(0, 0, 0, 0);
                layout.addView(snackView, 0);

                layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                y1 = event.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                y2 = event.getY();
                                float deltaX = y2 - y1;
                                if (Math.abs(deltaX) > MIN_DISTANCE) {
                                    if (y2 > y1) {
                                        Toast.makeText(getApplicationContext(), "bottom to up swipe ", Toast.LENGTH_SHORT).show();

                                        stickerSnackbar.dismiss();
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Tap or Else", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }

                        return true;
                    }
                });
                stickerSnackbar.show();

            }
        });


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFlash();
            }
        });
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startgalleryIntent();
            }
        });

        btnTrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrimOptions();
            }
        });


        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (cameraDevice != null) {
                    if (cameraCaptureSessions != null) {


                        final int actionMasked = motionEvent.getActionMasked();
                        float maxzoom = (characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)) * 10;

                        Rect sensorArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                        int action = motionEvent.getAction();
                        float current_finger_spacing;

                        if (motionEvent.getPointerCount() > 1) {
                            // Multi touch logic
                            current_finger_spacing = getFingerSpacing(motionEvent);
                            if (finger_spacing != 0) {
                                if (current_finger_spacing > finger_spacing && maxzoom > zoom_level) {
                                    if (zoom_level < 40) {
                                        zoom_level++;
                                    }

                                } else if (current_finger_spacing < finger_spacing && zoom_level > 1) {
                                    zoom_level--;
                                }
                                int minW = (int) (sensorArraySize.width() / maxzoom);
                                int minH = (int) (sensorArraySize.height() / maxzoom);
                                int difW = sensorArraySize.width() - minW;
                                int difH = sensorArraySize.height() - minH;
                                int cropW = difW / 100 * (int) zoom_level;
                                int cropH = difH / 100 * (int) zoom_level;
                                cropW -= cropW & 3;
                                cropH -= cropH & 3;
                                Rect zoom = new Rect(cropW, cropH, sensorArraySize.width() - cropW, sensorArraySize.height() - cropH);
                                captureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
                            }
                            finger_spacing = current_finger_spacing;
                            try {
                                cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);

                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                        } else {
                            if (action == MotionEvent.ACTION_DOWN) {

                                final int halfTouchWidth = 60; //(int)motionEvent.getTouchMajor(); //TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
                                final int halfTouchHeight = 60; //(int)motionEvent.getTouchMinor();
                                //TODO: here I just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
                                final int y = Math.max((int) motionEvent.getY() - halfTouchHeight, 0);
                                final int x = Math.max((int) motionEvent.getX() - halfTouchWidth, 0);

                                String text = "You click at x = " + x + " and y = " + y;


                                focusAreaTouch = new MeteringRectangle(x, y,
                                        halfTouchWidth * 2,
                                        halfTouchHeight * 2,
                                        MeteringRectangle.METERING_WEIGHT_MAX - 1);


                                try {
                                    //first stop the existing repeating request
                                    cameraCaptureSessions.stopRepeating();

                                    //cancel any existing AF trigger (repeated touches, etc.)
                                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
                                    cameraCaptureSessions.capture(captureRequestBuilder.build(), captureCallbackHandler, mBackgroundHandler);

                                    if (isMeteringAreaAFSupported()) {
                                        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{focusAreaTouch});
                                    }
                                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
                                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
                                    captureRequestBuilder.setTag("FOCUS_TAG"); //we'll capture this later for resuming the preview

                                    //then we ask for a single request (not repeating!)
                                    cameraCaptureSessions.capture(captureRequestBuilder.build(), captureCallbackHandler, mBackgroundHandler);
                                    mManualFocusEngaged = true;
                                    return true;

                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }


                                return true;
                            }
                        }

                    }
                }

                return true;
            }
        });


    }

    private void showTrimOptions() {
        isTrim = true;
        btnBrush.setVisibility(View.INVISIBLE);
        btnSticker.setVisibility(View.INVISIBLE);
        colorSeekBar.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);
        rangeSlider.setVisibility(View.VISIBLE);
        btnTrim.setVisibility(View.INVISIBLE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideTrimOptions();
                onBackPressed();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();

                int currentValue = (int) current;
                int nn = (int) TimeUnit.SECONDS.toMillis((long) currentValue);
                mMediaPlayer.seekTo(nn);
                hideTrimOptions();
                onBackPressed();
            }
        });

        rangeSlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {

                long hours = (long) (value / 3600);
                long mins = (long) ((value % 3600) / 60);
                long secs = (long) (value % 60);

                String timeString;

                if (hours == 0){
                    timeString = String.format("%02d:%02d", mins , secs);
                }else {
                    timeString = String.format("%02d:%02d:%02d", hours , mins , secs);
                }
                return timeString;
            }
        });


        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> values = slider.getValues();
                long currentMediaPlayer = mMediaPlayer.getCurrentPosition();

                if (current!=values.get(0)){
                    int currentValue = (int) current;
                    int nn = (int) TimeUnit.SECONDS.toMillis((long) currentValue);
                    mMediaPlayer.seekTo(nn);
                }
                current = values.get(0);
            }
        });


        rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {

                //Toast.makeText(getApplicationContext(),String.valueOf(values.get(0)),Toast.LENGTH_SHORT).show();

               // long init = TimeUnit.SECONDS.toMillis(Long.parseLong(String.valueOf(values.get(0))) );

                //Toast.makeText(getApplicationContext(),String.valueOf(currentValue), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setImageviewTouchListener(final ImageView imageView , final FrameLayout.LayoutParams params ){


        imageView.setOnTouchListener(new View.OnTouchListener() {

            private FrameLayout.LayoutParams updatedParams = params;
            int x,y;
            float touchedX , touchedY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {



                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:

                       // mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        x = updatedParams.leftMargin;
                        y = updatedParams.topMargin;
                        touchedX = event.getRawX();
                        touchedY = event.getRawY();

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

                            imageView.setLayoutParams(updatedParams);
                           //frameLayout.updateViewLayout(v,updatedParams);
                           // drag(event , v , updatedParams);
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

                            updatedParams.leftMargin = (int) (x + (event.getRawX() - touchedX));
                            updatedParams.topMargin = (int) (y + (event.getRawY() - touchedY));

                            imageView.setLayoutParams(updatedParams);
                        }
                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setVideoTouchListener(final SurfaceView videoView , final FrameLayout.LayoutParams params ){


        videoView.setOnTouchListener(new View.OnTouchListener() {

            private FrameLayout.LayoutParams updatedParams = params;
            int x,y;
            float touchedX , touchedY;
           int windowwidth ,windowheight;


            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onTouch(View v, MotionEvent event) {



                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:

                        // mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        x = updatedParams.leftMargin;
                        y = updatedParams.topMargin;
                        touchedX = event.getRawX();
                        touchedY = event.getRawY();


                        windowheight = frameLayout.getMeasuredHeight();


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

                            videoView.setLayoutParams(updatedParams);
                            //frameLayout.updateViewLayout(v,updatedParams);
                            // drag(event , v , updatedParams);
                        } else if(mode == ZOOM){
                            float newDist = spacing(event);
                            if (newDist > 30f ) {

                                float scale = newDist / oldDist ;


                                int oldH =videoView.getHolder().getSurfaceFrame().height();
                                int oldW =videoView.getHolder().getSurfaceFrame().width();



                                int newH =(int) (oldH*scale);
                                int newW =(int) (oldW*scale);




                               // videoView.getHolder().setFixedSize()
                                //int Maxheight = frameLayout.getMeasuredHeight();
                               // int Maxwidth = frameLayout.getMeasuredWidth();


                                if( newH>frameLayout.getMeasuredWidth() && newH <(frameLayout.getMeasuredHeight()+30)) {
                                    updatedParams.width = newW;
                                    updatedParams.height = newH;


                                }

                                


                                mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                    @Override
                                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                                    }
                                });
                              //  videoView.setScaleX(scale);
                                //videoView.setScaleY(scale);
                                //

                               // updatedParams.height =   (int) (updatedParams.height * scale);
                              //  updatedParams.width =  (int) (updatedParams.width * scale);
                               //videoView.setScaleX(scale);
                               // videoView.setScaleY(scale);
                                // zoom(event);
                            }
                            if (lastEvent != null) {
                                newRot = rotation(event);
                                videoView.setRotation((float) (videoView.getRotation() + (newRot - d)));
                            }





                                updatedParams.leftMargin = (int) (x + (event.getRawX() - touchedX));
                                updatedParams.topMargin = (int) (y + (event.getRawY() - touchedY));
                          //  Toast.makeText(getApplicationContext(),String.valueOf(y),Toast.LENGTH_SHORT).show();



                                videoView.setLayoutParams(updatedParams);


                        }
                        break;
                }

                return true;
            }
        });
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

    private void drag(MotionEvent event, View v, FrameLayout.LayoutParams updatedParams) {
      //  drawingView.updateViewLayout(v,updatedParams);

    }


    public Bitmap getBitmapFromView(View view) {


        Bitmap bmOverlay = Bitmap.createBitmap(selectedBitmap.getWidth(), selectedBitmap.getHeight(), selectedBitmap.getConfig());

        Canvas canvas = new Canvas(bmOverlay);
        Drawable bgDrawable = view.getBackground();
        view.draw(canvas);
        return bmOverlay;
    }

    private void toggleVideoIcons() {
        if (videoCaptured) {
            btnRotate.setVisibility(View.INVISIBLE);
            btnFlash.setVisibility(View.INVISIBLE);
            imgGallery.setVisibility(View.INVISIBLE);
        } else {
            btnRotate.setVisibility(View.VISIBLE);
            btnFlash.setVisibility(View.VISIBLE);
            imgGallery.setVisibility(View.VISIBLE);
        }
    }

    CameraCaptureSession.CaptureCallback captureCallbackHandler = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            mManualFocusEngaged = false;

            if (request.getTag() == "FOCUS_TAG") {
                //the focus trigger is complete -
                //resume repeating (preview surface will get frames), clear AF trigger
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                updatePreview();

            }
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.e("TAG", "Manual AF failure: " + failure);
            mManualFocusEngaged = false;
        }
    };

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private boolean isMeteringAreaAFSupported() {
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            return characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
        } catch (Exception e) {
            return false;
        }


    }

    private void startgalleryIntent() {
        closePreviewSession();
        closeCameraDevice();
        Intent galIntent = new Intent(statusCamera.this, galleryChooser.class);
        galIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // galIntent.putExtra("IMAGES_LIMIT", 1);
        // galIntent.putExtra("VIDEOS_LIMIT", 1);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        //startActivity(galIntent,options.toBundle());
        startActivityForResult(galIntent, 21, options.toBundle());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        closePreviewSession();
        closeCameraDevice();
        hideCameraButtons();
        if (requestCode == 21) {
            final String uri = data.getStringExtra("uri");
            fileUri = data.getStringExtra("uri");
            boolean mp4 = false;
            String m;
            m = uri.substring(uri.length() - 3);


            if (uri != null) {

                isGalIntent = true;
                btnRotate.setVisibility(View.INVISIBLE);
                btnFlash.setVisibility(View.INVISIBLE);

                allViews = new ArrayList<View>();
                btnBrush.setVisibility(View.VISIBLE);

                btnSticker.setVisibility(View.VISIBLE);
                btnTrim.setVisibility(View.VISIBLE);
                seekBar.setProgress(5);


                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStrokeWidth(12);
                mPaint.setColor(colorSeekBar.getColor());

                if (m.equals("jpg")) {

                    imageCaptured = true;



                    // updatePreview();
                    if (cameraDevice == null) {
                        File imgFile = new File(uri);
                        Uri nUri = Uri.fromFile(imgFile);

                        //Canvas canvas = textureView.lockCanvas();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), nUri);
                            int   height = frameLayout.getHeight();
                            if (bitmap!= null){


                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    public void onGenerated(Palette p) {
                                        int defaultValue = Color.parseColor("#484848");
                                        int vibrant = p.getDarkVibrantColor(defaultValue);
                                        textureBackground.setBackgroundColor(vibrant);

                                        //Toast.makeText(getApplicationContext(),String.valueOf(vibrant),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) height, (int) 600, true);

                            intentImageview.setImageBitmap(bitmap);
                            textureBackground.setVisibility(View.VISIBLE);

                            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT,Gravity.CENTER);


                            intentImageview.setLayoutParams(params);
                            setImageviewTouchListener(intentImageview,params);
                            //textureBackground.setBackgroundColor(808080);
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                       myBitmap = BitmapFactory.decodeFile(uri);
                        final int height = options.outHeight;
                        final int width = options.outWidth;


                       // BitmapFactory.decodeFile("content:/"+imgFile.getPath(), options);

                        Toast.makeText(getApplicationContext(),String.valueOf(height),Toast.LENGTH_SHORT).show();
                       // selectedBitmap = Bitmap.createScaledBitmap(myBitmap, canvas.getWidth(), canvas.getHeight(), true);
                        //Bitmap newB = Bitmap.createScaledBitmap(myBitmap, width, height, true);
                        //Bitmap bim = overlay(myBitmap,selectedBitmap);


                       // canvas.drawBitmap(selectedBitmap, 0, 0, null);
                       // textureView.unlockCanvasAndPost(canvas);

                        m = null;
                    }


                } else {


                    videoCaptured = true;
                    closePreviewSession();
                    hideCameraButtons();
                    closeCameraDevice();
                    //textureView = null;



                    List<Float> values = new ArrayList<Float>();



                    surfaceView.setVisibility(View.VISIBLE);
                    mMediaPlayer = new MediaPlayer();
                    surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                            videoSurface = holder.getSurface();

                            mMediaPlayer.setSurface(videoSurface);
                            try {



                                final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.MATCH_PARENT,Gravity.CENTER);
                                mMediaPlayer.setDataSource(fileUri);
                                mMediaPlayer.prepare();
                                videoLengthInSecs = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getDuration());

                                if (videoLengthInSecs <= 30){
                                    rangeSlider.setValues(0f,(float) videoLengthInSecs);
                                }
                                rangeSlider.setValueTo(videoLengthInSecs);
                                rangeSlider.setValueFrom(0);
                                rangeSlider.setStepSize(1f);

                                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {

                                    }
                                });
                                
                                mMediaPlayer.setLooping(true);
                                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                surfaceView.setLayoutParams(params);
                              //  surfaceView.setZOrderMediaOverlay(true);

                                setVideoTouchListener(surfaceView,params);
                                mMediaPlayer.start();




                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                        }

                        @Override
                        public void surfaceDestroyed(SurfaceHolder holder) {

                        }
                    });


                    textureBackground.setVisibility(View.VISIBLE);

                }

            }


        }

        if (requestCode == 25) {
            isStickerIntent = true;
            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
        }
        if (resultCode == 100) {
            Toast.makeText(getApplicationContext(), "mic permission not granted", Toast.LENGTH_SHORT).show();
        }
    }



    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    public void switchFlash() {
        try {
            if (cameraId.equals(CAMERA_BACK)) {
                if (isFlashSupported) {
                    if (isTorchOn) {
                        captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                        btnFlash.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.vector_flash_off));
                        isTorchOn = false;
                    } else {
                        captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                        btnFlash.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.vector_flash_on));
                        isTorchOn = true;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void switchCamera() {
        if (cameraId.equals(CAMERA_FRONT)) {
            cameraId = CAMERA_BACK;
            cameraDevice.close();
            reopenCamera();

        } else if (cameraId.equals(CAMERA_BACK)) {
            cameraId = CAMERA_FRONT;
            cameraDevice.close();
            reopenCamera();
        }
    }

    public void reopenCamera() {
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }


    protected void takePicture() {
        btnCapture.setEnabled(false);
        if (null == cameraDevice) {
            Log.e("TAG", "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;

                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    cameraCaptureSessions = session;
                    captureRequestBuilder = captureBuilder;
                    imageCaptured = true;
                    Toast.makeText(getApplicationContext(), "Saved:" + file, Toast.LENGTH_SHORT).show();

                    updatePreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void setUpMediaRecorder() throws IOException {

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mCurrentFile = getOutputMediaFile();

        mMediaRecorder.setOutputFile(mCurrentFile.getAbsolutePath());
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
        mMediaRecorder.setAudioSamplingRate(profile.audioSampleRate);
        int rotation = getWindow().getWindowManager().getDefaultDisplay().getRotation();
        if (cameraId.equals(CAMERA_FRONT)) {
            mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
        } else {
            mMediaRecorder.setOrientationHint(ORIENTATIONS.get(rotation));
        }

        mMediaRecorder.prepare();
    }

    private File getOutputMediaFile() {
        // External sdcard file location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                "Picmesh");
        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Log.d(TAG, "Oops! Failed create "
                //  + VIDEO_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID_" + timeStamp + ".mp4");
        return mediaFile;
    }

    public void startRecordingVideo() {

        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            surfaces = new ArrayList<>();

            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            captureRequestBuilder.addTarget(previewSurface);
            //MediaRecorder setup for surface
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);

            captureRequestBuilder.addTarget(recorderSurface);
            // Start a capture session
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSessions = session;

                    updatePreview();


                    // Start recording
                    mMediaRecorder.start();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closePreviewSession() {
        if (cameraCaptureSessions != null) {
            cameraCaptureSessions.close();
            cameraCaptureSessions = null;
        }
    }

    public void stopRecordingVideo() throws Exception {
        // UI
        //mIsRecordingVideo = false;
        try {
            cameraCaptureSessions.stopRepeating();
            cameraCaptureSessions.abortCaptures();
            //cameraCaptureSessions.close();

            //closePreviewSession();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        // Stop recording


        mMediaRecorder.stop();
        mMediaRecorder.reset();
        if (mCurrentFile != null) {
            hideCameraButtons();
            closeCameraDevice();
           // Toast.makeText(getApplicationContext(), mCurrentFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            startVideoPreview(mCurrentFile.getAbsolutePath());
        }
    }

    void closeCameraDevice() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }


    private void startVideoPreview(String path) {


        //
        // textureView.setSurfaceTextureListener(new S);
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;


            Surface surface = new Surface(texture);


            //ArrayList<GalleryVideo> image = new GalleryPicker(getApplicationContext()).getVideos();

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {

                }
            });

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();

                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                }
            });
            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                }
            });
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.start();

        } catch (IllegalArgumentException | IllegalStateException | IOException | SecurityException e) {
            // TODO Auto-generated catch block

            Toast.makeText(getApplicationContext(),"video error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {

            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);


            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //Toast.makeText(statusCamera.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);

        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
            if (imageCaptured) {
                closeCameraDevice();
                hideCameraButtons();
                btnCapture.setEnabled(true);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (1920 == size.getWidth() && 1080 == size.getHeight()) {
                return size;
            }
        }
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        //Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            //Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {

            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private void openCamera() {


        try {
            // cameraId = manager.getCameraIdList()[0];
            frontCamAvailable = manager.getCameraIdList()[1];
            if (!isGalIntent) {
                if (frontCamAvailable != null) {
                    btnRotate.setVisibility(View.VISIBLE);
                }
            }


            characteristics = manager.getCameraCharacteristics(cameraId);
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            isFlashSupported = available == null ? false : available;
            setupFlashButton();

            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);


            assert map != null;
            videoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));

            imageDimension = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    textureView.getWidth(), textureView.getHeight(), videoSize);
            if (cameraId == CAMERA_FRONT) {
                videoSize = map.getOutputSizes(MediaRecorder.class)[3];
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[3];
            }
           // Toast.makeText(getApplicationContext(), String.valueOf(imageDimension), Toast.LENGTH_SHORT).show();
            //Check realtime permission if run higher API 23
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_CAMERA_PERMISSION);
                return;
            }
            mMediaRecorder = new MediaRecorder();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setupFlashButton() {
        if (cameraId.equals(CAMERA_BACK) && isFlashSupported) {
            if (!isGalIntent) {
                btnFlash.setVisibility(View.VISIBLE);
            }

            if (isTorchOn) {
                btnFlash.setBackground(ContextCompat.getDrawable(getApplicationContext() , R.drawable.vector_flash_on));
            } else {
                btnFlash.setBackground(ContextCompat.getDrawable(getApplicationContext() , R.drawable.vector_flash_off));
            }

        } else {
            btnFlash.setVisibility(View.INVISIBLE);
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            if (!isGalIntent){
                openCamera();
            }


        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if(grantResults[1] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use camera without storage permission", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if(grantResults[2] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use camera without audio permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable())
            openCamera();
        else
            textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (isTrim){
            isTrim = false;
            mMediaPlayer.seekTo((int) mediaPlayerStartPosition);
            hideTrimOptions();

        }else {
            if (imageCaptured || videoCaptured){
                showCameraButtons();

                surfaceView.setVisibility(View.INVISIBLE);
                intentImageview.setVisibility(View.INVISIBLE);

                if (mMediaPlayer!=null){
                    // Toast.makeText(getApplicationContext(),"don",Toast.LENGTH_SHORT).show();
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                        mMediaPlayer = null;


                    }
                }


                if (isGalIntent ){
                    Canvas canvas = textureView.lockCanvas();
                    if (canvas!=null){
                        Paint clearPaint = new Paint();
                        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        canvas.drawPaint(clearPaint);

                    }
                    textureView.unlockCanvasAndPost(canvas);
                    isGalIntent = false;

                    drawView = false;
                    intentImageview.setImageBitmap(null);
                    if (btnSticker.getVisibility() == View.VISIBLE){
                        btnSticker.setVisibility(View.INVISIBLE);
                    }



                    Intent in = new Intent(statusCamera.this,galleryChooser.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                    startActivityForResult(in,21,options.toBundle());

                }
                videoCaptured = false;
                imageCaptured = false;
                textureBackground.setVisibility(View.INVISIBLE);
                reopenCamera();

            }
            else {
                super.onBackPressed();
            }
        }


    }

    private void hideTrimOptions() {

        btnBrush.setVisibility(View.VISIBLE);

        btnSticker.setVisibility(View.VISIBLE);
        btnTrim.setVisibility(View.VISIBLE);
        rangeSlider.setVisibility(View.INVISIBLE);
        done.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);

    }


    private void showCameraButtons() {
        btnFlash.setVisibility(View.VISIBLE);
        btnRotate.setVisibility(View.VISIBLE);
        btnCapture.setVisibility(View.VISIBLE);
        imgGallery.setVisibility(View.VISIBLE);
        captureBack.setVisibility(View.VISIBLE);
    }

    private void hideCameraButtons() {
        btnFlash.setVisibility(View.INVISIBLE);
        btnRotate.setVisibility(View.INVISIBLE);
        btnCapture.setVisibility(View.INVISIBLE);
        imgGallery.setVisibility(View.INVISIBLE);
        captureBack.setVisibility(View.INVISIBLE);
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread= null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbarCamera));
        }
    }


    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap  mBitmap, mBitmapBackup;
        private Canvas  mCanvas , Dcanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private ArrayList<Path> undonePaths = new ArrayList<Path>();
        private ArrayList<Path> paths = new ArrayList<Path>();
        ArrayList<Paint> paints = new ArrayList<Paint>();
        private int lastStroke = -1;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c ) {
            super(c);
            context=c;
            //mBitmap = bp;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);


             canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
             canvas.drawPath( mPath,  mPaint);







        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {

            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            paths.add(mPath);
            paints.add(mPaint);

            mPath.reset();
            mPath = new Path();

           // frameLayout.removeView(dv);
            undoDrawing.setVisibility(View.VISIBLE);
            DrawingView test = new DrawingView(context);
            frameLayout.addView(test);
            allViews.add(test);

        }

        public void onClickUndo () {
            if (paths.size()>0)
            {


                /*
                mCanvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
                mCanvas.drawPath(paths.get(paths.size()-1),mPaint);
                for (int i =0 ; i<= paths.size()-1; i++){
                    mCanvas.drawPath(paths.get(i),paints.get(i));
                }

                 */
                invalidate();
            }

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!drawView){
                return false;
            }
            else {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touch_start(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touch_move(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        touch_up();
                        invalidate();
                        break;
                }
                return true;
            }

        }
    }



}