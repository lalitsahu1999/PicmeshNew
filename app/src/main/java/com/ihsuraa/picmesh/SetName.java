package com.ihsuraa.picmesh;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetName extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;
    Button next;
    String gender = null;
    EditText fname;
    CircleImageView dp;
    FirebaseUser user;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));
        }
        radioGroup = findViewById(R.id.radioGroup);
        dp = findViewById(R.id.pro_pic);
        fname = findViewById(R.id.fname);
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
        next = findViewById(R.id.next);
        setValue();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(),"Please select gender",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),gender,Toast.LENGTH_SHORT).show();
                }
                LoginManager.getInstance().logOut();
                googleSignInClient.signOut();
                if (user!=null){
                    FirebaseAuth.getInstance().signOut();
                }

                Intent intent = new Intent(SetName.this,loginActivity.class);
                startActivity(intent);


            }
        });
    }

    private void setValue() {
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<String> info = getIntent().getStringArrayListExtra("Info");

        if (!(info.get(2) == null)){
            Picasso.get().load(info.get(2)).into(dp);
            //Toast.makeText(getApplicationContext(),info.get(2),Toast.LENGTH_LONG).show();
        }
        if (!(info.get(0) == null)){
            fname.setText(info.get(0));
        }
        //Toast.makeText(getApplicationContext(),info.get(2),Toast.LENGTH_SHORT).show();
    }
}
