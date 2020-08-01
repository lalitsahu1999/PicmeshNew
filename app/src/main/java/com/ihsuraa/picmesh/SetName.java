package com.ihsuraa.picmesh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetName extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;
    Button next;
    String gender = null;
    EditText fname;
    CircleImageView dp;
    FirebaseUser user;
    ArrayList<String> info;
    CircleImageView propic;
    GoogleSignInClient googleSignInClient;
    public static final int MULTIPLE_PERMISSIONS = 11;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    final int PIC_CROP = 3;
    public String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    Uri imageURI;
    File photoFile;
    SharedPreferences UserDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);
        setStatusBarColor();
        UserDetails = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        setIds();
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(SetName.this, gso);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio0:
                        gender = "male";
                        break;
                    case R.id.radio1:
                        gender = "female";
                        break;

                }
            }
        });

        setValue();
        buttonListen();

    }

    private void buttonListen() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(),"Please select gender",Toast.LENGTH_SHORT).show();
                }
                else if (dp.getTag().equals("default")){
                    Toast.makeText(getApplicationContext(),"Please select picture",Toast.LENGTH_SHORT).show();
                }
                else {

                    LoginManager.getInstance().logOut();
                   googleSignInClient.signOut();


                   UserDetails.edit().putString("fullName",fname.getText().toString()).apply();

                    Intent intent = new Intent(SetName.this,Complete_profile.class);
                    UserDetails.edit().putString("gender", gender).apply();
                    startActivity(intent);
                }



            }
        });


        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
                TextView camera = alertLayout.findViewById(R.id.camera);
                TextView gallery = alertLayout.findViewById(R.id.gallery);

                AlertDialog.Builder builder1 = new AlertDialog.Builder(SetName.this);
                builder1.setCancelable(true);
                builder1.setView(alertLayout);
                final AlertDialog alert11 = builder1.create();
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Toast.makeText(getApplicationContext(),"Camera",Toast.LENGTH_SHORT).show();
                        alert11.hide();
                        if (checkForPermission(getApplicationContext(),SetName.this)) {
                            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {

                            }
                            if (photoFile != null) {
                                imageURI = FileProvider.getUriForFile(SetName.this, "com.ihsuraa.picmesh.provider", photoFile);

                             captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                             startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                            }
                        }

                    }
                });
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getApplicationContext(),"Gallery",Toast.LENGTH_SHORT).show();
                        alert11.hide();
                        if (checkForPermission(getApplicationContext(),SetName.this)) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                        }
                    }
                });

                alert11.show();
            }
        });




    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageURI = Uri.parse(image.getAbsolutePath());
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {

                    if(imageURI!= null){
                       //dp.setImageURI(imageURI);
                        performCrop(imageURI);

                    }

                }
                break;



            case PICK_FROM_GALLERY:

                if (resultCode == Activity.RESULT_OK) {
                    //pick image from gallery
                    Uri selectedImage = data.getData();

                    //dp.setImageURI(selectedImage);
                    performCrop(selectedImage);

                }
                break;

            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    UserDetails.edit().putString("propicUrl" , resultUri.toString()).apply();
                    dp.setImageURI(resultUri);
                    dp.setTag("custom");

                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
                break;
        }
    }


    private void setIds() {
        radioGroup = findViewById(R.id.radioGroup);
        dp = findViewById(R.id.pro_pic);
        fname = findViewById(R.id.fname);
        next = findViewById(R.id.next);
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));
        }
    }

    private void setValue() {
        String picUrl = UserDetails.getString("propicUrl",null);
        String fullName =  UserDetails.getString("fullName",null);


        if (picUrl!= null){
            Picasso.get().load(picUrl).into(dp);
            dp.setTag("custom");
        }
        if (fullName!=null){
            fname.setText(fullName);
        }



    }



    public boolean checkForPermission(Context context,Activity activity){
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.


                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }   // permissions list of don't granted permission
                }
                return;
            }
        }
    }

    private void performCrop(Uri picUri) {
        Uri destinationUri = Uri.fromFile(new File(getApplicationContext().getCacheDir(), "IMG_" + System.currentTimeMillis()));
        UCrop.of(picUri,destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(300, 300)
                .start(SetName.this);

    }


}
