package com.rifcode.holalapremium.View.Main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Adapter.ArrayAdapterMatches;
import com.rifcode.holalapremium.Models.CardMatches;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.Models.Images;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.DialogUtils;
import com.rifcode.holalapremium.Utils.GPSTracker;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NearbyActivity extends AppCompatActivity implements CardStackListener {

    private CrystalRangeSeekbar rangeSeeker;
    private LinearLayout llDialogNearbyFilterRoot,llLikeBar;
    private CardStackView csvNearby;
    private ImageView imgvNearbyBack,imgvNearbyReturnLastItem,imgvNearbyFilter
            ,imgvNearbyFilterSave,imgvLikeMatches,imgvDislikeMatches,imgvNearbyFilterClose;
    private RadioButton rbBoth,rbGuys,rbGirls;
    private TextView tvAgeRange;
    private DataFire dataFire;
    private CardMatches item;
    private ArrayList<Images> arrayListImages;
    private ArrayAdapterMatches arrAdpMatches;
    private ArrayList<CardMatches> rowItemsMatches;
    private CardStackLayoutManager manager;
    private String oppositeGender;
    private String countryName,cityName;
    private String countryCode;
    private AlertDialog alertDialogGPS;
    private View mViewInflateEnableGPS;
    private int ACCESS_FINE_LOCATION_NUM=555;
    private String typeSwiping;
    private String lastIDUser;
    private String userId;
    private TextView tvNopeopleNearby;
    private Long mmaxValue;
    private Long mminValue;
    private String genderMeetChooser;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        dataFire=new DataFire();
        rowItemsMatches = new ArrayList<CardMatches>();
        widgets();
        llLikeBar.setBackgroundResource(0);
        llLikeBar.setBackground(null);

        arrAdpMatches = new ArrayAdapterMatches(NearbyActivity.this, item,rowItemsMatches);
        csvNearby.setAdapter(arrAdpMatches);
        manager = new CardStackLayoutManager( this,this);
        csvNearby.setLayoutManager(manager);
        manager.setCanScrollVertical(false);



        dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("purchase")){
                    String purchase = String.valueOf(snapshot.child("purchase").getValue());
                    if(purchase.equals("true")){
                    }else{
                        // interstial admob
                        mInterstitialAd = new InterstitialAd(NearbyActivity.this);
                        mInterstitialAd.setAdUnitId(getString(R.string.InterstitialAdAdmobID));
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());

                        //load so show the ads InterstitialAd
                        if (mInterstitialAd.isLoaded())
                            mInterstitialAd.show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        imgvNearbyReturnLastItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (typeSwiping!=null && lastIDUser!=null) {

                        if (typeSwiping.equals("like")) {

                            RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                                    .setDirection(Direction.Right)
                                    .build();
                            manager.setRewindAnimationSetting(setting);
                            csvNearby.rewind();

                        } else if (typeSwiping.equals("dislike")) {

                            RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                                    .setDirection(Direction.Left)
                                    .build();
                            manager.setRewindAnimationSetting(setting);
                            csvNearby.rewind();
                        }
                    }
            }
        });

        // set final value listener
        rangeSeeker.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                mmaxValue = maxValue.longValue();
                mminValue = minValue.longValue();
            }
        });

        imgvLikeMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting swipeAnimationSetting= new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .build();
                manager.setSwipeAnimationSetting(swipeAnimationSetting);
                csvNearby.swipe();
            }
        });

        imgvDislikeMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting swipeAnimationSetting= new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .build();
                manager.setSwipeAnimationSetting(swipeAnimationSetting);
                csvNearby.swipe();
            }
        });

        imgvNearbyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDialogNearbyFilterRoot.setVisibility(View.VISIBLE);
            }
        });

        imgvNearbyFilterClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDialogNearbyFilterRoot.setVisibility(View.GONE);
            }
        });


        imgvNearbyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgvNearbyFilterSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mmaxValue!=null)
                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("max_age_meet").setValue(String.valueOf(mmaxValue));
                if (mminValue!=null)
                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("min_age_meet").setValue(String.valueOf(mminValue));

                if(genderMeetChooser!=null)
                dataFire.getDbRefUsers().child(dataFire.getUserID())
                        .child("genderMeet").setValue(genderMeetChooser)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        llDialogNearbyFilterRoot.setVisibility(View.GONE);
                        if(rowItemsMatches.size()>0)
                            rowItemsMatches.clear();
                        arrAdpMatches.notifyDataSetChanged();

                        getMyLatLong();
                        checkMySearchPreferences();
                    }
                });

            }
        });

        rbBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbBoth.setChecked(true);
                rbGirls.setChecked(false);
                rbGuys.setChecked(false);
                genderMeetChooser="both";
            }
        });

        rbGirls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbGirls.setChecked(true);
                rbBoth.setChecked(false);
                rbGuys.setChecked(false);
                genderMeetChooser="girl";
            }
        });

        rbGuys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbGuys.setChecked(true);
                rbBoth.setChecked(false);
                rbGirls.setChecked(false);
                genderMeetChooser="guy";
            }
        });


        // set listener
        rangeSeeker.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                String minmax =minValue+" - "+maxValue;
                tvAgeRange.setText(minmax);
            }
        });

    }

    private void getMatches(final String maxAge, final String minAge){

        dataFire.getDbRefUsers().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshotusers, String s) {

                if (!dataSnapshotusers.getKey().equals(dataFire.getUserID())) {

                    dataFire.getDbConnections().child(String.valueOf(dataSnapshotusers.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotConnection) {

                            if(dataSnapshotusers.hasChild("age") && !String.valueOf(dataSnapshotusers.child("age").getValue()).equals("")) {

                                if (!dataSnapshotConnection.child("dislike").hasChild(dataFire.getUserID())
                                        && !dataSnapshotConnection.child("like").hasChild(dataFire.getUserID())
                                        && !dataSnapshotConnection.child("matches").hasChild(dataFire.getUserID())
                                        && Integer.parseInt(String.valueOf(dataSnapshotusers.child("age").getValue()))
                                        >= Integer.parseInt(minAge)
                                        && Integer.parseInt(String.valueOf(dataSnapshotusers.child("age").getValue()))
                                        <= Integer.parseInt(maxAge)) {


                                    if (dataSnapshotusers.hasChild("city") && dataSnapshotusers.hasChild("country_code")) {

                                        if (oppositeGender.equals("both")) {
                                            if ((dataSnapshotusers.child("city").getValue().toString().equals(cityName)
                                                    || dataSnapshotusers.child("country_code").getValue().toString().equals(countryCode))
                                            ) {
                                                setupAdapterCard(dataSnapshotusers);
                                            }
                                        } else {
                                            if (dataSnapshotusers.child("gender").getValue().toString().equals(oppositeGender)
                                                    && (dataSnapshotusers.child("city").getValue().toString().equals(cityName)
                                                    || dataSnapshotusers.child("country_code").getValue().toString().equals(countryCode))
                                            ) {
                                                setupAdapterCard(dataSnapshotusers);
                                            }
                                        }

                                    }

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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setupAdapterCard(DataSnapshot dataSnapshotusers){
        arrayListImages = new ArrayList<>();

        if (dataSnapshotusers.hasChild("username") && dataSnapshotusers.hasChild("photoProfile")) {

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



    private void checkMySearchPreferences(){
        DatabaseReference dbMyIDusers = dataFire.getDbRefUsers().child(dataFire.getUserID());
        dbMyIDusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String genderMeet = dataSnapshot.child("genderMeet").getValue().toString();
                String max_age_meet = dataSnapshot.child("max_age_meet").getValue().toString();
                String min_age_meet = dataSnapshot.child("min_age_meet").getValue().toString();
                oppositeGender=genderMeet;

                rangeSeeker.setMaxStartValue(Float.parseFloat(max_age_meet));
                rangeSeeker.setMinStartValue(Float.parseFloat(min_age_meet));
                rangeSeeker.apply();

                String minmax =min_age_meet+" - "+max_age_meet;
                tvAgeRange.setText(minmax);

                if(genderMeet.equals("guy")){
                    rbGuys.setChecked(true);
                    rbBoth.setChecked(false);
                    rbGirls.setChecked(false);
                }else if(genderMeet.equals("girl")){
                    rbGuys.setChecked(false);
                    rbBoth.setChecked(false);
                    rbGirls.setChecked(true);
                }else{
                    rbGuys.setChecked(false);
                    rbBoth.setChecked(true);
                    rbGirls.setChecked(false);
                }

                genderMeetChooser=oppositeGender;
                mminValue= Long.parseLong(min_age_meet);
                mmaxValue= Long.parseLong(max_age_meet);

                getMatches(max_age_meet,min_age_meet);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(rowItemsMatches.size()>0)
            rowItemsMatches.clear();
        arrAdpMatches.notifyDataSetChanged();

        getMyLatLong();
        checkMySearchPreferences();


    }

    private void widgets() {

        llDialogNearbyFilterRoot=findViewById(R.id.ll_dialog_nearby_filter_root);
        llLikeBar=findViewById(R.id.llLikeBar);
        csvNearby=findViewById(R.id.csvNearby);
        imgvNearbyReturnLastItem=findViewById(R.id.imgvNearbyReturnLastItem);
        imgvNearbyFilterClose=findViewById(R.id.imgvNearbyFilterClose);
        imgvNearbyBack=findViewById(R.id.imgvNearbyBack);
        imgvNearbyFilter=findViewById(R.id.imgvNearbyFilter);
        imgvDislikeMatches=findViewById(R.id.imgvDislikeMatches);
        imgvLikeMatches=findViewById(R.id.imgvLikeMatches);
        tvNopeopleNearby=findViewById(R.id.tvNopeopleNearby);

        //dialog widgets
        imgvNearbyFilterSave=findViewById(R.id.imgvNearbyFilterSave);
        rbGuys=findViewById(R.id.rb_guys);
        rbGirls=findViewById(R.id.rb_girls);
        rbBoth=findViewById(R.id.rb_both);
        tvAgeRange=findViewById(R.id.tv_age_range);
        // Range seekbar setup
        rangeSeeker =findViewById(R.id.rangeSeekbarNearbyFilter);
        rangeSeeker.setMinValue(18F);
        rangeSeeker.setMaxValue(99F);

        rangeSeeker.apply();

    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {
        Log.d("CardStackView", "onCardDragging:");

    }

    @Override
    public void onCardSwiped(Direction direction) {
        Log.d("CardStackView", "onCardSwiped:");
         userId = rowItemsMatches.get(manager.getTopPosition()-1).getUserId();
         lastIDUser=userId;
        if(direction.equals(Direction.Left)){
            typeSwiping="dislike";
              onDislikeUser(userId);
        }

        if(direction.equals(Direction.Right)){
            typeSwiping="like";
            onLikeUser(userId);
        }


    }

    @Override
    public void onCardRewound() {
        Log.d("CardStackView", "onCardRewound:");
        if (!typeSwiping.equals("") && !lastIDUser.equals("")) {

            if (typeSwiping.equals("like")) {

                dataFire.getDbConnections().child(lastIDUser).child("like").child(dataFire.getUserID()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

            } else if (typeSwiping.equals("dislike")) {

                dataFire.getDbConnections().child("dislike").child(dataFire.getUserID()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

            }
        }
    }

    @Override
    public void onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled:");
    }

    @Override
    public void onCardAppeared(View view, int position) {
        Log.d("CardStackView", "onCardAppeared:");
        llLikeBar.setVisibility(View.VISIBLE);
        tvNopeopleNearby.setVisibility(View.GONE);
        imgvNearbyReturnLastItem.setVisibility(View.VISIBLE);
        if(lastIDUser==null){
            imgvNearbyReturnLastItem.setAlpha(0.5f);
        }else{
            imgvNearbyReturnLastItem.setAlpha(1f);
        }
    }

    @Override
    public void onCardDisappeared(View view, int position) {
        Log.d("CardStackView", "onCardDisappeared:");
       llLikeBar.setVisibility(View.GONE);
        tvNopeopleNearby.setVisibility(View.VISIBLE);
        imgvNearbyReturnLastItem.setVisibility(View.GONE);
    }

    private void onLikeUser(String userId){
        // when i liked someone, set myid in his or her connections like
        dataFire.getDbConnections().child(userId).child("like").child(dataFire.getUserID()).child("Time")
                .setValue(-1 * (System.currentTimeMillis()/1000));

        isConnectionMatch(userId);
    }

    private void onDislikeUser(String userId){
        // when i disliked someone, set myid in his or her connections dislike
        dataFire.getDbConnections().child(userId)
                .child("dislike")
                .child(dataFire.getUserID()).child("Time")
                .setValue(-1 * (System.currentTimeMillis()/1000));
        DatabaseReference dbCheckDislike = dataFire.getDbConnections().child(dataFire.getUserID()).child("like");
        if (dbCheckDislike.child(userId) != null)
            dataFire.getDbConnections().child(dataFire.getUserID()).child("like").child(userId).removeValue();

    }

    private void isConnectionMatch(final String userId) {
        // check if already she or he like
        DatabaseReference dbConnectionAlreadyLike = dataFire.getDbConnections().child(dataFire.getUserID()).child("like").child(userId);
        dbConnectionAlreadyLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    dataFire.getDbRefUsers().child(dataFire.getUserID())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String gems = String.valueOf(snapshot.child("gems").getValue());
                            String first_friend_match = String.valueOf(snapshot.child("first_friend_match").getValue());

                            if(first_friend_match.equals("false")){
                                dataFire.getDbRefUsers().child(dataFire.getUserID())
                                        .child("gems")
                                        .setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string.earn_4_new_match)));
                                dataFire.getDbRefUsers().child(dataFire.getUserID())
                                        .child("first_friend_match")
                                        .setValue("true");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    dataFire.getDbConnections().child(dataFire.getUserID()).child("matches").child(userId).child("Time").setValue(-1*(System.currentTimeMillis()/1000));
                    dataFire.getDbConnections().child(userId).child("matches").child(dataFire.getUserID()).child("Time").setValue(-1*(System.currentTimeMillis()/1000));

                    dataFire.getDbRefUsers().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String userusername = String.valueOf(dataSnapshot.child("username").getValue());
                            dataFire.getDbConnections().child(dataFire.getUserID()).child("matches").child(userId)
                                    .child("username").setValue(userusername);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String myusername=String.valueOf(dataSnapshot.child("username").getValue());
                            dataFire.getDbConnections().child(userId).child("matches").child(dataFire.getUserID())
                                    .child("username").setValue(myusername);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    dataFire.getDbConnections().child(dataFire.getUserID()).child("like").child(userId).removeValue();
                    dataFire.getDbConnections().child(userId).child("like").child(dataFire.getUserID()).removeValue();
                    dataFire.getDbConnections().child(userId).child("dislike").child(dataFire.getUserID()).removeValue();
                    dataFire.getDbConnections().child(dataFire.getUserID()).child("dislike").child(userId).removeValue();

                    // notification match here
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public  boolean isStoragePermissionGPSGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_NUM);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    private void getMyLatLong() {
        if(isStoragePermissionGPSGranted()){
            getLocaiton();
        }
    }

    private void getLocaiton(){
        GPSTracker gps1 = new GPSTracker(this, this);

        // Check if GPS enabled
        if (gps1.canGetLocation()) {

            double latitude = gps1.getLatitude();
            double longitude = gps1.getLongitude();


            Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if(addresses!=null && addresses.size()!=0) {

                    cityName = addresses.get(0).getLocality();

                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("city").setValue(cityName);

                    countryName = addresses.get(0).getCountryName();

                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("country").setValue(countryName);

                    countryCode = addresses.get(0).getCountryCode();

                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("country_code").setValue(countryCode);


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getString(R.string.sorry_noflocarion), Toast.LENGTH_SHORT).show();
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {

        mViewInflateEnableGPS = getLayoutInflater().inflate(R.layout.dialog_enable_mylocation, null);

        TextView tv_dialog_gps_confirm = mViewInflateEnableGPS.findViewById(R.id.tv_dialog_gps_confirm);

        final AlertDialog.Builder adenableGPS = DialogUtils.CustomAlertDialog(mViewInflateEnableGPS, this);
        alertDialogGPS = adenableGPS.create();
        alertDialogGPS.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation; //style id
        alertDialogGPS.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogGPS.setCancelable(true);
        alertDialogGPS.show();

        tv_dialog_gps_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogGPS.dismiss();
                alertDialogGPS.cancel();
                onStart();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_NUM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.fine_location_perm_granted, Toast.LENGTH_LONG).show();
                buildAlertMessageNoGps();
            } else {
                Toast.makeText(this, R.string.fine_location_perms_denied, Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
