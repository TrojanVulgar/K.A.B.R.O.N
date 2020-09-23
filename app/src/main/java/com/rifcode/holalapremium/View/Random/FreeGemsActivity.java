package com.rifcode.holalapremium.View.Random;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;

import java.util.concurrent.TimeUnit;

public class FreeGemsActivity extends AppCompatActivity {

    private ImageView imgvFreeGemsBack;
    private LinearLayout lyTurnOnNotifications;
    private  DataFire dataFire;
    private Button btnTurnOnnotifi;
    private Button btnInvite,btnWatchAds,btnCheckinEveryDay;
    private String TAG="FREEGems";
    private RewardedAd extraCoinsRewardedAd;
    private static final long START_TIME_IN_MILLIS = 86400000;
    private static final long START_TIME_IN_MILLISwatch_ads = 1800000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private SharedPreferences prefs;
    private SharedPreferences prefsWatchAds;
    private long mTimeLeftInMillisWatchAds;
    private boolean mTimerRunningWatchAds;
    private long mEndTimeWatchAds;
    private CountDownTimer mCountDownTimerWatchAds;
    private LinearLayout lyFirstVideoMatch,lyFirstVoiceMatch,lyFirstMatchMatch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_gems);
        dataFire=new DataFire();
        widgets();
        getdata();

        btnCheckinEveryDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String gems = String.valueOf(dataSnapshot.child("gems").getValue());

                        dataFire.getDbRefUsers().child(dataFire.getUserID()).child("gems")
                                .setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string.earn_50_evry_day)))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startTimer();
                                        Toast.makeText(FreeGemsActivity.this, getString(R.string.earn_you), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });

        btnWatchAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rewarded ads
                extraCoinsRewardedAd = createAndLoadRewardedAd(
                        getString(R.string.REWARDED_ADS_ID));
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReferlink();
            }
        });

        imgvFreeGemsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnTurnOnnotifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String gems = String.valueOf(dataSnapshot.child("gems").getValue());

                        dataFire.getDbRefUsers().child(dataFire.getUserID()).child("gems")
                                .setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string.earn10_notification)))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dataFire.getDbRefUsers().child(dataFire.getUserID()).child("notifi_on").setValue("true");

                                        lyTurnOnNotifications.setAlpha(0.5f);
                                        btnTurnOnnotifi.setEnabled(false);
                                        Intent i = new Intent();
                                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        i.addCategory(Intent.CATEGORY_DEFAULT);
                                        i.setData(Uri.parse("package:" + getPackageName()));
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        startActivity(i);
                                    }
                                });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });

    }


    public void createReferlink(){

        String link = getString(R.string.site_web_link)+"/?invitedby=" + dataFire.getUserID();
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://"+getString(R.string.domain_link))
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(getPackageName())
                        .setMinimumVersion(125).build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        Uri mInvitationUrl = shortDynamicLink.getShortLink();
                        Log.d(TAG, "onSuccess ShortDynamicLink : "+mInvitationUrl.toString());

                        // share app dialog
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,  mInvitationUrl.toString());
                            intent.setType("text/plain");
                            startActivity(intent);
                        // ...
                    }
                });


    }

    private void widgets() {
        imgvFreeGemsBack=findViewById(R.id.imgvFreeGemsBack);
        lyTurnOnNotifications=findViewById(R.id.lyNotificayionON);
        lyFirstVideoMatch=findViewById(R.id.lyFirstVideoMatch);
        lyFirstMatchMatch=findViewById(R.id.lyFirstMatchMatch);
        lyFirstVoiceMatch=findViewById(R.id.lyFirstVoiceMatch);
        btnTurnOnnotifi=findViewById(R.id.btnTurnOnnotifi);
        btnInvite = findViewById(R.id.btnInvite);
        btnWatchAds = findViewById(R.id.btnWatchAds);
        btnCheckinEveryDay = findViewById(R.id.btnCheckinEveryDay);
    }


    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        btnCheckinEveryDay.setEnabled(false);
        btnCheckinEveryDay.setAlpha(0.5f);
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mTimeLeftInMillis = millisUntilFinished;

                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                btnCheckinEveryDay.setEnabled(true);
                btnCheckinEveryDay.setAlpha(1f);
                btnCheckinEveryDay.setText(R.string.check_in);
                prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                prefs.edit().clear().apply();
                mTimeLeftInMillis = 86400000;
            }
        }.start();

        mTimerRunning = true;
    }

    private void getdata(){
        dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("first_voice_calls")) {

                    String first_voice_calls = String.valueOf(dataSnapshot.child("first_voice_calls").getValue());

                    if (first_voice_calls.equals("true")) {
                        lyFirstVoiceMatch.setAlpha(0.5f);
                    }
                }
                if(dataSnapshot.hasChild("first_video_calls")) {

                    String first_video_calls = String.valueOf(dataSnapshot.child("first_video_calls").getValue());

                    if (first_video_calls.equals("true")) {
                        lyFirstVideoMatch.setAlpha(0.5f);
                    }
                }
                if(dataSnapshot.hasChild("first_friend_match")) {

                    String first_friend_match = String.valueOf(dataSnapshot.child("first_friend_match").getValue());

                    if (first_friend_match.equals("true")) {
                        lyFirstMatchMatch.setAlpha(0.5f);
                    }
                }

                if (!dataSnapshot.hasChild("notifi_on")){
                    isNotificationEnabled();
                }else{
                    String notifi_enabled = String.valueOf(dataSnapshot.child("notifi_on").getValue());
                    if (notifi_enabled.equals("true")){
                        lyTurnOnNotifications.setAlpha(0.5f);
                        btnTurnOnnotifi.setEnabled(false);
                    }else{
                        isNotificationEnabled();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void isNotificationEnabled() {
        if(!NotificationManagerCompat.from(this).areNotificationsEnabled()){
            lyTurnOnNotifications.setAlpha(1f);
            btnTurnOnnotifi.setEnabled(true);
        }else{
            lyTurnOnNotifications.setAlpha(0.5f);
            btnTurnOnnotifi.setEnabled(false);
            dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String gems = String.valueOf(dataSnapshot.child("gems").getValue());

                     dataFire.getDbRefUsers().child(dataFire.getUserID()).child("gems")
                    .setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string.earn10_notification)))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dataFire.getDbRefUsers().child(dataFire.getUserID()).child("notifi_on").setValue("true");

                        }
                    });

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public RewardedAd createAndLoadRewardedAd(String adUnitId) {

        final RewardedAd rewardedAd = new RewardedAd(this, adUnitId);
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                // Ad successfully loaded.
                if (rewardedAd.isLoaded()) {
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                            Log.d("TAG", "Ad open");
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            Log.d("TAG", "Ad closed");
                        }
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            startTimerWatchAds();

                            // User earned reward.
                            Log.d("TAG", "User earned reward");
                            dataFire.getDbRefUsers().child(dataFire.getUserID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String gems = String.valueOf(snapshot.child("gems").getValue());

                                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("gems")
                                            .setValue(Integer.parseInt(gems)+Integer.parseInt(getString(R.string.earn_15_reward_ads)))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(FreeGemsActivity.this, R.string.earn_you, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            // Ad failed to display.
                        }
                    };
                    rewardedAd.show(FreeGemsActivity.this, adCallback);
                } else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateCountDownText() {

        String timeLeftFormatted = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis),
                TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis)));

        btnCheckinEveryDay.setText(timeLeftFormatted);
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                btnCheckinEveryDay.setEnabled(true);
                btnCheckinEveryDay.setText(R.string.check_in);
                btnCheckinEveryDay.setAlpha(1f);
            } else {
                startTimer();
            }
        }else{
            btnCheckinEveryDay.setEnabled(true);
            btnCheckinEveryDay.setText(R.string.check_in);
            btnCheckinEveryDay.setAlpha(1f);
        }

        //watch ads
        prefsWatchAds = getSharedPreferences("prefs_watch_ads", MODE_PRIVATE);
        mTimeLeftInMillisWatchAds = prefsWatchAds.getLong("millisLeft_watch_ads", START_TIME_IN_MILLISwatch_ads);
        mTimerRunningWatchAds = prefsWatchAds.getBoolean("timerRunning_watch_ads", false);

        updateCountDownTextWatchAds();

        if (mTimerRunningWatchAds) {
            mEndTimeWatchAds = prefsWatchAds.getLong("endTimeWatchAds", 0);
            mTimeLeftInMillisWatchAds = mEndTimeWatchAds - System.currentTimeMillis();

            if (mTimeLeftInMillisWatchAds < 0) {
                mTimeLeftInMillisWatchAds = 0;
                mTimerRunningWatchAds = false;
                updateCountDownTextWatchAds();
                btnWatchAds.setEnabled(true);
                btnWatchAds.setText(R.string.watch_ads);
                btnWatchAds.setAlpha(1f);
            } else {
                startTimerWatchAds();
            }
        }else{
            btnWatchAds.setEnabled(true);
            btnWatchAds.setText(R.string.watch_ads);
            btnWatchAds.setAlpha(1f);
        }


    }

    private void startTimerWatchAds() {

        mEndTimeWatchAds = System.currentTimeMillis() + mTimeLeftInMillisWatchAds;

        btnWatchAds.setEnabled(false);
        btnWatchAds.setAlpha(0.5f);
        mCountDownTimerWatchAds = new CountDownTimer(mTimeLeftInMillisWatchAds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mTimeLeftInMillisWatchAds = millisUntilFinished;

                updateCountDownTextWatchAds();
            }

            @Override
            public void onFinish() {
                mTimerRunningWatchAds = false;
                btnWatchAds.setEnabled(true);
                btnWatchAds.setAlpha(1f);
                btnWatchAds.setText(R.string.watch_ads);
                prefsWatchAds = getSharedPreferences("prefs_watch_ads", MODE_PRIVATE);
                prefsWatchAds.edit().clear().apply();
                mTimeLeftInMillisWatchAds = 1800000;
            }
        }.start();

        mTimerRunningWatchAds = true;

    }

    private void updateCountDownTextWatchAds() {

        String timeLeftFormattedwa = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillisWatchAds),
                TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillisWatchAds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillisWatchAds)));

        btnWatchAds.setText(timeLeftFormattedwa);
    }

    @Override
    public void onStop() {

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        prefsWatchAds = getSharedPreferences("prefs_watch_ads", MODE_PRIVATE);
        SharedPreferences.Editor editorwatchads = prefsWatchAds.edit();

        editorwatchads.putLong("millisLeft_watch_ads", mTimeLeftInMillisWatchAds);
        editorwatchads.putBoolean("timerRunning_watch_ads", mTimerRunningWatchAds);
        editorwatchads.putLong("endTimeWatchAds", mEndTimeWatchAds);

        editorwatchads.apply();

        if (mCountDownTimerWatchAds != null) {
            mCountDownTimerWatchAds.cancel();
        }

        super.onStop();
    }

}
