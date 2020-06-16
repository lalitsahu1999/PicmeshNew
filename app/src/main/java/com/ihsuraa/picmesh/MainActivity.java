package com.ihsuraa.picmesh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if(prefManager.isFirstTimeLaunch()){
            prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            finish();
        }
    }
    protected void btn_Click(View view){
        Toast.makeText(MainActivity.this, "clicked on button", Toast.LENGTH_LONG).show();
    }
}
