package com.ihsuraa.picmesh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditStatus extends AppCompatActivity{
    ImageView image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);
        image = (ImageView) findViewById(R.id.editImage);
        Intent i = getIntent();
        if (i.getExtras().containsKey("imageuri")){
            Uri uri = Uri.parse(i.getStringExtra("imageuri"));
            Toast.makeText(getApplicationContext(),String.valueOf(uri),Toast.LENGTH_SHORT).show();
            image.setImageURI(uri);
        }
    }
}
