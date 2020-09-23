package com.rifcode.holalapremium.View.Chat;

import android.os.Bundle;
import android.view.View;
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

public class VideoCallAnswerActivity extends AppCompatActivity {

    private DataFire dataFire;
    private String userID;
    private ImageView imageUser,imgvDecline,imgvAccept;
    private TextView tvCountry,tvName;
    private CountryCodePicker ccpCountryReqCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_call);
        dataFire=new DataFire();
        widgets();

        userID = getIntent().getStringExtra("userIDvisited");
        dataFire.getDbRefUsers().child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String image = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                String age = String.valueOf(dataSnapshot.child("age").getValue());
                String username = String.valueOf(dataSnapshot.child("username").getValue());
                String sex = String.valueOf(dataSnapshot.child("gender").getValue());
                String country = String.valueOf(dataSnapshot.child("country").getValue());
                String country_code = String.valueOf(dataSnapshot.child("country_code").getValue());
                setImage(image,imageUser);
                tvName.setText(username+", "+age);
                tvCountry.setText(country);
                ccpCountryReqCall.setCountryForNameCode(country_code);

                if(sex.equals("guy")){
                    tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_common_male_selected, 0, 0, 0);
                }else{
                    tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_common_female_selected, 0, 0, 0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataFire.getDbRefVideoCall().child(dataFire.getUserID()).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        VideoCallAnswerActivity.this.finish();
                        String room_id =  String.valueOf(dataSnapshot.child("room_id").getValue());
                        dataFire.getDbRefVideoCall().child(userID).child(dataFire.getUserID()).child("room_id")
                                .setValue(room_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dataFire.getDbRefVideoCall().child(userID).child(dataFire.getUserID()).child("status_request").setValue("accept");
                                dataFire.getDbRefVideoCall().child(dataFire.getUserID()).child(userID).child("status_request").setValue("accept");
                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        imgvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataFire.getDbRefVideoCall().child(dataFire.getUserID()).child(userID).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                VideoCallAnswerActivity.this.finish();
                            }
                        });
            }
        });
    }

    public void setImage(String thumb_image, ImageView photocall){
        Picasso.get().load(thumb_image).placeholder(R.drawable.icon_register_select_header).into(photocall);
    }

    private void widgets() {
        imgvAccept = findViewById(R.id.video_answer_accept);
        imgvDecline = findViewById(R.id.video_answer_reject);
        tvCountry =  findViewById(R.id.tvCountryAnswerCall);
        tvName = findViewById(R.id.tvNameAgeSexAnswerCall);
        imageUser = findViewById(R.id.imgvPhotoUserAnswerCall);
        ccpCountryReqCall = findViewById(R.id.ccpCountryAnswerCall);
        ccpCountryReqCall.setEnabled(false);
    }
}
