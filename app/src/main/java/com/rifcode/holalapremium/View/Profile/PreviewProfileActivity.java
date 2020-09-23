package com.rifcode.holalapremium.View.Profile;

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
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.ArrayList;

public class PreviewProfileActivity extends AppCompatActivity implements
        CardStackListener {

    private DataFire dataFire;
    private CardMatches item;
    private ArrayList<Images> arrayListImages;
    private CardStackView cardStackView;
    private ArrayAdapterMatches arrAdpMatches;
    private ArrayList<CardMatches> rowItemsMatches;
    public static CardStackLayoutManager manager;
    private TextView tvPreviewGoEditProfile;
    private ImageView imgvPreviewProfileBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_profile);
        dataFire=new DataFire();
        arrayListImages=new ArrayList<>();

        widgets();

        tvPreviewGoEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreviewProfileActivity.this,EditProfileActivity.class));
            }
        });

        imgvPreviewProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rowItemsMatches = new ArrayList<CardMatches>();
        arrAdpMatches = new ArrayAdapterMatches(PreviewProfileActivity.this, item,rowItemsMatches);
        cardStackView.setAdapter(arrAdpMatches);
        manager = new CardStackLayoutManager( this);
        cardStackView.setLayoutManager(manager);

            manager.setCanScrollHorizontal(false);
            manager.setCanScrollVertical(false);



    }

    private void widgets() {
        cardStackView=findViewById(R.id.card_stack_view);
        tvPreviewGoEditProfile=findViewById(R.id.tv_preview_go_edit_profile);
        imgvPreviewProfileBack=findViewById(R.id.imgvPreviewProfileBack);
    }


    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {

    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (arrayListImages!=null && arrayListImages.size()>0)
        arrayListImages.clear();
        if(rowItemsMatches!=null && rowItemsMatches.size()>0)
        rowItemsMatches.clear();


        dataFire.getDbRefUsers().child(dataFire.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String userUsername = dataSnapshot.child("username").getValue().toString();
                String school = dataSnapshot.child("school").getValue().toString();
                String job = dataSnapshot.child("job").getValue().toString();
                String userGender = dataSnapshot.child("gender").getValue().toString();
                String userAge = dataSnapshot.child("age").getValue().toString();
                String about = dataSnapshot.child("about").getValue().toString();

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
}
