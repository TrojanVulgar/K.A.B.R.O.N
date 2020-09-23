package com.rifcode.holalapremium.View.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Main.MainActivity;

import java.util.Arrays;

public class WelcomeActivity extends AppCompatActivity {

    private LinearLayout llLoginFacebook,llLoginAccountKit;
    private TextView tvWelcomePrivacyPolicy,tvWelcomeTermsOFServices;
    public static String typeAccount;
    private CallbackManager mCallbackManager;
    private DataFire dataFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        dataFire=new DataFire();
        wedgets();

        // facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        handleFacebookAccessToken(loginResult.getAccessToken(),loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(WelcomeActivity.this, getString(R.string.lcancl), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.w("FacebookException:", exception.getMessage());
                        Toast.makeText(WelcomeActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        llLoginAccountKit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeAccount="phone";
                llLoginAccountKit.setSelected(true);
                Intent phone = new Intent(WelcomeActivity.this,PhoneActivity.class);
                startActivity(phone);
                Animatoo.animateSlideLeft(WelcomeActivity.this);
            }
        });

        llLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeAccount="facebook";
                LoginManager.getInstance().logInWithReadPermissions(WelcomeActivity.this
                        , Arrays.asList("public_profile","email"));
            }
        });

        tvWelcomePrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PrivacyPolicy = new Intent(WelcomeActivity.this,PrivacyPolicyActivity.class);
                startActivity(PrivacyPolicy);
                Animatoo.animateSlideLeft(WelcomeActivity.this);
            }
        });

        tvWelcomeTermsOFServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TermsOfService = new Intent(WelcomeActivity.this,TermsOfServiceActivity.class);
                startActivity(TermsOfService);
                Animatoo.animateSlideLeft(WelcomeActivity.this);
            }
        });





    }

    private void wedgets() {
       //Linearlayout
        llLoginFacebook=findViewById(R.id.linearlayout_login_facebook);
        llLoginAccountKit=findViewById(R.id.linearlayout_login_accountkit);

        //Textview
        tvWelcomeTermsOFServices=findViewById(R.id.tvWelcomeTermsOFServices);
        tvWelcomePrivacyPolicy=findViewById(R.id.tvWelcomePrivacyPolicy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        llLoginAccountKit.setSelected(false);
    }


    private void handleFacebookAccessToken(AccessToken token, final LoginResult result) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        dataFire.getmAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            dataFire.getDbRefUsers().child(dataFire.getUserID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Intent continueintent = new Intent(WelcomeActivity.this, MainActivity.class);
                                        continueintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(continueintent);
                                    }else{
                                        Intent continueintent = new Intent(WelcomeActivity.this, InfoRegisterActivity.class);
                                        startActivity(continueintent);
                                        Animatoo.animateSlideLeft(WelcomeActivity.this);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(WelcomeActivity.this, getString(R.string.dbauthf),
                                    Toast.LENGTH_SHORT).show();
                        }
                        //...
                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //facebook  login
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
