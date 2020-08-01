package com.ihsuraa.picmesh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Complete_profile extends AppCompatActivity {
    ImageButton back, done, select_date, select_location;
    Calendar myCalendar;
    EditText username, birthday, current_location;
    LocationManager locationManager;
    LocationListener locationListener;
    Toolbar toolBar;
    SetName name;
    String userId,propicUrl;
    ImageButton rem_img1, rem_img2, rem_img3;
    ProgressBar progressBar;
    ImageView img1,img2,img3;
    private static final int PICK_FROM_GALLERY = 2;
    SharedPreferences settings,UserDetails;
    SharedPreferences.Editor editor;
    ArrayList<String> ImageList,dwnldUrl;
    int upload_count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        UserDetails = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        ImageList = new ArrayList<String>();
        dwnldUrl = new ArrayList<String>();
        setIds();
        setSupportActionBar(toolBar);
        setStatusBarColor();
        setupLocationListener();
        myCalendar = Calendar.getInstance();
         name = new SetName();
        settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        setValue();
       buttonListener();
    }

    @SuppressLint("SetTextI18n")
    private void setValue() {
       String uname = UserDetails.getString("fullName",null);
        Random random = new Random();
        int randomNumber = random.nextInt(99999 - 10000) + 10000;
        if (uname != null){
            String[] arr = uname.split(" ");

            username.setText(arr[0] + randomNumber);
        }

    }

    private void buttonListener() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Complete_profile.this, SetName.class));
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               checkForValidation();
            }
        });
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(Complete_profile.this,R.style.DatePickerDialogTheme, listener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),  myCalendar.get(Calendar.DAY_OF_MONTH));
                long max = 157852800000L;
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - max);
                dialog.show();

            }
        });

        select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Permissions.Check_FINE_LOCATION(Complete_profile.this)) {
                    Permissions.Request_FINE_LOCATION(Complete_profile.this, 22);
                } else {

                    //Toast.makeText(Complete_profile.this, "permisson granted", Toast.LENGTH_SHORT).show();
                    locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                    boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!enabled) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                    if (ActivityCompat.checkSelfPermission(Complete_profile.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Complete_profile.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
                    progressBar.setVisibility(View.VISIBLE);


                }
            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (name.checkForPermission(getApplicationContext(),Complete_profile.this)) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra("image","img1");
                    editor.putString("image", "img1");

                    editor.commit();
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                }
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (name.checkForPermission(getApplicationContext(),Complete_profile.this)) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra("image","img2");
                    editor.putString("image", "img2");

                    editor.commit();
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                }
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (name.checkForPermission(getApplicationContext(),Complete_profile.this)) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra("image","img3");
                    editor.putString("image", "img3");

                    editor.commit();
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                }



            }
        });
    }

    private void checkForValidation() {

        if (birthday.getText().length()==0){
            Toast.makeText(getApplicationContext(),"Please select your birthday",Toast.LENGTH_LONG).show();
        }
        else if (current_location.getText().length()==0){
            Toast.makeText(getApplicationContext(),"Please select your location",Toast.LENGTH_LONG).show();
        }
        else {
           disableButtons();
            uploadImages();
        }
    }

    private void disableButtons() {
        rem_img1.setEnabled(false);
        rem_img2.setEnabled(false);
        rem_img3.setEnabled(false);
    }
    private void enableButtons() {
        rem_img1.setEnabled(true);
        rem_img2.setEnabled(true);
        rem_img3.setEnabled(true);
    }

    private static boolean IsLocalPath(String p)
    {
        if (p.startsWith("https://"))
        {
            return false;
        }

        return true;
    }

    private void uploadImages() {
        progressBar.setVisibility(View.VISIBLE);
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("ProfileImages/"+ userId);

        propicUrl = UserDetails.getString("propicUrl",null);
        if (IsLocalPath(propicUrl)){
            ref.putFile(Uri.parse(propicUrl)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"propic uploaded",Toast.LENGTH_SHORT).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                            UserDetails.edit().putString("propicUrl",downloadUrl.toString()).apply();
                            uploadInfo();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    enableButtons();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else {
            uploadInfo();

        }

    }

    private void uploadInfo() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        final DatabaseReference locRef = FirebaseDatabase.getInstance().getReference("locations").child(current_location.getText().toString()).child(userId);

        PicmeshUser picmeshUser = new PicmeshUser(username.getText().toString(),UserDetails.getString("fullName",null),UserDetails.getString("contact",null),UserDetails.getString("userEmail",null),UserDetails.getString("gender",null),current_location.getText().toString(),birthday.getText().toString(),UserDetails.getString("propicUrl",null));
        myRef.setValue(picmeshUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(getApplicationContext(),"data saved",Toast.LENGTH_SHORT).show();
                PicmeshUsersLocation picmeshUsersLocations = new  PicmeshUsersLocation(UserDetails.getString("fullName",null),UserDetails.getString("propicUrl",null),String.valueOf( Calendar.getInstance().get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR)));

                locRef.setValue(picmeshUsersLocations).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getOptImages();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        enableButtons();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                enableButtons();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getOptImages() {

        if (!"default".equals(String.valueOf(img1.getTag()))){
           ImageList.add(String.valueOf(img1.getTag()));

        }
        if (!"default".equals(String.valueOf(img2.getTag()))){
            ImageList.add(String.valueOf(img2.getTag()));
        }
        if (!"default".equals(String.valueOf(img3.getTag()))){
            ImageList.add(String.valueOf(img3.getTag()));
        }

        uploadOptImages(ImageList);
    }

    private void uploadOptImages(final ArrayList<String> imageList) {

        if (imageList.size() > 0){
            Toast.makeText(getApplicationContext(),imageList.get(0),Toast.LENGTH_LONG).show();
            for (  upload_count = 0 ; upload_count<imageList.size(); upload_count++){

                Uri imgUri = Uri.parse(imageList.get(upload_count));
                final StorageReference reference = FirebaseStorage.getInstance().getReference("Posts").child(userId).child("Images" + imgUri.getLastPathSegment());
                reference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String imgUri = task.getResult().toString();
                                dwnldUrl.add(imgUri);

                                if (dwnldUrl.size()==imageList.size()){
                                    UpdateOptImageUrls();

                                }


                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });


            }
        }
        else {

            UpdateActivity();
            progressBar.setVisibility(View.INVISIBLE);

        }


    }

    private void UpdateOptImageUrls() {
        OptinalImages optinalImages = new OptinalImages(dwnldUrl);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("posts").child(userId);
        databaseReference.setValue(optinalImages).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);
                UpdateActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void UpdateActivity() {
        startActivity(new Intent(Complete_profile.this, Home.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                if (resultCode == Activity.RESULT_OK) {

                    Uri selectedImage = data.getData();
                    performCrop(selectedImage);

                }
                break;

            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    String selec = settings.getString("image",null);


                    switch (selec){
                        case "img1":
                            setOptImage(resultUri,img1 , rem_img1);

                         break;
                        case "img2":
                            setOptImage(resultUri,img2 , rem_img2);
                           break;
                        case "img3":
                            setOptImage(resultUri,img3 , rem_img3);
                            break;
                    }


                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
                break;

        }
    }

    private void setOptImage(Uri uri, final ImageView img, final ImageButton rem_img) {
        img.setImageURI(uri);
        img.setTag(uri);
        rem_img.setVisibility(View.VISIBLE);
        rem_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rem_img.setVisibility(View.INVISIBLE);
                img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.select_img));
                img.setTag("default");
            }
        });
    }

    private void performCrop(Uri picUri) {
        Uri destinationUri = Uri.fromFile(new File(getApplicationContext().getCacheDir(), "IMG_" + System.currentTimeMillis()));
        UCrop.of(picUri,destinationUri)
                .withAspectRatio(1,1)
                .withMaxResultSize(1200,628)
                .start(Complete_profile.this);

    }

    private void setupLocationListener() {
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                try {

                    Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.isEmpty()) {

                    }
                    else {
                        if (addresses.size() > 0) {
                            current_location.setText(addresses.get(0).getLocality());
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));
        }
    }

    private void setIds() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        back = toolBar.findViewById(R.id.back);
        done = toolBar.findViewById(R.id.done);
        username = findViewById(R.id.username);
        select_location = findViewById(R.id.select_location);
        birthday = findViewById(R.id.birthday);
        select_date = findViewById(R.id.select_date);
        current_location = findViewById(R.id.location);
        progressBar = findViewById(R.id.progressBar);
        img1 = findViewById(R.id.opt_img1);
        img2 = findViewById(R.id.opt_img2);
        img3 = findViewById(R.id.opt_img3);
        rem_img1 = findViewById(R.id.rem_img1);
        rem_img2 = findViewById(R.id.rem_img2);
        rem_img3 = findViewById(R.id.rem_img3);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthday.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
    }
}
