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
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class VoiceCallActivity extends AppCompatActivity implements BSDChooseGift.BottomSheetListener {

    private static final String LOG_TAG = VoiceCallActivity.class.getSimpleName();
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private RtcEngine mRtcEngine;
    public static String userIDvisited;
    private String voice_room_id;
    private ImageView imgvEndCall,imgvAddUser;
    private TextView tvNameAgeGenderVoiceCall;
    private float total = 0f;
    private TextView tvTimerVoiceCall;
    private static final int MY_CAMERA_PERMISSION_CODE = 500;
    private static final int CAMERA_REQUEST = 600;
    long starttime = 0;
    private boolean isRequestSend=false;


    //this  posts a message to the main thread from our timertask
    //and updates the textfield
    final Handler h = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            long millis = System.currentTimeMillis() - starttime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;

            tvTimerVoiceCall.setText(String.format("%d:%02d", minutes, seconds));
            return false;
        }

    });
    //runs without timer be reposting self
    Handler h2 = new Handler();
    Runnable run = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - starttime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;

            tvTimerVoiceCall.setText(String.format("%d:%02d", minutes, seconds));

            h2.postDelayed(this, 500);

        }
    };

    private CircleImageView civ_voice_call_avatar;
    private View mViewInflatedialogRequestFriend;
    private ImageView imgCloseEdtMessage;
    private LinearLayout linearlayout_discover_input_message;
    private EditText edittext_discover_input_message;
    private ImageView iv_voice_call_chat;
    private InputMethodManager inputManager;
    private RecyclerView rv_chat_messages_Voice;
    private LottieAnimationView v_send_gift_success;
    private LinearLayoutManager mChatLayoutManager;
    private FirebaseRecyclerAdapter<ChatObject, ChatVoiceViewHolders> firebaseRecyclerAdapterChats;
    private DatabaseReference dbMessagingHis;
    private DatabaseReference dbMessagingMy;
    private ImageView iv_voice_call_gift;
    private ImageView iv_voice_call_report;
    private View mViewInflatedialogReport;

    @Override
    public void onButtonClicked(String text) {

    }

    //tells handler to send a message
    class firstTask extends TimerTask {

        @Override
        public void run() {
            h.sendEmptyMessage(0);
        }
    }

    //tells activity to run on ui thread
    class secondTask extends TimerTask {

        @Override
        public void run() {
            VoiceCallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    long millis = System.currentTimeMillis() - starttime;
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds     = seconds % 60;

                    tvTimerVoiceCall.setText(String.format("%d:%02d", minutes, seconds));

                }
            });
        }
    }

    Timer timer = new Timer();

    private DataFire dataFire; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);
        dataFire=new DataFire();
        widgets();
        voice_room_id = getIntent().getStringExtra("voice_room_id");

        userIDvisited = getIntent().getStringExtra("userIDvisited");

        //decrease coins when you change country
        decreaseChangeCountry();
        //decrease coins when choose girl
        decreaseChooseGirl();

        //increase coins when your first call voice
        firstVoiceCall();


        dbMessagingMy = dataFire.getDbRefMsgCall().child(dataFire.getUserID()).child(userIDvisited);
        dbMessagingHis = dataFire.getDbRefMsgCall().child(userIDvisited).child(dataFire.getUserID());

        starttime = System.currentTimeMillis();
        timer = new Timer();
        timer.schedule(new firstTask(), 0,500);
        timer.schedule(new secondTask(),  0,500);
        h2.postDelayed(run, 0);

        iv_voice_call_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReportUser();
            }
        });
        
        iv_voice_call_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSDChooseGift bSDChooseGift = new BSDChooseGift();
                bSDChooseGift.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        iv_voice_call_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearlayout_discover_input_message.setVisibility(View.VISIBLE);

            }
        });

        imgCloseEdtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearlayout_discover_input_message.setVisibility(View.GONE);
            }
        });

        edittext_discover_input_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    String txt = edittext_discover_input_message.getText().toString();
                    if (!txt.isEmpty())
                        sendMessage(txt);
                    linearlayout_discover_input_message.setVisibility(View.GONE);
                    closeKeyboard();
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });

        imgvEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    leaveChannel();
                dataFire.getDbRefSearch().child(dataFire.getUserID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent  = new Intent(VoiceCallActivity.this,SearchRandomActivity.class);
                        // need to set your Intent View here
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });



        imgvAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRequestSend==false) {
                    dataFire.getDbRequests().child(userIDvisited).child(dataFire.getUserID()).child("send_request").setValue("true");
                    Toast.makeText(VoiceCallActivity.this, R.string.str_send_reau_seccss, Toast.LENGTH_SHORT).show();
                    isRequestSend=true;
                }
            }
        });

        dataFire.getDbRefUsers().child(userIDvisited).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = String.valueOf(dataSnapshot.child("username").getValue());
                String age = String.valueOf(dataSnapshot.child("age").getValue());
                String sex = String.valueOf(dataSnapshot.child("gender").getValue());
                String image = String.valueOf(dataSnapshot.child("photoProfile").getValue());

                tvNameAgeGenderVoiceCall.setText(username+", "+age);

                if(sex.equals("guy")){
                    tvNameAgeGenderVoiceCall.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_register_male_selected, 0, 0, 0);
                }else{
                    tvNameAgeGenderVoiceCall.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_register_female_selected, 0, 0, 0);
                }
                Picasso.get().load(image).placeholder(R.drawable.icon_register_select_header).into(civ_voice_call_avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }

        dataFire.getDbHistory().child(dataFire.getUserID()).child(userIDvisited).child("Time").setValue((System.currentTimeMillis()/1000)*-1)
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

    private void firstVoiceCall() {
        //first voice call
        dataFire.getDbRefUsers().child(dataFire.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild("gems") && snapshot.hasChild("first_voice_calls")) {

                            String gems = String.valueOf(snapshot.child("gems").getValue());
                            String first_voice_calls = String.valueOf(snapshot.child("first_voice_calls").getValue());

                            if (first_voice_calls.equals("false")) {
                                dataFire.getDbRefUsers().child(dataFire.getUserID())
                                        .child("gems")
                                        .setValue(Integer.parseInt(gems) + Integer.parseInt(getString(R.string.earn_3_voice_call)));
                                dataFire.getDbRefUsers().child(dataFire.getUserID())
                                        .child("first_voice_calls")
                                        .setValue("true");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    private void decreaseChangeCountry(){
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


    private void dialogReportUser(){

        mViewInflatedialogReport = getLayoutInflater().inflate(R.layout.dialog_report_user,null);
        LinearLayout llInappropPhotos = mViewInflatedialogReport.findViewById(R.id.llInappropPhotos);
        LinearLayout llProfileSpam = mViewInflatedialogReport.findViewById(R.id.llProfileSpam);
        LinearLayout llReportBlockDialogCancel = mViewInflatedialogReport.findViewById(R.id.llReportBlockDialogCancel);
        final android.app.AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogReport, VoiceCallActivity.this);
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
                dataFire.getDbRefReportInappropriatePhoto().child(keypush).child("time").setValue((System.currentTimeMillis()/1000)*-1);

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
                dataFire.getDbRefReportSpam().child(keypush).child("time").setValue((System.currentTimeMillis()/1000)*-1);

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
        tvNameAgeGenderVoiceCall=findViewById(R.id.tv_voice_call_name);
        imgvEndCall=findViewById(R.id.iv_voice_call_exit);
        imgvAddUser=findViewById(R.id.iv_voice_call_addfriend);
        tvTimerVoiceCall=findViewById(R.id.tv_voice_call_duration);
        civ_voice_call_avatar=findViewById(R.id.civ_voice_call_avatar);
        iv_voice_call_chat=findViewById(R.id.iv_voice_call_chat);
        iv_voice_call_gift=findViewById(R.id.iv_voice_call_gift);
        iv_voice_call_report=findViewById(R.id.iv_voice_call_report);

        imgCloseEdtMessage = findViewById(R.id.imgCloseEdtMessageVoice);

        // linear layout
        linearlayout_discover_input_message = findViewById(R.id.linearlayout_discover_input_messageVoice);

        // edit text
        edittext_discover_input_message = findViewById(R.id.edittext_discover_input_messageVoice);
        
        //animation
        v_send_gift_success=findViewById(R.id.v_send_gift_success);
        
        //rcv
        rv_chat_messages_Voice=findViewById(R.id.rv_chat_messages_Voice);
        rv_chat_messages_Voice.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(VoiceCallActivity.this,LinearLayoutManager.VERTICAL,false);
        mChatLayoutManager.setStackFromEnd(true);
        rv_chat_messages_Voice.setLayoutManager(mChatLayoutManager);
    }


    private void closeKeyboard() {
        inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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

        edittext_discover_input_message.setText("");


        rv_chat_messages_Voice.smoothScrollToPosition(rv_chat_messages_Voice.getAdapter().getItemCount());

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
                    rv_chat_messages_Voice.scrollToPosition(positionStart);
                }
            }
        });

    }
    
    private void dialogRequestFriend(){

        mViewInflatedialogRequestFriend= getLayoutInflater().inflate(R.layout.dialog_friend_request,null);
        TextView tvAccept = mViewInflatedialogRequestFriend.findViewById(R.id.tv_dialog_fr_confirm);
        TextView tvDecline = mViewInflatedialogRequestFriend.findViewById(R.id.tv_dialog_fr_cancel);
        final android.app.AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogRequestFriend
                , VoiceCallActivity.this);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(!this.isFinishing()) {

            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

                alertDialog.setCancelable(true);
                alertDialog.show();
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
                        dataFire.getDbConnections().child(dataFire.getUserID()).child("matches").child(userIDvisited).child("Time").setValue(-1*(System.currentTimeMillis()/1000));
                        dataFire.getDbConnections().child(userIDvisited).child("matches").child(dataFire.getUserID()).child("Time").setValue(-1*(System.currentTimeMillis()/1000));

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


    private void frChat(){

        firebaseRecyclerAdapterChats = new FirebaseRecyclerAdapter<ChatObject, ChatVoiceViewHolders>(
                ChatObject.class,
                R.layout.item_messages_voice_call,
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

        rv_chat_messages_Voice.setAdapter(firebaseRecyclerAdapterChats);
        rv_chat_messages_Voice.getLayoutManager().scrollToPosition(rv_chat_messages_Voice.getAdapter().getItemCount());
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
                    rv_chat_messages_Voice.scrollToPosition(positionStart);
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
                    v_send_gift_success.setVisibility(View.VISIBLE);
                    v_send_gift_success.setAnimationFromUrl(link);
                    v_send_gift_success.playAnimation();

                    v_send_gift_success.addAnimatorListener(new Animator.AnimatorListener() {
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
                                    v_send_gift_success.setVisibility(View.GONE);
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
                    imgvAddUser.setImageDrawable(getDrawable(R.drawable.discover_auto_friend));
                    imgvAddUser.setEnabled(false);
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

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                }
            });
        }


        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }
    };


    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        joinChannel();               // Tutorial Step 2
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {

                    finish();
                }
                break;
            }
        }
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

    // Tutorial Step 5
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


    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            // Sets the channel profile of the Agora RtcEngine.
            // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
            // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannel() {
        String accessToken = getString(R.string.agora_access_token);
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
            accessToken = null; // default, no token
        }

        // Allows a user to join a channel.
        mRtcEngine.joinChannel(accessToken,voice_room_id , "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 3
    private void leaveChannel() {
        dataFire.getDbRefMsgCall().child(dataFire.getUserID()).child(userIDvisited).removeValue();
        dataFire.getDbRefMsgCall().child(userIDvisited).child(dataFire.getUserID()).removeValue();
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
            leaveChannel();
        dataFire.getDbRefSearch().child(dataFire.getUserID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent  = new Intent(VoiceCallActivity.this,SearchRandomActivity.class);
                // need to set your Intent View here
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

            leaveChannel();

        dataFire.getDbRefSearch().child(dataFire.getUserID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent  = new Intent(VoiceCallActivity.this,SearchRandomActivity.class);
                // need to set your Intent View here
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        
    }
}
