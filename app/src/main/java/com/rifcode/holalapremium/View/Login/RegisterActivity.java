package com.rifcode.holalapremium.View.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Main.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView imgvRegisterUser;
    private TextView tvRegisterName,tvRegisterAge;
    private RadioButton rbRegisterMale,rbRegisterFemale;
    private TextView btnRegisterConfirm;
    private DataFire dataFire;
    private String photoUser;
    private SharedPreferences prefs;
    private ProgressBar pbRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dataFire=new DataFire();
        prefs = getSharedPreferences("photoUser", MODE_PRIVATE);
        photoUser = prefs.getString("photoUser", null);

        // images reviews
        String puchKey = dataFire.getdbRefImagesReviews().push().getKey();
        HashMap<String, String> imagesReviewsMap = new HashMap<>();
        imagesReviewsMap.put("userID", dataFire.getUserID());
        imagesReviewsMap.put("image", photoUser);
        imagesReviewsMap.put("type", "photo_profile");
        dataFire.getdbRefImagesReviews().child(puchKey).setValue(imagesReviewsMap);
        dataFire.getdbRefImagesReviews().child(puchKey).child("time").setValue(-1*(System.currentTimeMillis()/1000));

        wedgets();
        getData();
        btnRegisterConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbRegister.setVisibility(View.VISIBLE);
                signUpDetails();
            }
        });


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null
                                && deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby", false)) {
                            final String referrerUid = deepLink.getQueryParameter("invitedby");

                            dataFire.getDbref().child("Referrer").child(referrerUid).child(dataFire.getUserID()).setValue("installed");

                            dataFire.getDbRefUsers().child(referrerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String gems = String.valueOf(snapshot.child("gems").getValue());
                                    dataFire.getDbRefUsers().child(referrerUid).child("gems")
                                            .setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string.earn_10_invite)));

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        }
                    }
                });

    }

    private void signUpDetails(){

        HashMap<String, String> userMap = new HashMap<>();
        String deviceTokenID = FirebaseInstanceId.getInstance().getToken();

        //info user
        userMap.put("username", tvRegisterName.getText().toString());
        userMap.put("age",tvRegisterAge.getText().toString());
        userMap.put("gender",InfoRegisterActivity.gender);
        if(InfoRegisterActivity.gender.equals("guy"))
            userMap.put("genderMeet","girl");
        else
            userMap.put("genderMeet","guy");

        userMap.put("about", "---");
        userMap.put("purchase", "false");
        userMap.put("first_video_calls", "false");
        userMap.put("first_voice_calls", "false");
        userMap.put("first_friend_match", "false");
        userMap.put("notifi_on", "false");
        userMap.put("online","false");
        userMap.put("photoProfile", photoUser);
        userMap.put("discovery", "false");
        userMap.put("job", "---");
        userMap.put("school", "---");
        userMap.put("birthday",
                InfoRegisterActivity.day +"/"
                        + InfoRegisterActivity.month +"/"
                        + InfoRegisterActivity.year);
        ////---------------- for notification one to one ------------------//
        userMap.put("device_token", deviceTokenID);
        //
        if(WelcomeActivity.typeAccount!=null) {
            if (WelcomeActivity.typeAccount.equals("phone")) {
                userMap.put("numberPhone", PhoneActivity.phonenumber);
                userMap.put("codeCountryPhone", PhoneActivity.codecountry);
                userMap.put("type_account", "phone");

            } else {
                userMap.put("type_account", "facebook");
                userMap.put("numberPhone", "---");
                userMap.put("codeCountryPhone", "---");
                userMap.put("email", InfoRegisterActivity.emailfb);
            }
        }
        //gps use
        userMap.put("country", "---");
        userMap.put("country_code", "---");
        userMap.put("city", "---");

        //options meet for matches
        userMap.put("country_meet", "my_country");
        userMap.put("max_age_meet", "99");
        userMap.put("min_age_meet", "18");

        //app options
        userMap.put("language_app", Locale.getDefault().getLanguage());
        userMap.put("state_app", "start");
        userMap.put("rateApp","false");
        dataFire.getDbRefUsers().child(dataFire.getUserID()).setValue(userMap);

        //set free gems 10
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("gems").setValue(10);

        //5 images user vide

        //0
        //images user
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("0")
                .child("thumb_picture").setValue(photoUser);
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("0")
                .child("Time").setValue(-1*(System.currentTimeMillis()/1000));


        //1
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("1")
                .child("thumb_picture").setValue("none");
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("1")
                .child("Time").setValue(-1*(System.currentTimeMillis()/1000));

        //2
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("2")
                .child("thumb_picture").setValue("none");
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("2")
                .child("Time").setValue(-1*(System.currentTimeMillis()/1000));

        //3
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("3")
                .child("thumb_picture").setValue("none");
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("3")
                .child("Time").setValue(-1*(System.currentTimeMillis()/1000));

        //4
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("4")
                .child("thumb_picture").setValue("none");
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("4")
                .child("Time").setValue(-1*(System.currentTimeMillis()/1000));

        //5
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("5")
                .child("thumb_picture").setValue("none");
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("images").child("5")
                .child("Time").setValue(-1*(System.currentTimeMillis()/1000));


        //number user signup
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .child("number")
                .setValue(-1*(System.currentTimeMillis()/1000)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pbRegister.setVisibility(View.GONE);

                Intent intentman  = new Intent(RegisterActivity.this,MainActivity.class);
                intentman.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentman);
                Toast.makeText(RegisterActivity.this, getString(R.string.auth_success), Toast.LENGTH_SHORT).show();
            }
        });



    }



    private void getData(){

        tvRegisterAge.setText(InfoRegisterActivity.age);
        tvRegisterName.setText(InfoRegisterActivity.name);

        if(InfoRegisterActivity.gender.equals("guy")){
            rbRegisterMale.setChecked(true);
        }else{
            rbRegisterFemale.setChecked(true);
        }

        Picasso.get().load(photoUser).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.icon_register_select_header).into(imgvRegisterUser, new Callback() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError(Exception e) {
                Picasso.get().load(photoUser).placeholder(R.drawable.icon_register_select_header)
                        .into(imgvRegisterUser);
            }
        });

    }

    private void wedgets() {
        rbRegisterFemale=findViewById(R.id.rb_register_female);
        rbRegisterMale=findViewById(R.id.rb_register_male);
        tvRegisterName=findViewById(R.id.tvRegister_name);
        tvRegisterAge=findViewById(R.id.tvRegister_age);
        imgvRegisterUser=findViewById(R.id.imgvRegisterUser);
        pbRegister=findViewById(R.id.pbRegister);
        btnRegisterConfirm=findViewById(R.id.btn_register_confirm);
    }



}
