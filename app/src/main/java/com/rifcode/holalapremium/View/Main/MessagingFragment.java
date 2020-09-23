package com.rifcode.holalapremium.View.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Models.CardMatches;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Chat.ChatActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class MessagingFragment extends Fragment{

    private View view;
    private LinearLayout llChatNearbyEntrance;
    private TextView tvTryNowNewMatches;
    private RecyclerView rvNewFriends;
    private ImageView imagoVideoNewMatches;
    private LinearLayoutManager mLayoutManagercNewFriends;
    private String mauserID;
    private FirebaseUser mAuth;
    private FirebaseRecyclerAdapter<CardMatches,usersNewMatchesViewHolder> firebaseRecyclerAdapterNewMatches;
    private EditText etChatSmsSearch;
    private FirebaseRecyclerAdapter<CardMatches,messagesHolderView> firebaseRecyclerAdapterMessages;
    private RecyclerView rcMessages;
    private LinearLayoutManager mLayoutManagercNewMessages;
    private DataFire dataFire;
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    private String userKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_messaging, container, false);
        dataFire=new DataFire();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mauserID = mAuth.getUid();
        widgets();




                llChatNearbyEntrance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), NearbyActivity.class));
                    }
                });

        tvTryNowNewMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.viewPager.setCurrentItem(0);
            }
        });

        etChatSmsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final Query qqs = dataFire.getDbConnections().child(mauserID).child("matches").orderByChild("username").startAt(s.toString());
                frNewFriends(qqs);
                if(s.length()==0){
                    final Query qq = dataFire.getDbConnections().child(mauserID).child("matches").orderByChild("Time").limitToLast(20);
                    frNewFriends(qq);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkMyNewFriends();
        frNewMessages();
    }

    private void widgets() {
        llChatNearbyEntrance=view.findViewById(R.id.ll_chat_nearby_entrance);
        tvTryNowNewMatches =view.findViewById(R.id.tvTryNowNewMatchs);

        //rcv new friends
        rvNewFriends=view.findViewById(R.id.rvNewFriends);
        rvNewFriends.setHasFixedSize(true);
        mLayoutManagercNewFriends = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rvNewFriends.setLayoutManager(mLayoutManagercNewFriends);


        // rcv new messages
        rcMessages = view.findViewById(R.id.rcvChatMessages);
        rcMessages.setHasFixedSize(true);
        mLayoutManagercNewMessages = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rcMessages.setLayoutManager(mLayoutManagercNewMessages);

        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getActivity(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(getActivity(), "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView

                //chat
                dataFire.getDbRefChat().child(mauserID).child(userKey).removeValue();
                //messages
                dataFire.getDbRefMessages().child(mauserID).child(userKey).removeValue();

                Toast.makeText(getActivity(), R.string.char_remove_secc, Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rcMessages);


        imagoVideoNewMatches =view.findViewById(R.id.imgvVideNewMatchs);
        etChatSmsSearch=view.findViewById(R.id.et_chat_sms_search);

    }

    private void checkMyNewFriends() {
        DatabaseReference dbConnectionMatches = dataFire.getDbConnections().child(mauserID).child("matches");
        dbConnectionMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    rvNewFriends.setVisibility(View.VISIBLE);
                    imagoVideoNewMatches.setVisibility(View.GONE);
                    final Query qq = dataFire.getDbConnections().child(mauserID).child("matches").orderByChild("Time").limitToLast(20);
                    frNewFriends(qq);
                }else{
                    rvNewFriends.setVisibility(View.GONE);
                    imagoVideoNewMatches.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void frNewFriends(Query qrMyNewMatches){

        firebaseRecyclerAdapterNewMatches = new FirebaseRecyclerAdapter<CardMatches, usersNewMatchesViewHolder>(
                CardMatches.class,
                R.layout.layout_new_friends,
                usersNewMatchesViewHolder.class,
                qrMyNewMatches
        ) {

            @Override
            protected void populateViewHolder(final usersNewMatchesViewHolder viewHolder, final CardMatches model, int position) {

                final String listPostKey = getRef(position).getKey();
                userKey=listPostKey;
                dataFire.getDbRefUsers().child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String thumb_picture = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                        String username = String.valueOf(dataSnapshot.child("username").getValue());
                        String age = String.valueOf(dataSnapshot.child("age").getValue());
                        String gender = String.valueOf(dataSnapshot.child("gender").getValue());
                        viewHolder.setImageProfileNewMatches(getContext(),gender,thumb_picture);
                        viewHolder.tvusername.setText(username+", "+age);
                        if(gender.equals("guy")){
                            viewHolder.tvusername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_common_male_selected, 0);
                            viewHolder.tvusername.setCompoundDrawablesWithIntrinsicBounds( R.drawable.icon_common_male_selected, 0,0, 0);

                        }else{
                            viewHolder.tvusername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_common_female_selected, 0);
                            viewHolder.tvusername.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_common_female_selected, 0, 0, 0);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.imgvUserrcNewMatches.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatActivity.chatActivity="false";
                        Intent profileintent = new Intent(getContext(), UserProfileActivity.class);
                        profileintent.putExtra("userIDvisited",listPostKey);
                        getContext().startActivity(profileintent);
                    }
                });

            }
        };

        rvNewFriends.setAdapter(firebaseRecyclerAdapterNewMatches);

    }

    public static class usersNewMatchesViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgvUserrcNewMatches;
        TextView tvusername;

        public usersNewMatchesViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            imgvUserrcNewMatches = view.findViewById(R.id.imgvUserNewMatches);
            tvusername = view.findViewById(R.id.tvUsernameNewMatches);
        }


        public void setImageProfileNewMatches(final Context ct, final String sex, final String imageUri){

                Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.icon_register_select_header).into(imgvUserrcNewMatches, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(imageUri).placeholder(R.drawable.icon_register_select_header)
                                .into(imgvUserrcNewMatches);
                    }
                });
        }

    }


    private void frNewMessages(){

        final Query qrNewMessages = FirebaseDatabase.getInstance().getReference().child("chat").child(mauserID).orderByChild("Time").limitToLast(10);
        firebaseRecyclerAdapterMessages = new FirebaseRecyclerAdapter<CardMatches, messagesHolderView>(
                CardMatches.class,
                R.layout.layout_item_chat,
                messagesHolderView.class,
                qrNewMessages
        ) {

            @Override
            protected void populateViewHolder(final messagesHolderView viewHolder, final CardMatches model, int position) {

                final String listPostKey = getRef(position).getKey();



                dataFire.getDbRefUsers().child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String thumb_picture = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                        String username = String.valueOf(dataSnapshot.child("username").getValue());
                        viewHolder.setImageProfileNewMessages(thumb_picture);
                        viewHolder.tvusername.setText(username);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Query messagesQuery = dataFire.getDbRefMessages().child(mauserID).child(listPostKey).limitToLast(1);

                messagesQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String message = String.valueOf(dataSnapshot.child("message").getValue());
                        String timestamp = String.valueOf(dataSnapshot.child("msgTimeAgo").getValue());
                        viewHolder.tv_chat_msg_content.setText(message);

                        try {
                            long l = Long.parseLong(timestamp); // mills
                            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(l);
                            viewHolder.tv_chat_msg_time.setText(formatter.format(calendar.getTime()));
                        }catch (Exception e){

                        }

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

                viewHolder.lyMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileintent = new Intent(getContext(), ChatActivity.class);
                        profileintent.putExtra("userIDvisited",listPostKey);
                        getContext().startActivity(profileintent);
                        Animatoo.animateSlideLeft(getContext());
                    }
                });


            }
        };
        rcMessages.setAdapter(firebaseRecyclerAdapterMessages);


    }


    public static class messagesHolderView extends RecyclerView.ViewHolder {

        View view;
        ImageView imgvUserrcNewmessages;
        TextView tvusername;
        TextView tv_chat_msg_time,tv_chat_msg_content;
        LinearLayout lyMessage;

        public messagesHolderView(View itemView) {
            super(itemView);

            view = itemView;
            imgvUserrcNewmessages = view.findViewById(R.id.imgvUserMessages);
            tvusername = view.findViewById(R.id.tvUsernameMessages);
            tv_chat_msg_time = view.findViewById(R.id.tv_chat_msg_time);
            tv_chat_msg_content = view.findViewById(R.id.tv_chat_msg_content);
            lyMessage = view.findViewById(R.id.lyMessage);

        }


        public void setImageProfileNewMessages(final String imageUri){
            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.icon_register_select_header).into(imgvUserrcNewmessages, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.icon_register_select_header)
                            .into(imgvUserrcNewmessages);
                }
            });
        }

    }




}
