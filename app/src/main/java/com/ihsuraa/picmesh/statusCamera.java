package com.ihsuraa.picmesh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.picker.gallery.model.GalleryImage;
import com.picker.gallery.model.GalleryVideo;
import com.picker.gallery.model.interactor.GalleryPicker;
import com.picker.gallery.view.PickerActivity;

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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;


public class statusCamera extends AppCompatActivity  {
    TextureView textureView;

    ImageButton btnCapture, btnRotate , btnFlash , imgGallery;
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";
    private boolean isFlashSupported;
    private boolean isTorchOn,isClicked = true;
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
    private Size imageDimension , videoSize;
    private ImageReader imageReader;
    private int mSensorOrientation;
    private boolean imageCaptured = false,videoCaptured = false;
    //save to file
    CardView captureBack;
    SurfaceView surfaceView;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported,mIsRecordingVideo;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    CameraManager manager;
    CameraCharacteristics characteristics;
    MeteringRectangle focusAreaTouch;
    Canvas canvas;
    ConstraintLayout constraintLayout;
    ProgressBar videoCaptureProgress;

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

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraint) ;
        captureBack = (CardView) findViewById(R.id.cardView);
        textureView = (TextureView) findViewById(R.id.view_finder);
        btnCapture = (ImageButton) findViewById(R.id.imgCapture);
        btnRotate = (ImageButton) findViewById(R.id.rotate_camera);
        btnFlash = (ImageButton) findViewById(R.id.flash);
        imgGallery = (ImageButton) findViewById(R.id.imgGallery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cameraId = CAMERA_BACK;
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        videoCaptureProgress = (ProgressBar) findViewById(R.id.videoprogress);



        btnCapture.setOnTouchListener(new View.OnTouchListener() {
            private long firstTouchTS = 0;
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    firstTouchTS = System.currentTimeMillis();
                    new CountDownTimer(300, 100) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {

                            if (event.getAction()!=MotionEvent.ACTION_UP){
                                isClicked = false;
                                videoCaptured = true;
                                toggleVideoIcons();
                                videoCaptureProgress.setVisibility(View.VISIBLE);
                                startRecordingVideo();
                            }


                        }

                    }.start();



                } else if(event.getAction()==MotionEvent.ACTION_UP) {

                    if (isClicked){
                        takePicture();
                    }
                    else {

                        videoCaptureProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(statusCamera.this, ((System.currentTimeMillis() - this.firstTouchTS) / 1000) + " seconds", Toast.LENGTH_LONG).show();
                        isClicked =true;
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

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
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

        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (cameraDevice!=null){


                final int actionMasked = motionEvent.getActionMasked();
                float maxzoom = (characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM))*10;

                Rect sensorArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                int action = motionEvent.getAction();
                float current_finger_spacing;

                if (motionEvent.getPointerCount() > 1) {
                    // Multi touch logic
                    current_finger_spacing = getFingerSpacing(motionEvent);
                    if(finger_spacing != 0){
                        if(current_finger_spacing > finger_spacing && maxzoom > zoom_level){
                            if (zoom_level<40){
                                zoom_level++;
                            }

                        } else if (current_finger_spacing < finger_spacing && zoom_level > 1){
                            zoom_level--;
                        }
                        int minW = (int) (sensorArraySize.width() / maxzoom);
                        int minH = (int) (sensorArraySize.height() / maxzoom);
                        int difW = sensorArraySize.width() - minW;
                        int difH = sensorArraySize.height() - minH;
                        int cropW = difW /100 *(int)zoom_level;
                        int cropH = difH /100 *(int)zoom_level;
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

                } else{
                    if (action == MotionEvent.ACTION_DOWN) {

                        final int halfTouchWidth  = 60; //(int)motionEvent.getTouchMajor(); //TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
                        final int halfTouchHeight = 60; //(int)motionEvent.getTouchMinor();
                        //TODO: here I just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
                        final int y = Math.max((int)motionEvent.getY() - halfTouchHeight, 0);
                        final int x = Math.max((int)motionEvent.getX() - halfTouchWidth ,  0);

                        String text = "You click at x = " + x + " and y = " + y ;


                        focusAreaTouch = new MeteringRectangle(x, y,
                                halfTouchWidth  * 2,
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


                return true;
            }
        });



    }

    private void toggleVideoIcons() {
        if (videoCaptured){
            btnRotate.setVisibility(View.INVISIBLE);
            btnFlash.setVisibility(View.INVISIBLE);
            imgGallery.setVisibility(View.INVISIBLE);
        }
        else {
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

    private boolean isMeteringAreaAFSupported()  {
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            return characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
        }
        catch (Exception e){
            return  false;
        }


    }

    private void startgalleryIntent() {

        Intent galIntent = new Intent(statusCamera.this, CustomPicker.class);

       // galIntent.putExtra("IMAGES_LIMIT", 1);
       // galIntent.putExtra("VIDEOS_LIMIT", 1);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(galIntent,options.toBundle());
        //startActivity(galIntent);

    }

    public void switchFlash() {
        try {
            if (cameraId.equals(CAMERA_BACK)) {
                if (isFlashSupported) {
                    if (isTorchOn) {
                        captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                        btnFlash.setBackground(ContextCompat.getDrawable(getApplicationContext() , R.drawable.vector_flash_off));
                        isTorchOn = false;
                    } else {
                        captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                        btnFlash.setBackground(ContextCompat.getDrawable(getApplicationContext() , R.drawable.vector_flash_on));
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
        if(null == cameraDevice) {
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
            final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
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
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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
        }
        else {
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

                    mIsRecordingVideo = true;
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
        if (mCurrentFile!=null){
            hideCameraButtons();


            closeCameraDevice();

            startVideoPreview();
        }
    }

    void closeCameraDevice() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }


    private void startVideoPreview() {

       //
       // textureView.setSurfaceTextureListener(new S);
        try {
        SurfaceTexture texture =  textureView.getSurfaceTexture();
        assert  texture != null;


        Surface surface = new Surface(texture);


        //ArrayList<GalleryVideo> image = new GalleryPicker(getApplicationContext()).getVideos();

            mMediaPlayer= new MediaPlayer();
            mMediaPlayer.setDataSource(mCurrentFile.getAbsolutePath());
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
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try{

            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert  texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);


            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(statusCamera.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            },null);
        } catch (CameraAccessException  e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if(cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);

        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
            if (imageCaptured){
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


        try{
           // cameraId = manager.getCameraIdList()[0];
            frontCamAvailable = manager.getCameraIdList()[1];
            if (frontCamAvailable!=null){
                btnRotate.setVisibility(View.VISIBLE);
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
            if (cameraId==CAMERA_FRONT){
                videoSize = map.getOutputSizes(MediaRecorder.class)[3];
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[3];
            }
            Toast.makeText(getApplicationContext(),String.valueOf(imageDimension),Toast.LENGTH_SHORT).show();
            //Check realtime permission if run higher API 23
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },REQUEST_CAMERA_PERMISSION);
                return;
            }
            mMediaRecorder = new MediaRecorder();
            manager.openCamera(cameraId,stateCallback,null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setupFlashButton() {
        if (cameraId.equals(CAMERA_BACK) && isFlashSupported) {
            btnFlash.setVisibility(View.VISIBLE);

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
            openCamera();

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

        if (imageCaptured || videoCaptured){
            showCameraButtons();

            videoCaptured = false;
            imageCaptured = false;
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            openCamera();

        }
        else {
            super.onBackPressed();
        }
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


}