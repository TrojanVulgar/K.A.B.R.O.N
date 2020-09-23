package com.rifcode.holalapremium.View.Chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Adapter.ChatViewHolders;
import com.rifcode.holalapremium.Models.ChatObject;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.DialogUtils;
import com.rifcode.holalapremium.View.Main.UserProfileActivity;
import com.rifcode.holalapremium.View.Random.SearchRandomActivity;
import com.rifcode.holalapremium.View.Random.VideoCallActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView civChatMessageTitleAvatar;
    private ImageView ivChatMessageTitleBack,ivChatMessageTitleMore
            ,ivChatMsgInpuVideo,ivChatMsgSend;
    private EditText edtTextMsg;
    private RecyclerView recycle_chat_messages;
    private DataFire dataFire;
    private String userID;
    DatabaseReference  dbrefMyChat,dbrefHischat,dbMessagingMy,dbMessagingHis;
    private String myuserid;
    private FirebaseRecyclerAdapter<ChatObject, ChatViewHolders> firebaseRecyclerAdapterChats;
    private LinearLayoutManager mChatLayoutManager;
    public static String chatActivity;
    private ViewStub menuChatMessageMore;
    private boolean menuOpened=false;
    private View mViewInflatedialogReport;
    private LinearLayout llChatMessageUnmatch,llChatMessageReport;
    private View mViewInflatedialogUnmatch;
    private DatabaseReference dbrefnotification,dbrefnotificationstate;
    private AdView adAdmobBannerChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        dataFire =new DataFire();
        widgets();

        userID = getIntent().getStringExtra("userIDvisited");
        myuserid = dataFire.getUserID();

        if(SearchRandomActivity.strcheckOption!=null){
            SearchRandomActivity.strcheckOption=null;
        }

        dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("purchase")){
                    String purchase = String.valueOf(snapshot.child("purchase").getValue());
                    if(purchase.equals("true")){
                        adAdmobBannerChat.setVisibility(View.GONE);
                    }else{
                        adAdmobBannerChat.setVisibility(View.VISIBLE);
                        // admob ads banner
                        AdRequest adRequestbanner = new AdRequest.Builder().build();
                        adAdmobBannerChat.loadAd(adRequestbanner);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        
        dbrefMyChat = dataFire.getDbRefChat().child(myuserid).child(userID);
        dbrefHischat = dataFire.getDbRefChat().child(userID).child(myuserid);
        dbMessagingMy = dataFire.getDbRefMessages().child(myuserid).child(userID);
        dbMessagingHis = dataFire.getDbRefMessages().child(userID).child(myuserid);

        dataFire.getDbRefUsers().child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String photoProfile = dataSnapshot.child("photoProfile").getValue().toString();
                Picasso.get().load(photoProfile).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.icon_register_select_header).into(civChatMessageTitleAvatar, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(photoProfile).placeholder(R.drawable.icon_register_select_header)
                                .into(civChatMessageTitleAvatar);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        ivChatMsgInpuVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for notification video call
                getrefNotificationState().child(userID)
                        .child(myuserid).child("state").setValue("voice_call");

                DatabaseReference notifRef = getdbrefnotification().child(userID).push();
                notifRef.child("From").setValue(myuserid);

                onSendVideoCall();
            }
        });

        ivChatMessageTitleMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!menuOpened) {
                    menuChatMessageMore.setVisibility(View.VISIBLE);
                    menuOpened=true;
                }else{
                    menuChatMessageMore.setVisibility(View.GONE);
                    menuOpened=false;
                }
            }
        });

        llChatMessageReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuChatMessageMore.setVisibility(View.GONE);
                menuOpened=false;
                dialogReportUser();
            }
        });

        llChatMessageUnmatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuChatMessageMore.setVisibility(View.GONE);
                menuOpened=false;
                dialogUnmatch();
            }
        });

        civChatMessageTitleAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.chatActivity="true";
                Intent profileintent = new Intent(ChatActivity.this, UserProfileActivity.class);
                profileintent.putExtra("userIDvisited",userID);
                startActivity(profileintent);
            }
        });

        ivChatMessageTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtTextMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    ivChatMsgSend.setAlpha(1f);
                }else{
                    ivChatMsgSend.setAlpha(0.5f);
                }
            }
        });

        ivChatMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = edtTextMsg.getText().toString();
                if (!txt.isEmpty())
                    sendMessage(txt,"text");
            }
        });



    }

    private void sendMessage(String message, String type){

        DatabaseReference dbrefMyChatsend = dbMessagingMy.push();
        String keypuch = dbrefMyChatsend.getKey();
        DatabaseReference dbrefHIschattsend = dbMessagingHis.child(keypuch);

        // send to my messages
        dbrefMyChatsend.child("from").setValue(myuserid);
        dbrefMyChatsend.child("message").setValue(message);
        dbrefMyChatsend.child("msgTimeAgo").setValue(ServerValue.TIMESTAMP);
        dbrefMyChatsend.child("type").setValue(type);
        dbrefMyChatsend.child("seen").setValue("false");

        // send to user chat
        dbrefHIschattsend.child("from").setValue(myuserid);
        dbrefHIschattsend.child("message").setValue(message);
        dbrefHIschattsend.child("msgTimeAgo").setValue(ServerValue.TIMESTAMP);
        dbrefHIschattsend.child("type").setValue(type);

        dbrefHischat.child("Time").setValue(-1*(System.currentTimeMillis()/1000));
        dbrefMyChat.child("Time").setValue(-1*(System.currentTimeMillis()/1000));


        DatabaseReference dbrefreviewsmsg = dataFire.getDbRefMessagesReview().child(keypuch);
        dbrefreviewsmsg.child("from").setValue(myuserid);
        dbrefreviewsmsg.child("to").setValue(userID);
        dbrefreviewsmsg.child("message").setValue(message);
        dbrefreviewsmsg.child("time").setValue(-1*(System.currentTimeMillis()/1000));
        dbrefreviewsmsg.child("type").setValue(type);

        // for notification messages
        getrefNotificationState().child(userID)
                .child(myuserid).child("state").setValue("message");

        DatabaseReference notifRef = getdbrefnotification().child(userID).push();
        notifRef.child("From").setValue(myuserid);


        edtTextMsg.setText("");


        recycle_chat_messages.smoothScrollToPosition(recycle_chat_messages.getAdapter().getItemCount());

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
                    recycle_chat_messages.scrollToPosition(positionStart);
                }
            }
        });

    }
    
    private void widgets() {

        //ads banner
        adAdmobBannerChat = findViewById(R.id.adAdmobBannerChat);

        edtTextMsg=findViewById(R.id.edtTextMessage);
        civChatMessageTitleAvatar=findViewById(R.id.civ_chat_message_title_avatar);
        menuChatMessageMore=findViewById(R.id.menu_chat_message_more);
        menuChatMessageMore.inflate();
        menuChatMessageMore.setVisibility(View.GONE);
        //imageview
        ivChatMsgSend = findViewById(R.id.iv_chat_msg_send);
        ivChatMessageTitleMore = findViewById(R.id.iv_chat_message_title_more);
        ivChatMsgInpuVideo = findViewById(R.id.iv_chat_msg_inpu_video);
        ivChatMessageTitleBack=findViewById(R.id.iv_chat_message_title_back);

        //ll
        llChatMessageReport = findViewById(R.id.ll_chat_message_report);
        llChatMessageUnmatch = findViewById(R.id.ll_chat_message_unmatch);

        //rcv
        recycle_chat_messages=findViewById(R.id.recycle_chat_messages);
        recycle_chat_messages.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this,LinearLayoutManager.VERTICAL,false);
        mChatLayoutManager.setStackFromEnd(true);
        recycle_chat_messages.setLayoutManager(mChatLayoutManager);
    }


    public  DatabaseReference getdbrefnotification(){
        dbrefnotification = FirebaseDatabase.getInstance().getReference().child("Notifications");
        return dbrefnotification;
    }

    public  DatabaseReference getrefNotificationState(){
        dbrefnotificationstate = FirebaseDatabase.getInstance().getReference().child("notifications_state");
        return dbrefnotificationstate;
    }


    @Override
    protected void onStart() {
        super.onStart();

        frChat();

        dataFire.getDbRefVideoCall().child(dataFire.getUserID()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("room_id")){
                    String roomid = String.valueOf(dataSnapshot.child("room_id").getValue());
                    String status_request = String.valueOf(dataSnapshot.child("status_request").getValue());

                    if(status_request.equals("send")){
                        //activity
                        Intent reqvideointent = new Intent(ChatActivity.this, VideoCallAnswerActivity.class);
                        reqvideointent.putExtra("userIDvisited",userID);
                        startActivity(reqvideointent);
                    }else if(status_request.equals("accept")){
                        //activity
                        Intent videointent = new Intent(ChatActivity.this, VideoCallActivity.class);
                        videointent.putExtra("video_room_id",roomid);
                        videointent.putExtra("userIDvisited",userID);
                        startActivity(videointent);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void onSendVideoCall() {
        String key = dataFire.getDbRefVideoCall().child(userID).child(dataFire.getUserID()).push().getKey();
        dataFire.getDbRefVideoCall().child(userID).child(dataFire.getUserID()).child("room_id")
                .setValue(key).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChatActivity.this, R.string.vcss, Toast.LENGTH_SHORT).show();
                dataFire.getDbRefVideoCall().child(userID).child(dataFire.getUserID()).child("status_request").setValue("send");
            }
        });
    }


    private void dialogReportUser(){

        mViewInflatedialogReport = getLayoutInflater().inflate(R.layout.dialog_report_user,null);
        LinearLayout llInappropPhotos = mViewInflatedialogReport.findViewById(R.id.llInappropPhotos);
        LinearLayout llProfileSpam = mViewInflatedialogReport.findViewById(R.id.llProfileSpam);
        LinearLayout llReportBlockDialogCancel = mViewInflatedialogReport.findViewById(R.id.llReportBlockDialogCancel);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogReport, ChatActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setCancelable(true);
        alertDialog.show();

        llInappropPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                //report inappropriate photos
                String keypush = dataFire.getDbRefReportInappropriatePhoto().push().getKey();
                dataFire.getDbRefReportInappropriatePhoto().child(keypush).child("userID").setValue(myuserid);
                dataFire.getDbRefReportInappropriatePhoto().child(keypush).child("reportUserID").setValue(userID);
                dataFire.getDbRefReportInappropriatePhoto().child(keypush).child("time").setValue((System.currentTimeMillis()/1000)*-1);

            }
        });

        llProfileSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                //report spam
                String keypush = dataFire.getDbRefReportSpam().push().getKey();
                dataFire.getDbRefReportSpam().child(keypush).child("userID").setValue(myuserid);
                dataFire.getDbRefReportSpam().child(keypush).child("reportUserID").setValue(userID);
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


    private void dialogUnmatch(){

        mViewInflatedialogUnmatch = getLayoutInflater().inflate(R.layout.dialog_unmatch,null);
        TextView tv_dialog_unmatch_cancel = mViewInflatedialogUnmatch.findViewById(R.id.tv_dialog_unmatch_cancel);
        TextView tv_dialog_unmatch_confirm = mViewInflatedialogUnmatch.findViewById(R.id.tv_dialog_unmatch_confirm);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogUnmatch, ChatActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setCancelable(true);
        alertDialog.show();

        tv_dialog_unmatch_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //block and remove messages and chat
                //chat
                dataFire.getDbRefChat().child(myuserid).child(userID).removeValue();
                dataFire.getDbRefChat().child(userID).child(myuserid).removeValue();
                //messages
                dataFire.getDbRefMessages().child(myuserid).child(userID).removeValue();
                dataFire.getDbRefMessages().child(userID).child(myuserid).removeValue();
                //match
                dataFire.getDbConnections().child(myuserid).child("matches").child(userID).removeValue();
                dataFire.getDbConnections().child(userID).child("matches").child(myuserid).removeValue();
                //dislike
                dataFire.getDbConnections().child(userID)
                        .child("dislike")
                        .child(dataFire.getUserID()).child("Time")
                        .setValue(-1 * (System.currentTimeMillis()/1000));
            }
        });

        tv_dialog_unmatch_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }


    private void frChat(){

        firebaseRecyclerAdapterChats = new FirebaseRecyclerAdapter<ChatObject, ChatViewHolders>(
                ChatObject.class,
                R.layout.item_messages_chat,
                ChatViewHolders.class,
                dbMessagingMy
        ) {

            @Override
            protected void populateViewHolder(final ChatViewHolders viewHolder, final ChatObject model, int position) {

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

        recycle_chat_messages.setAdapter(firebaseRecyclerAdapterChats);
        recycle_chat_messages.getLayoutManager().scrollToPosition(recycle_chat_messages.getAdapter().getItemCount());
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
                    recycle_chat_messages.scrollToPosition(positionStart);
                }
            }
        });

    }

    private void getDataMessage(final ChatViewHolders holder, String from, String message) {


        if (from.equals(myuserid)) {

            holder.lyMessageOutText.setVisibility(View.VISIBLE);
                holder.lyMessageInText.setVisibility(View.GONE);
                holder.tvOutMessageTextChat.setText(message);

        } else {
                holder.lyMessageInText.setVisibility(View.VISIBLE);
                holder.lyMessageOutText.setVisibility(View.GONE);
                holder.tvInMessageTextChat.setText(message);
        }
    }


}
