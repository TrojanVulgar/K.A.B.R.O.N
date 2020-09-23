package com.rifcode.holalapremium.View.Main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.camerakit.CameraKitView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.polyak.iconswitch.IconSwitch;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.BSDFiltersRandom;
import com.rifcode.holalapremium.Utils.DialogUtils;
import com.rifcode.holalapremium.Utils.GPSTracker;
import com.rifcode.holalapremium.View.Random.FreeGemsActivity;
import com.rifcode.holalapremium.View.Random.GemsActivity;
import com.rifcode.holalapremium.View.Random.SearchRandomActivity;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public  class RandomPeopleFragment extends Fragment implements IconSwitch.CheckedChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    private View view;
    private CameraKitView cameraKitView;
    private RippleBackground rippleBackground;
    private TextView tvStartRandom;
    private LinearLayout llRandomFilter;
    private IconSwitch iconSwitch;
    private String currentCheck,strcheckOption;
    private SharedPreferences.Editor checkOption;
    private SharedPreferences prefscheckOption;
    private DataFire dataFire;
    private int ACCESS_FINE_LOCATION_NUM=555;
    private View mViewInflateEnableGPS;
    private AlertDialog alertDialogGPS;
    private ImageView imgvRandomHistory,imgvFreeGems,imgvMyGems;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    private int REQUEST_CHECK_SETTINGS = 100;
    private SharedPreferences prefs_region_preferences,prefscheckMatchWith;
    private String get_region_preferences;
    private SharedPreferences.Editor edit_region_preferences,edit_checkMatchWith;
    private String strMatchWith;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_random_people, container, false);
        dataFire=new DataFire();
        widgets();
        animationTextViewTaptoStart();
        checkOption = getActivity().getSharedPreferences("checkOption", MODE_PRIVATE).edit();
        prefscheckOption = getActivity().getSharedPreferences("checkOption", Context.MODE_PRIVATE);
        strcheckOption = prefscheckOption.getString("checkOption", null);

        edit_checkMatchWith = getActivity().getSharedPreferences("match_with", MODE_PRIVATE).edit();
        prefscheckMatchWith = getActivity().getSharedPreferences("match_with", Context.MODE_PRIVATE);
        strMatchWith = prefscheckMatchWith.getString("match_with", null);

        // region preferences
        edit_region_preferences = getActivity().getSharedPreferences("region_preferences", MODE_PRIVATE).edit();
        prefs_region_preferences = getActivity().getSharedPreferences("region_preferences", Context.MODE_PRIVATE);
        get_region_preferences = prefs_region_preferences.getString("region_preferences", null);

        if(get_region_preferences==null){
            edit_region_preferences.putString("region_preferences", "global");
            edit_region_preferences.apply();
        }

        if (strMatchWith==null){
            edit_checkMatchWith.putString("match_with", "both");
            edit_checkMatchWith.apply();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        if (strcheckOption==null){
            iconSwitch.setChecked(IconSwitch.Checked.LEFT);
            currentCheck = "voice";
            checkOption.putString("checkOption", currentCheck);
            checkOption.apply();
        }
        else {
            if (strcheckOption.equals("video")) {
                iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
            } else {
                iconSwitch.setChecked(IconSwitch.Checked.LEFT);
            }
        }

        llRandomFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSDFiltersRandom bsdFiltersRandom = new BSDFiltersRandom();
                bsdFiltersRandom.show( getActivity().getSupportFragmentManager(), "exampleBottomSheet");
            }
        });


        rippleBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_region_preferences = prefs_region_preferences.getString("region_preferences", null);
                if(get_region_preferences.equals("nearby")){
                    dataFire.getDbRefUsers().child(dataFire.getUserID())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.hasChild("city")){
                                buildAlertMessageNoGps();
                            }else{
                                Intent searchIntent = new Intent(getActivity(), SearchRandomActivity.class);
                                startActivity(searchIntent);
                                Animatoo.animateSlideLeft(getActivity());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    Intent searchIntent = new Intent(getActivity(), SearchRandomActivity.class);
                    startActivity(searchIntent);
                    Animatoo.animateSlideLeft(getActivity());
                }
            }
        });

        imgvRandomHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),HistoryActivity.class));
            }
        });

        imgvMyGems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GemsActivity.class));
            }
        });

        imgvFreeGems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FreeGemsActivity.class));
            }
        });

        return view;
    }


    private void widgets() {
        cameraKitView = view.findViewById(R.id.camera);
        rippleBackground = view.findViewById(R.id.rippleBackground);
        tvStartRandom = view.findViewById(R.id.tvStartRandom);
        llRandomFilter = view.findViewById(R.id.llRandomFilter);
        imgvRandomHistory = view.findViewById(R.id.imgvRandomHistory);
        imgvFreeGems = view.findViewById(R.id.imgvFreeGems);
        imgvMyGems = view.findViewById(R.id.imgvMyGems);
        iconSwitch =  view.findViewById(R.id.icon_switch);
        iconSwitch.setCheckedChangeListener(this);


    }

    private void animationTextViewTaptoStart(){
        rippleBackground.startRippleAnimation();
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);
        tvStartRandom.startAnimation(a);
    }

    @Override
    public void onStart() {
        super.onStart();
        cameraKitView.onStart();

        if(isStoragePermissionGPSGranted()) { }

        try {
            dataFire.getDbRefSearch().child(dataFire.getUserID()).removeValue();
        }catch (Exception e){}

    }

    @Override
    public void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    public void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_NUM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), R.string.fine_location_perm_granted, Toast.LENGTH_LONG).show();
                buildAlertMessageNoGps();
            } else {
                Toast.makeText(getActivity(), R.string.fine_location_perms_denied, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void buildAlertMessageNoGps() {

        mViewInflateEnableGPS = getLayoutInflater().inflate(R.layout.dialog_enable_mylocation, null);

        TextView tv_dialog_gps_confirm = mViewInflateEnableGPS.findViewById(R.id.tv_dialog_gps_confirm);

        final AlertDialog.Builder adenableGPS = DialogUtils.CustomAlertDialog(mViewInflateEnableGPS, getActivity());
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
                getLocation();
            }
        });

    }


    @Override
    public void onCheckChanged(IconSwitch.Checked current) {
        switch (iconSwitch.getChecked()) {
            case LEFT:
                currentCheck = "voice";
                checkOption.putString("checkOption", currentCheck);
                checkOption.apply();
                break;
            case RIGHT:
                currentCheck = "video";
                checkOption.putString("checkOption", currentCheck);
                checkOption.apply();
                break;
        }
    }



    public  boolean isStoragePermissionGPSGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_NUM);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private void getLocation(){
        GPSTracker gps1 = new GPSTracker(getActivity(),getActivity());

        // Check if GPS enabled
        if (gps1.canGetLocation()) {

            double latitude = gps1.getLatitude();
            double longitude = gps1.getLongitude();


            Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if(addresses!=null && addresses.size()!=0) {

                    String cityName = addresses.get(0).getLocality();
                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("city").setValue(cityName);
                    String countryName = addresses.get(0).getCountryName();
                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("country").setValue(countryName);
                    final List<Address> finalAddresses = addresses;
                    String countryCode = finalAddresses.get(0).getCountryCode();
                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("country_code").setValue(countryCode);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            buildAlertMessageNoGps();
        }
    }


    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
                getLocation();
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



}
