package com.ihsuraa.picmesh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    Button button;
    SharedPreferences UserDetails;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        button = findViewById(R.id.logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser()!=null){

                    FirebaseAuth.getInstance().signOut();
                    UserDetails.edit().clear().apply();
                    startActivity(new Intent(Home.this, loginActivity.class));
                }
            }
        });
    }
}
