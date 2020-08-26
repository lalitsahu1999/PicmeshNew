package com.ihsuraa.picmesh;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    private PrefManager prefManager;
    String userId;
    ImageView splash;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splash = findViewById(R.id.splashImage);
        splash.setImageResource(R.drawable.gift);
        setStatusBarColor();
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user!=null){
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                checkForDetails();
            }
            else {
                startActivity(new Intent(SplashScreen.this,loginActivity.class));
                finish();
            }

        }
        else {
            launchSlider();
            finish();
        }
    }

    private void checkForDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    startActivity(new Intent(SplashScreen.this, statusCamera.class));
                }
                else {
                    startActivity(new Intent(SplashScreen.this,SetName.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void launchSlider() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(SplashScreen.this, WelcomeSlider.class));
        finish();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));
        }
    }
}
