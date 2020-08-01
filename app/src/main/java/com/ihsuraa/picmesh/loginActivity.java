package com.ihsuraa.picmesh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.ImageRequest;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;

public class loginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101 ;
    CardView card_fb, card_gmail ,card_phone , card_twit;
    CallbackManager callbackManager;
    FirebaseAuth  mFirebaseAuth;
    String profileImageUrl = null, fullname = null , userEmail = null;
    ArrayList<String> info;
    GoogleSignInClient googleSignInClient;
    TwitterAuthClient mTwitterAuthClient;
    SharedPreferences UserDetails;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setIds();
        info = new ArrayList<String>();
        profileImageUrl = fullname = userEmail = null;
        UserDetails = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);

        TwitterAuthConfig mTwitterAuthConfig = new TwitterAuthConfig(getString(R.string.twitter_app_id),
                getString(R.string.twitter_app_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(mTwitterAuthConfig)
                .build();
        Twitter.initialize(twitterConfig);
        mFirebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        cardlistener();

    }

    private void setIds() {
        card_fb = findViewById(R.id.card_fb);
        card_gmail =findViewById(R.id.card_gmail);
        card_phone = findViewById(R.id.card_phone);
        card_twit = findViewById(R.id.card_twit);
    }


    private void cardlistener() {

        card_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LoginManager.getInstance().logInWithReadPermissions(loginActivity.this, Arrays.asList( "email", "public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(final LoginResult loginResult) {

                                Profile profile = Profile.getCurrentProfile();
                                fullname = profile.getName();
                                profileImageUrl = profile.getProfilePictureUri(500,500).toString();
                                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                                firebaseLogin(credential);
                            }

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });


            }
        });
        card_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(loginActivity.this, gso);
                signInWithGoogle();

            }
        });
        card_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, PhoneLogin.class);
                startActivity(intent);
            }
        });
        card_twit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 mTwitterAuthClient = new TwitterAuthClient();
                mTwitterAuthClient.authorize(loginActivity.this, new Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(true, true, true).enqueue(new Callback<User>() {
                            @Override
                            public void success(Result<User> result) {

                                fullname = result.data.name;
                                userEmail = result.data.email;
                                profileImageUrl = result.data.profileImageUrlHttps.replace("_normal", "");
                            }

                            @Override
                            public void failure(TwitterException exception) {

                            }
                        });

                        AuthCredential credential = TwitterAuthProvider.getCredential(twitterSessionResult.data.getAuthToken().token,
                                twitterSessionResult.data.getAuthToken().secret);
                        firebaseLogin(credential);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                
            }
        });
    }



    private void firebaseLogin( AuthCredential credential){
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            upateActivity();

                        } else {

                            Toast.makeText(loginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
               fullname = account.getDisplayName();
               userEmail = account.getEmail();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseLogin(credential);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }else if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE){

            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void upateActivity(){
        Intent intent = new Intent(loginActivity.this, SetName.class);
        SharedPreferences.Editor editor = UserDetails.edit();
        editor.putString("fullName",fullname);
        editor.putString("userEmail",userEmail);
        editor.putString("propicUrl",profileImageUrl);
        editor.apply();
        startActivity(intent);
    }
}
