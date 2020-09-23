package com.rifcode.holalapremium.View.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Adapter.ArrayAdapterMatches;
import com.rifcode.holalapremium.Models.CardMatches;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.Models.Images;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Chat.ChatActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    private DataFire dataFire;
    private CardMatches item;
    private ArrayList<Images> arrayListImages;
    private CardStackView cardStackViewUserprofile;
    private ArrayAdapterMatches arrAdpMatches;
    private ArrayList<CardMatches> rowItemsMatches;
    public static CardStackLayoutManager manager;
    private TextView tvSendMsgUserProfile,tvNameUserProfile;
    private String userID;
    private ImageView imgvPhotoUserProfile;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        dataFire=new DataFire();
        arrayListImages=new ArrayList<>();
        widgets();


        userID = getIntent().getStringExtra("userIDvisited");

        tvSendMsgUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ChatActivity.chatActivity!=null && ChatActivity.chatActivity.equals("true")){
                    finish();
                }else {
                    Intent interntChat  = new Intent(UserProfileActivity.this, ChatActivity.class);
                    interntChat.putExtra("userIDvisited",userID);
                    startActivity(interntChat);
                }

            }
        });

        rowItemsMatches = new ArrayList<CardMatches>();
        arrAdpMatches = new ArrayAdapterMatches(UserProfileActivity.this, item,rowItemsMatches);
        cardStackViewUserprofile.setAdapter(arrAdpMatches);
        manager = new CardStackLayoutManager( this);
        cardStackViewUserprofile.setLayoutManager(manager);

        manager.setCanScrollHorizontal(false);
        manager.setCanScrollVertical(false);


        dataFire.getDbRefUsers().child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userUsername = dataSnapshot.child("username").getValue().toString();
                final String photoProfile = dataSnapshot.child("photoProfile").getValue().toString();
                String school = dataSnapshot.child("school").getValue().toString();
                String job = dataSnapshot.child("job").getValue().toString();
                String userGender = dataSnapshot.child("gender").getValue().toString();
                String userAge = dataSnapshot.child("age").getValue().toString();
                String about = dataSnapshot.child("about").getValue().toString();

                tvNameUserProfile.setText(userUsername);
                Picasso.get().load(photoProfile).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.icon_register_select_header).into(imgvPhotoUserProfile, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(photoProfile).placeholder(R.drawable.icon_register_select_header)
                                .into(imgvPhotoUserProfile);
                    }
                });

                for (DataSnapshot serieSnapshot : dataSnapshot.child("images").getChildren()) {
                    Images images = serieSnapshot.getValue(Images.class);
                    Images img = new Images();
                    if(!images.getThumb_picture().equals("none")) {
                        img.setThumb_picture(images.getThumb_picture());
                        arrayListImages.add(img);
                    }

                }

                if(dataSnapshot.hasChild("city") && dataSnapshot.hasChild("country")){
                    String city = dataSnapshot.child("city").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();

                    item  = new CardMatches(dataSnapshot.getKey(),userUsername,userAge,userGender,job,school,arrayListImages,about,city,country);
                    rowItemsMatches.add(item);
                    arrAdpMatches.notifyDataSetChanged();
                }else{
                    item  = new CardMatches(dataSnapshot.getKey(),userUsername,userAge,userGender,job,school,arrayListImages,about,"--- ","---");
                    rowItemsMatches.add(item);
                    arrAdpMatches.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void widgets() {
        cardStackViewUserprofile=findViewById(R.id.card_stack_view_userprofile);
        tvSendMsgUserProfile=findViewById(R.id.tvSendMsgUserProfile);
        tvNameUserProfile=findViewById(R.id.tvNameUserProfile);
        imgvPhotoUserProfile=findViewById(R.id.imgvPhotoUserProfile);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
