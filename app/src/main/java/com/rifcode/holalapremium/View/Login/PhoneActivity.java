package com.rifcode.holalapremium.View.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {

    private ImageView ivBackLeftPhone;
    private EditText edtPhonenumber;
    private TextView tvSendSMSCode;
    private CountryCodePicker ccpPhoneNumber;
    public static String phonenumber,codecountry,codeNamecountry;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private DataFire dataFire;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        dataFire=new DataFire();
        wedgets();
        ivBackLeftPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backwelcome = new Intent(PhoneActivity.this,WelcomeActivity.class);
                startActivity(backwelcome);
                Animatoo.animateSlideRight(PhoneActivity.this);
            }
        });

        edtPhonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>=8){
                    tvSendSMSCode.setAlpha(1);
                }else{
                    tvSendSMSCode.setAlpha(0.5f);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneActivity.this, R.string.veif_fealed_phone, Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                                pd.dismiss();
                                Intent phoneLogin = new Intent(PhoneActivity.this,VerifyPhoneActivity.class);
                                phoneLogin.putExtra("AuthCredentials", s);
                                startActivity(phoneLogin);
                                Animatoo.animateSlideLeft(PhoneActivity.this);

            }
        };

        tvSendSMSCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvSendSMSCode.getAlpha()==1){
                    if(!TextUtils.isEmpty(edtPhonenumber.getText())){

                        pd.show();
                        phonenumber= ccpPhoneNumber.getSelectedCountryCodeWithPlus()+edtPhonenumber.getText().toString();
                        codecountry=ccpPhoneNumber.getSelectedCountryCode().toUpperCase();
                        codeNamecountry=ccpPhoneNumber.getSelectedCountryCode().toUpperCase();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phonenumber,
                                60,
                                TimeUnit.SECONDS,
                                PhoneActivity.this,
                                mCallbacks
                        );


                    }else{
                        Toast.makeText(PhoneActivity.this, R.string.plnmpho, Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }
        });

    }

    private void wedgets() {
        ivBackLeftPhone=findViewById(R.id.iv_back_left_phone);
        edtPhonenumber=findViewById(R.id.edtPhonenumber);
        tvSendSMSCode=findViewById(R.id.tvSendSMSCode);
        ccpPhoneNumber=findViewById(R.id.ccpPhoneNumber);
        pd = new ProgressDialog(PhoneActivity.this);
        pd.setMessage(getString(R.string.lod_phone));
        pd.setCancelable(false);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        dataFire.getmAuth().signInWithCredential(credential)
                .addOnCompleteListener(PhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // ...
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneActivity.this, R.string.very_otp, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(PhoneActivity.this);
    }

}
