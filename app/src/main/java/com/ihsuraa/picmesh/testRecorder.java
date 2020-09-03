package com.ihsuraa.picmesh;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.picker.gallery.model.GalleryVideo;
import com.picker.gallery.model.interactor.GalleryPicker;

import java.io.IOException;
import java.util.ArrayList;

public class testRecorder extends AppCompatActivity {
    TextureView textureView;
    private MediaPlayer mMediaPlayer;
    ArrayList<GalleryVideo> image ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        textureView = (TextureView) findViewById(R.id.textureView2);
        image = new GalleryPicker(getApplicationContext()).getVideos();
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Surface s = new Surface(surface);

                Toast.makeText(getApplicationContext(),image.get(0).getDATA(),Toast.LENGTH_LONG).show();
                try {
                    mMediaPlayer= new MediaPlayer();
                    mMediaPlayer.setDataSource(image.get(0).getDATA());
                    mMediaPlayer.setSurface(s);
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
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer!=null){
            mMediaPlayer.start();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer!=null) {
            mMediaPlayer.pause();
        }
    }
}