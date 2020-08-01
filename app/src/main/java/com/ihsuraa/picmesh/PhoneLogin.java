package com.ihsuraa.picmesh;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {
    CountryCodePicker ccp;
    String selected_country_code = "+91";
    EditText numb, otp;
    Button getCode;
    ImageView phn;
    String mVerificationId;
    ProgressBar horizontalProgressBar;
    FirebaseAuth mFirebaseAuth;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    TextView timer;
    String yourNumb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        ccp = findViewById(R.id.ccp);
        numb = findViewById(R.id.phone);
        getCode = findViewById(R.id.numbSubmit);
        otp = findViewById(R.id.otp);
        phn = findViewById(R.id.phn_logo);
        mFirebaseAuth = FirebaseAuth.getInstance();
        timer =findViewById(R.id.timer);
        horizontalProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        numb.requestFocus();
        setStatusBarColor();

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();
                Toast.makeText(getApplicationContext(),selected_country_code,Toast.LENGTH_SHORT).show();
            }
        });

        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numb.getText().length() == 0){
                    numb.setError("fill number");
                    numb.requestFocus();
                }
                else if (numb.getText().length()>0 && phn.getVisibility() == View.INVISIBLE){
                     yourNumb = selected_country_code + numb.getText().toString();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PhoneLogin.this);
                    builder1.setTitle("Confirm sending otp to:");
                    builder1.setMessage(yourNumb);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    horizontalProgressBar.setVisibility(View.VISIBLE);


                                    sendVerificationCode(yourNumb);
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else if (otp.getText().length()>0){
                    horizontalProgressBar.setVisibility(View.VISIBLE);
                    verifyVerificationCode(otp.getText().toString());

                }
            }
        });
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));
        }
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                 mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                otp.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            horizontalProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            Toast.makeText(getApplicationContext(), "code sent", Toast.LENGTH_SHORT).show();
            startTimer(timer);
            phn.setVisibility(View.VISIBLE);
            otp.setVisibility(View.VISIBLE);
            timer.setVisibility(View.VISIBLE);
            getCode.setText(R.string.phone_login_submit_button);
            horizontalProgressBar.setVisibility(View.INVISIBLE);
            mResendToken = forceResendingToken;
        }
    };

    private void startTimer(final TextView timer) {

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer.getText().equals("Resend")){
                    horizontalProgressBar.setVisibility(View.VISIBLE);
                    resendVerificationCode(yourNumb,mResendToken);
                }
            }
        });

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText( String.valueOf(millisUntilFinished / 1000) );

            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                timer.setText("Resend");
            }

        }.start();


    }

    private void verifyVerificationCode(String otp) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            horizontalProgressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(PhoneLogin.this, SetName.class);
                            SharedPreferences UserDetails = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                            UserDetails.edit().putString("contact", yourNumb).apply();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            //verification unsuccessful.. display an error message
                            horizontalProgressBar.setVisibility(View.INVISIBLE);
                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }
}
