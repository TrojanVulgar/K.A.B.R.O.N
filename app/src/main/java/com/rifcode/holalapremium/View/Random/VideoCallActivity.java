package com.rifcode.holalapremium.View.Random;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Adapter.ChatVoiceViewHolders;
import com.rifcode.holalapremium.Models.ChatObject;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.BSDChooseGift;
import com.rifcode.holalapremium.Utils.DialogUtils;
import com.rifcode.holalapremium.View.Main.MainActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VideoCallActivity extends AppCompatActivity implements BSDChooseGift.BottomSheetListener{

    private DataFire dataFire;
    private ImageView ivVideoCallChat,imgCloseEdtMessage;
    private LinearLayout llDiscoverInputMessage;
    private EditText edtDiscoverInputMessage;
    private InputMethodManager inputManager;

    private static final String TAG = VideoCallActivity.class.getSimpleName();
    private String video_room_id;
    public static String userIDvisited;

    private static final int PERMISSION_REQ_ID = 22;

    // permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine mRtcEngine;

    private FrameLayout mLocalContainer;
    private FrameLayout mRemoteContainer;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;

    private ImageView mSwitchCameraBtn;
    private ImageView imgvRaportChatVideo;
    private CircleImageView imgvPicUserVidcall;
    private ImageView ivExitVicall;
    private ImageView ivAddFriend;
    private View mViewInflatedialogReport;
    private ImageView ivVideoCallGift;
    private View mViewInflatedialogRequestFriend;
    private boolean isRequestSend;
    private LottieAnimationView vSendGiftSuccess;
    private LinearLayoutManager mChatLayoutManager;
    private RecyclerView rvVideoChatMessages;
    private FirebaseRecyclerAdapter<ChatObject, ChatVoiceViewHolders> firebaseRecyclerAdapterChats;
    private DatabaseReference dbMessagingMy,dbMessagingHis;
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        dataFire = new DataFire();
        widgets();
        video_room_id = getIntent().getStringExtra("video_room_id");
        userIDvisited = getIntent().getStringExtra("userIDvisited");

        dbMessagingMy = dataFire.getDbRefMsgCall().child(dataFire.getUserID()).child(userIDvisited);
        dbMessagingHis = dataFire.getDbRefMsgCall().child(userIDvisited).child(dataFire.getUserID());


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(VideoCallActivity.this);

        //Decrease coins when you change country
        decreaseChangeCountry();

        //decrease coins when you choose girl.
        decreaseChooseGirl();
        //Increase coins first video call
        firstVideoCall();

        // Ask for permissions at runtime.
        // This is just an example set of permissions, other permissions
        // May be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {


            dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("purchase")){
                        String purchase = String.valueOf(snapshot.child("purchase").getValue());
                        if(purchase.equals("true")){

                        }else{
                            //facebook ads interstital ad
                            mInterstitialAd = new com.facebook.ads.InterstitialAd(VideoCallActivity.this, getString(R.string.Interstitial_FacebbokAds));
                            // load the ad
                            mInterstitialAd.loadAd();
                            if(mInterstitialAd.isAdLoaded())
                                // Show the ad
                             mInterstitialAd.show();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            dataFire.getDbRefUsers().child(userIDvisited).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String pic = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                    Picasso.get().load(pic).placeholder(R.drawable.icon_register_select_header).into(imgvPicUserVidcall);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            initEngineAndJoinChannel();
        }


        edtDiscoverInputMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    String txt = edtDiscoverInputMessage.getText().toString();
                    if (!txt.isEmpty())
                        sendMessage(txt);
                    llDiscoverInputMessage.setVisibility(View.GONE);
                    closeKeyboard();
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });


        ivAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRequestSend==false) {
                    dataFire.getDbRequests().child(userIDvisited).child(dataFire.getUserID()).child("send_request").setValue("true");
                    Toast.makeText(VideoCallActivity.this, R.string.str_send_reau_seccss, Toast.LENGTH_SHORT).show();
                    isRequestSend=true;
                }
            }
        });
        
        ivVideoCallGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSDChooseGift bSDChooseGift = new BSDChooseGift();
                bSDChooseGift.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        imgvRaportChatVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReportUser();
            }
        });

        ivVideoCallChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDiscoverInputMessage.setVisibility(View.VISIBLE);

            }
        });

        ivExitVicall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        imgCloseEdtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDiscoverInputMessage.setVisibility(View.GONE);
            }
        });

        dataFire.getDbHistory().child(dataFire.getUserID()).child(userIDvisited).child("Time").setValue(System.currentTimeMillis()*-1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

    }

    private void decreaseChooseGirl() {
        if(SearchRandomActivity.strMatchWith.equals("girl")){
            dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("gems")) {

                        String gems = String.valueOf(dataSnapshot.child("gems").getValue());

                        if (Integer.parseInt(gems) >= Integer.parseInt(getString(R.string._gems_search_girls_5))) {
                            dataFire.getDbRefUsers()
                                    .child(dataFire.getUserID())
                                    .child("gems")
                                    .setValue(Integer.parseInt(gems)-Integer.parseInt(getString(R.string._gems_search_girls_5)));
                        }

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }

    private void firstVideoCall() {

        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild("gems") && snapshot.hasChild("first_video_calls")) {

                            String gems = String.valueOf(snapshot.child("gems").getValue());
                            String first_video_calls = String.valueOf(snapshot.child("first_video_calls").getValue());

                            if (first_video_calls.equals("false")) {

                                dataFire.getDbRefUsers().child(dataFire.getUserID())
                                        .child("gems")
                                        .setValue(Integer.parseInt(gems) + Integer.parseInt(getString(R.string.earn_3_video_call)));

                                dataFire.getDbRefUsers().child(dataFire.getUserID())
                                        .child("first_video_calls")
                                        .setValue("true");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    private void decreaseChangeCountry() {
        if(SearchRandomActivity.get_region_preferences.equals("select_country")){
            dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("gems")) {

                        String gems = String.valueOf(dataSnapshot.child("gems").getValue());

                        if (Integer.parseInt(gems) >= Integer.parseInt(getString(R.string._gems_change_country_5))) {
                            dataFire.getDbRefUsers()
                                    .child(dataFire.getUserID())
                                    .child("gems")
                                    .setValue(Integer.parseInt(gems)-Integer.parseInt(getString(R.string._gems_change_country_5)));
                        }

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void closeKeyboard() {
        inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void dialogRequestFriend(){

        mViewInflatedialogRequestFriend= getLayoutInflater().inflate(R.layout.dialog_friend_request,null);
        TextView tvAccept = mViewInflatedialogRequestFriend.findViewById(R.id.tv_dialog_fr_confirm);
        TextView tvDecline = mViewInflatedialogRequestFriend.findViewById(R.id.tv_dialog_fr_cancel);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogRequestFriend
                , VideoCallActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(!this.isFinishing()) {

            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

                alertDialog.setCancelable(true);
                try {
                    alertDialog.show();
                } catch (Exception e) {

                }
            }
        }
        tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRequestSend=true;
                dataFire.getDbRequests().child(dataFire.getUserID()).child(userIDvisited)
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataFire.getDbConnections().child(dataFire.getUserID()).child("matches").child(userIDvisited).child("Time").setValue(-1*System.currentTimeMillis());
                        dataFire.getDbConnections().child(userIDvisited).child("matches").child(dataFire.getUserID()).child("Time").setValue(-1*System.currentTimeMillis());

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

                        alertDialog.dismiss();
                    }
                });

            }
        });

        tvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataFire.getDbRequests().child(dataFire.getUserID()).child(userIDvisited)
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

    }


    private void dialogReportUser(){

        mViewInflatedialogReport = getLayoutInflater().inflate(R.layout.dialog_report_user,null);
        LinearLayout llInappropPhotos = mViewInflatedialogReport.findViewById(R.id.llInappropPhotos);
        LinearLayout llProfileSpam = mViewInflatedialogReport.findViewById(R.id.llProfileSpam);
        LinearLayout llReportBlockDialogCancel = mViewInflatedialogReport.findViewById(R.id.llReportBlockDialogCancel);
        final android.app.AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogReport, VideoCallActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(!this.isFinishing()) {

            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.setCancelable(true);
            alertDialog.show();
        }
        llInappropPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                //report inappropriate photos
                String keypush = dataFire.getDbRefReportInappropriatePhoto().push().getKey();
                dataFire.getDbRefReportInappropriatePhoto().child(keypush).child("userID").setValue(dataFire.getUserID());
                dataFire.getDbRefReportInappropriatePhoto().child(keypush).child("reportUserID").setValue(userIDvisited);
                dataFire.getDbRefReportInappropriatePhoto().child(keypush).child("time").setValue(System.currentTimeMillis()*-1);

            }
        });

        llProfileSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                //report spam
                String keypush = dataFire.getDbRefReportSpam().push().getKey();
                dataFire.getDbRefReportSpam().child(keypush).child("userID").setValue(dataFire.getUserID());
                dataFire.getDbRefReportSpam().child(keypush).child("reportUserID").setValue(userIDvisited);
                dataFire.getDbRefReportSpam().child(keypush).child("time").setValue(System.currentTimeMillis()*-1);

            }
        });

        llReportBlockDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }



    private void widgets() {

        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.fragment_videocall_container);

        imgvPicUserVidcall = findViewById(R.id.imgv_pic_user_vidcall);
        mSwitchCameraBtn = findViewById(R.id.iv_switch_camera_video_call_activity);
        imgvRaportChatVideo = findViewById(R.id.iv_call_report);

        // Sample logs are optional.
        showSampleLogs();

        // image view
        ivVideoCallChat = findViewById(R.id.iv_video_call_chat);
        imgCloseEdtMessage = findViewById(R.id.imgCloseEdtMessage);
        ivExitVicall = findViewById(R.id.imgv_exit_vicall);
        ivAddFriend = findViewById(R.id.iv_add_friend);
        ivVideoCallGift=findViewById(R.id.iv_video_call_gift);

        // linear layout
        llDiscoverInputMessage = findViewById(R.id.linearlayout_discover_input_message);

        // edit text
        edtDiscoverInputMessage = findViewById(R.id.edittext_discover_video_input_message);


        //animation
        vSendGiftSuccess=findViewById(R.id.v_send_video_gift_success);

        //rcv
        rvVideoChatMessages=findViewById(R.id.rv_video_chat_messages);
        rvVideoChatMessages.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(VideoCallActivity.this,LinearLayoutManager.VERTICAL,false);
        mChatLayoutManager.setStackFromEnd(true);
        rvVideoChatMessages.setLayoutManager(mChatLayoutManager);
    }



    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("onJoinChannelSuccess","Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("onFirstRemoteVideo","First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("onUserOffline","User offline, uid: " + (uid & 0xFFFFFFFFL));
                    onRemoteUserLeft();
                }
            });
        }
    };


    private void startRandomSearch(){

                Intent intent  = new Intent(VideoCallActivity.this,SearchRandomActivity.class); // need to set your Intent View here
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

    }


    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        int count = mRemoteContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = mRemoteContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);
    }

    private void onRemoteUserLeft() {
        removeRemoteVideo();
        endCall();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        mRemoteView = null;
    }

    private void showSampleLogs() {
        Log.i("showSampleLogs","Welcome to Agora 1v1 video call");
        Log.i("showSampleLogs","You will see custom logs here");
        Log.i("showSampleLogs","You can also use this to show errors");
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }


    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        //Toast.makeText(this, video_room_id, Toast.LENGTH_SHORT).show();
        mRtcEngine.joinChannel(token, video_room_id, "Extra Optional Data", 0);
    }

    private void frChat(){

        firebaseRecyclerAdapterChats = new FirebaseRecyclerAdapter<ChatObject, ChatVoiceViewHolders>(
                ChatObject.class,
                R.layout.item_messages_video_call,
                ChatVoiceViewHolders.class,
                dbMessagingMy
        ) {

            @Override
            protected void populateViewHolder(final ChatVoiceViewHolders viewHolder, final ChatObject model, int position) {

                final String listPostKey = getRef(position).getKey();
                dbMessagingMy.child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String message  = String.valueOf(dataSnapshot.child("message").getValue());
                        final String from = String.valueOf(dataSnapshot.child("from").getValue());
                        getDataMessage(viewHolder,from,message);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


        };

        rvVideoChatMessages.setAdapter(firebaseRecyclerAdapterChats);
        rvVideoChatMessages.getLayoutManager().scrollToPosition(rvVideoChatMessages.getAdapter().getItemCount());
        firebaseRecyclerAdapterChats.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = firebaseRecyclerAdapterChats.getItemCount();
                int lastVisiblePosition =
                        mChatLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    rvVideoChatMessages.scrollToPosition(positionStart);
                }
            }
        });

    }

    private void getDataMessage(final ChatVoiceViewHolders holder, String from, String message) {


        if (from.equals(dataFire.getUserID())) {

            holder.lyMessageOutText.setVisibility(View.VISIBLE);
            holder.lyMessageInText.setVisibility(View.GONE);
            holder.tvOutMessageTextChat.setText(message);

        } else {
            dataFire.getDbRefUsers().child(from).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Picasso.get().load(String.valueOf(dataSnapshot.child("photoProfile").getValue()))
                            .placeholder(R.drawable.icon_register_select_header).into(holder.imgVProfilePicChatVoice);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            holder.lyMessageInText.setVisibility(View.VISIBLE);
            holder.lyMessageOutText.setVisibility(View.GONE);
            holder.tvInMessageTextChat.setText(message);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        frChat();

        dataFire.getDbSendGift().child(dataFire.getUserID()).child(userIDvisited).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("link_json")){
                    String link = String.valueOf(dataSnapshot.child("link_json").getValue());
                    vSendGiftSuccess.setVisibility(View.VISIBLE);
                    vSendGiftSuccess.setAnimationFromUrl(link);
                    vSendGiftSuccess.playAnimation();

                    vSendGiftSuccess.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            animation.cancel();
                            dataFire.getDbSendGift().child(dataFire.getUserID()).child(userIDvisited).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            vSendGiftSuccess.setVisibility(View.GONE);
                                            animation.removeAllListeners();
                                        }
                                    });
                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataFire.getDbConnections().child(dataFire.getUserID()).child("matches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userIDvisited)){
                    ivAddFriend.setImageDrawable(getDrawable(R.drawable.discover_auto_friend));
                    ivAddFriend.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataFire.getDbRequests().child(dataFire.getUserID()).child(userIDvisited).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dialogRequestFriend();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
          endCall();
        RtcEngine.destroy();

    }

    private void leaveChannel() {
        dataFire.getDbRefMsgCall().child(dataFire.getUserID()).child(userIDvisited).removeValue();
        dataFire.getDbRefMsgCall().child(userIDvisited).child(dataFire.getUserID()).removeValue();
        if(mRtcEngine!=null)
        mRtcEngine.leaveChannel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endCall();
        RtcEngine.destroy();
    }


    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    private void sendMessage(String message){

        DatabaseReference dbrefMyChatsend = dbMessagingMy.push();
        String keypuch = dbrefMyChatsend.getKey();
        DatabaseReference dbrefHIschattsend = dbMessagingHis.child(keypuch);

        // send to my messages
        dbrefMyChatsend.child("from").setValue(dataFire.getUserID());
        dbrefMyChatsend.child("message").setValue(message);

        // send to user chat
        dbrefHIschattsend.child("from").setValue(dataFire.getUserID());
        dbrefHIschattsend.child("message").setValue(message);

        edtDiscoverInputMessage.setText("");


        rvVideoChatMessages.smoothScrollToPosition(rvVideoChatMessages.getAdapter().getItemCount());

        firebaseRecyclerAdapterChats.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseRecyclerAdapterChats.getItemCount();
                int lastVisiblePosition =
                        mChatLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    rvVideoChatMessages.scrollToPosition(positionStart);
                }
            }
        });

    }
    

    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);

        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    private void endCall() {

        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
        if(SearchRandomActivity.strcheckOption!=null) {
            startRandomSearch();
        }
        else
        {
            Intent intent  = new Intent(VideoCallActivity.this, MainActivity.class); // need to set your Intent View here
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            VideoCallActivity.this.finish();
            dataFire.getDbRefVideoCall().child(dataFire.getUserID()).removeValue();
            dataFire.getDbRefVideoCall().child(userIDvisited).removeValue();
        }

    }

    private void removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView = null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onButtonClicked(String text) {

    }
}
