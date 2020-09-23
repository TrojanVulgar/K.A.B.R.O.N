package com.rifcode.holalapremium.View.Random;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.squareup.picasso.Picasso;

public class RequestCallActivity extends AppCompatActivity {

    private ImageView imageuser,imgvTypeCall;
    private Button imgvAccept,imgvDecline;
    private TextView tvcountry,tvname;
    private String userid;
    private TextView tvcounter10secRequesCall;
    private DataFire dataFire;
    private CountryCodePicker ccpCountryReqCall;
    private boolean isAccept=false;
    private CountDownTimer cdt;
    private TextView tvWaitAccept;
    private String TAG="activity_request_call";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_call);
        dataFire =new DataFire();
        widgets();
        userid = getIntent().getStringExtra("userIDvisited");

        imgvAccept.setVisibility(View.VISIBLE);
        imgvDecline.setVisibility(View.VISIBLE);

        try {
            if (SearchRandomActivity.strcheckOption != null) {
                if (SearchRandomActivity.strcheckOption.equals("voice")) {
                    imgvTypeCall.setImageDrawable(getDrawable(R.drawable.ic_voice_red));
                } else {
                    imgvTypeCall.setImageDrawable(getDrawable(R.drawable.ic_videocam_red));
                }
            }
        }catch(Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());

        }

        dataFire.getDbRefUsers().child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String image = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                String age = String.valueOf(dataSnapshot.child("age").getValue());
                String username = String.valueOf(dataSnapshot.child("username").getValue());
                String sex = String.valueOf(dataSnapshot.child("gender").getValue());
                String country = String.valueOf(dataSnapshot.child("country").getValue());
                String country_code = String.valueOf(dataSnapshot.child("country_code").getValue());
                setImage(image,imageuser);
                tvname.setText(username+", "+age);
                tvcountry.setText(country);
                ccpCountryReqCall.setCountryForNameCode(country_code);

                if(sex.equals("guy")){
                    tvname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_register_male_unselected, 0, 0, 0);
                }else{
                    tvname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_register_female_unselected, 0, 0, 0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

      cdt=  new CountDownTimer(15000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                tvcounter10secRequesCall.setText(String.valueOf(millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                finish();
            }

        }.start();

        imgvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdt.start();

                isAccept=true;
                dataFire.getDbRefSearch().child(userid).child("status_request_call").setValue("accept");
                dataFire.getDbRefSearch().child(userid).child("request_iduser").setValue(dataFire.getUserID());

                imgvAccept.setVisibility(View.GONE);
                imgvDecline.setVisibility(View.GONE);
                tvWaitAccept.setVisibility(View.VISIBLE);

                dataFire.getDbRefSearch().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("status_request_call")) {
                            String status_request_call = dataSnapshot.child("status_request_call").getValue().toString();
                            if(status_request_call.equals("accept")){

                                String roomid = String.valueOf(dataSnapshot.child("room_id").getValue());
                                String type_call = String.valueOf(dataSnapshot.child("type_call").getValue());

                                if(type_call.equals("voice")){
                                    setupCallVoice(roomid);
                                }else
                                if(type_call.equals("video")){
                                    setupCallVideo(roomid);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        imgvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdt.cancel();
                dataFire.getDbRefSearch().child(dataFire.getUserID()).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        gotoSearchRandom();
                    }
                });
            }
        });

    }

    private void widgets() {
        imgvAccept = findViewById(R.id.btnAcceptCall);
        imgvDecline = findViewById(R.id.btnNextCall);
        tvcountry =  findViewById(R.id.tvCountryRequesCall);
        tvname = findViewById(R.id.tvNameAgeSexRequesCall);
        imageuser = findViewById(R.id.imgvPhotoUserRandom);
        tvcounter10secRequesCall = findViewById(R.id.tvcounter10secRequesCall);
        ccpCountryReqCall = findViewById(R.id.ccpCountryReqCall);
        ccpCountryReqCall.setEnabled(false);
        ccpCountryReqCall.setCcpClickable(false);

        tvWaitAccept = findViewById(R.id.tvWaitAccept);
        imgvTypeCall = findViewById(R.id.imgvTypeCall);
    }

    public void setImage(String thumb_image, ImageView photocall){
        Picasso.get().load(thumb_image).placeholder(R.drawable.icon_register_select_header).into(photocall);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dataFire.getDbRefSearch().child(dataFire.getUserID()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                gotoSearchRandom();
            }
        });
    }

    private void gotoSearchRandom(){
        Intent intent  = new Intent(RequestCallActivity.this,SearchRandomActivity.class);
        // need to set your Intent View here
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        dataFire.getDbRefSearch().child(dataFire.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("chatnow")) {

                    String chatnow = dataSnapshot.child("chatnow").getValue().toString();
                    String type_call = dataSnapshot.child("type_call").getValue().toString();

                    if(dataSnapshot.hasChild("voice_room_id")) {

                        if (type_call.equals("voice")) {
                            String voice_room_id = dataSnapshot.child("voice_room_id").getValue().toString();
                            Intent chatTextActIntent = new Intent(RequestCallActivity.this, VoiceCallActivity.class);
                            chatTextActIntent.putExtra("userIDvisited", chatnow);
                            chatTextActIntent.putExtra("voice_room_id", voice_room_id);
                            startActivity(chatTextActIntent);

                        }
                    }
                    else
                    if(dataSnapshot.hasChild("video_room_id")) {

                        if (type_call.equals("video")) {
                            String video_room_id = dataSnapshot.child("video_room_id").getValue().toString();
                            Intent chatTextActIntent = new Intent(RequestCallActivity.this, VideoCallActivity.class);
                            chatTextActIntent.putExtra("userIDvisited", chatnow);
                            chatTextActIntent.putExtra("video_room_id", video_room_id);
                            startActivity(chatTextActIntent);

                        }

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupCallVoice(final String roomid){
        isAccept=true;
        Intent chatTextActIntent = new Intent(RequestCallActivity.this, VoiceCallActivity.class);
        chatTextActIntent.putExtra("userIDvisited", userid);
        chatTextActIntent.putExtra("voice_room_id", roomid);
        startActivity(chatTextActIntent);
       

        dataFire.getDbRefSearch().child(userid).child("chatnow").setValue(dataFire.getUserID())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataFire.getDbRefSearch().child(userid).child("voice_room_id").setValue(roomid);
                dataFire.getDbRefSearch().child(dataFire.getUserID()).child("voice_room_id").setValue(roomid);
                dataFire.getDbRefSearch().child(dataFire.getUserID()).child("chatnow").setValue(userid);

            }
        });

    }

    private void setupCallVideo(final String roomid){
        isAccept=true;

        Intent chatTextActIntent = new Intent(RequestCallActivity.this, VideoCallActivity.class);
        chatTextActIntent.putExtra("userIDvisited", userid);
        chatTextActIntent.putExtra("video_room_id", roomid);
        startActivity(chatTextActIntent);

        dataFire.getDbRefSearch().child(userid).child("chatnow").setValue(dataFire.getUserID())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataFire.getDbRefSearch().child(userid).child("video_room_id").setValue(roomid);
                        dataFire.getDbRefSearch().child(dataFire.getUserID()).child("video_room_id").setValue(roomid);
                        dataFire.getDbRefSearch().child(dataFire.getUserID()).child("chatnow").setValue(userid);

                    }
                });

    }



}