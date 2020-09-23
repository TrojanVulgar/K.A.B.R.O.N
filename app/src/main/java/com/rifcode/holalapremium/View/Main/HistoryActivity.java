package com.rifcode.holalapremium.View.Main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Adapter.ArrayAdapterMatches;
import com.rifcode.holalapremium.Models.CardMatches;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.Models.Images;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.DialogPurchase;
import com.rifcode.holalapremium.View.Chat.ChatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {


    private DataFire dataFire;
    private ImageView imgvRecentlySearchBack;
    private LinearLayoutManager mLayoutManagermax;
    private RecyclerView rcvRecentHorizontalmax;
    private ArrayAdapterMatches arrAdpMatches;
    private CardMatches item;
    private ArrayList<CardMatches> rowItemsMatches;
    private ArrayList<Images> arrayListImages;
    private TextView tvSendCommonMsg,tvCoinsSendMsg;
    private String userIdSelected;
    private String TAG="activity_history";
    private AdView adViewBanner;
    private LinearLayout adContainer;
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dataFire=new DataFire();
        widgets();
        rowItemsMatches = new ArrayList<CardMatches>();
        arrAdpMatches = new ArrayAdapterMatches(HistoryActivity.this, item,rowItemsMatches);
        rcvRecentHorizontalmax.setAdapter(arrAdpMatches);

        getRecentlySearchUsers();


        bp = new BillingProcessor(this, getString(R.string.LICENSE_KEY), getString(R.string.MERCHANT_KEY)
                , new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull final String productId, @Nullable TransactionDetails details) {

                if(bp.isPurchased(productId)){

                    showToast(getString(R.string.purch_secc));

                    dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("gems")) {
                                String gems = String.valueOf(dataSnapshot.child("gems").getValue());
                                // purchase gems //
                                if(productId.equals(getString(R.string.PRODUCT_ID_100_GEMS))){
                                    dataFire.getDbRefUsers().child(dataFire.getUserID())
                                            .child("gems").setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string._100_gems)));
                                }
                                if(productId.equals(getString(R.string.PRODUCT_ID_500_GEMS))){
                                    dataFire.getDbRefUsers().child(dataFire.getUserID())
                                            .child("gems").setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string._500_gems)));
                                }
                                if(productId.equals(getString(R.string.PRODUCT_ID_1000_GEMS))){
                                    dataFire.getDbRefUsers().child(dataFire.getUserID())
                                            .child("gems").setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string._1000_gems)));
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                if(errorCode==1){
                    showToast(getString(R.string.bilical));
                }
            }
            @Override
            public void onBillingInitialized() {
                //showToast("onBillingInitialized");
            }
            @Override
            public void onPurchaseHistoryRestored() {

            }
        });


        imgvRecentlySearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rcvRecentHorizontalmax.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    int position = getCurrentItem();

                    try {
                            if (position >= 0) {
                                userIdSelected = rowItemsMatches.get(position).getUserId();
                            }

                    }catch(Exception e){
                        Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                    }

                }
            }
        });

        tvSendCommonMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataFire.getDbRefUsers().child(dataFire.getUserID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.hasChild("purchase") && snapshot.hasChild("gems")) {

                                    String purchase = String.valueOf(snapshot.child("purchase").getValue());
                                    String gems = String.valueOf(snapshot.child("gems").getValue());

                                    if (purchase.equals("true") || Integer.parseInt(gems)>=Integer.parseInt(getString(R.string._3_gems_send_msg))) {

                                        if(userIdSelected==null){
                                            userIdSelected =  rowItemsMatches.get(0).getUserId();
                                        }
                                        Intent chatiintent = new Intent(HistoryActivity.this,ChatActivity.class);
                                        chatiintent.putExtra("userIDvisited",userIdSelected);
                                        startActivity(chatiintent);

                                        dataFire.getDbRefUsers()
                                                .child(dataFire.getUserID())
                                                .child("gems")
                                                .setValue(Integer.parseInt(gems)-Integer.parseInt(getString(R.string._3_gems_send_msg)));
                                    }else {
                                        DialogPurchase.dialog_purchase(dataFire.getmAuth(),bp,HistoryActivity.this);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }
        });
    }

    private int getCurrentItem(){
        return ((LinearLayoutManager)rcvRecentHorizontalmax.getLayoutManager())
                .findFirstVisibleItemPosition();
    }

    private void setupAdapterCard(String keyid){

        dataFire.getDbRefUsers().child(keyid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotusers) {
                arrayListImages = new ArrayList<>();

                if (dataSnapshotusers.hasChild("username") && dataSnapshotusers.hasChild("photoProfile")) {

                    tvSendCommonMsg.setVisibility(View.VISIBLE);
                    tvCoinsSendMsg.setVisibility(View.VISIBLE);

                    String userUsername = dataSnapshotusers.child("username").getValue().toString();
                    String school = dataSnapshotusers.child("school").getValue().toString();
                    String job = dataSnapshotusers.child("job").getValue().toString();
                    String userGender = dataSnapshotusers.child("gender").getValue().toString();
                    String userAge = dataSnapshotusers.child("age").getValue().toString();
                    String about = dataSnapshotusers.child("about").getValue().toString();

                    for (DataSnapshot serieSnapshot : dataSnapshotusers.child("images").getChildren()) {
                        Images images = serieSnapshot.getValue(Images.class);
                        Images img = new Images();
                        if(!images.getThumb_picture().equals("none")) {
                            img.setThumb_picture(images.getThumb_picture());
                            arrayListImages.add(img);
                        }

                    }

                    if(dataSnapshotusers.hasChild("city") && dataSnapshotusers.hasChild("country")){
                        String city = dataSnapshotusers.child("city").getValue().toString();
                        String country = dataSnapshotusers.child("country").getValue().toString();

                        item  = new CardMatches(dataSnapshotusers.getKey(),userUsername,userAge,userGender,job,school,arrayListImages,about,city,country);
                        rowItemsMatches.add(item);
                        arrAdpMatches.notifyDataSetChanged();
                    }else{
                        item  = new CardMatches(dataSnapshotusers.getKey(),userUsername,userAge,userGender,job,school,arrayListImages,about,"--- ","---");
                        rowItemsMatches.add(item);
                        arrAdpMatches.notifyDataSetChanged();
                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void getRecentlySearchUsers(){
        dataFire.getDbHistory().child(dataFire.getUserID()).orderByChild("Time").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                setupAdapterCard(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void widgets() {

        rcvRecentHorizontalmax=findViewById(R.id.rcvRecentHorizontalmax);
        rcvRecentHorizontalmax.setHasFixedSize(true);
        mLayoutManagermax = new LinearLayoutManager(HistoryActivity.this,LinearLayoutManager.HORIZONTAL,false);
        rcvRecentHorizontalmax.setLayoutManager(mLayoutManagermax);

        imgvRecentlySearchBack=findViewById(R.id.imgvRecentlySearchBack);
        tvSendCommonMsg=findViewById(R.id.tv_send_common_msg);
        tvCoinsSendMsg=findViewById(R.id.tv_coins_send_msg);
        adContainer = findViewById(R.id.banner_container);

    }



    @Override
    protected void onStart() {
        super.onStart();

        dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("purchase")){
                    String purchase = String.valueOf(snapshot.child("purchase").getValue());
                    if(purchase.equals("true")){
                        adContainer.setVisibility(View.GONE);
                    }else{
                        bannerFacebbok();
                        adContainer.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void bannerFacebbok(){

        // banner ads facebook
        adViewBanner = new AdView(HistoryActivity.this, getString(R.string.Banner_ads_facebook), AdSize.BANNER_HEIGHT_50);

        // Add the ad view to your activity layout
        adContainer.addView(adViewBanner);
        adViewBanner.loadAd();
    }

}
