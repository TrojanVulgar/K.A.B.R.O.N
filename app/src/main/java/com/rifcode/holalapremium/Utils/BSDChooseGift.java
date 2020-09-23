package com.rifcode.holalapremium.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Adapter.GiftViewHolders;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.Models.Gift;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Random.SearchRandomActivity;
import com.rifcode.holalapremium.View.Random.VideoCallActivity;
import com.rifcode.holalapremium.View.Random.VoiceCallActivity;
import com.squareup.picasso.Picasso;

public class BSDChooseGift extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private View v;
    private RecyclerView rcvGiftList;
    private GridLayoutManager mGLayoutManager;
    private FirebaseRecyclerAdapter<Gift, GiftViewHolders> firebaseRecyclerAdapter;
    private DataFire dataFire;
    private BillingProcessor bp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_gifts_choose, container, false);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.bottomSheetDialogTheme);
        dataFire=new DataFire();
        wedgets();

        bp = new BillingProcessor(getActivity(), getString(R.string.LICENSE_KEY), getString(R.string.MERCHANT_KEY)
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

       
        return v;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
    private void wedgets() {
        rcvGiftList=v.findViewById(R.id.rcv_gift_list);
        rcvGiftList.setHasFixedSize(true);
        mGLayoutManager = new GridLayoutManager(getActivity(),2,LinearLayoutManager.HORIZONTAL,false);
        rcvGiftList.setLayoutManager(mGLayoutManager);
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }


    @Override
    public void onStart() {
        super.onStart();
        Query qr = dataFire.getDbRefGifts().orderByChild("Time");
        fra(qr);
    }

    private void fra(Query qr){

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Gift, GiftViewHolders>(
                Gift.class,
                R.layout.item_gifts_list_layout,
                GiftViewHolders.class,
                qr
        ) {

            @Override
            protected void populateViewHolder(final GiftViewHolders viewHolder, final Gift model, int position) {

                final String listPostKey = getRef(position).getKey();

                dataFire.getDbRefGifts().child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        String image_preview = String.valueOf(dataSnapshot.child("image_preview").getValue());
                        final String link_json = String.valueOf(dataSnapshot.child("link_json").getValue());
                        final String gift_gems = String.valueOf(dataSnapshot.child("gift_gems").getValue());
                        String name = String.valueOf(dataSnapshot.child("name").getValue());

                        Picasso.get().load(image_preview).into(viewHolder.ivGiftIcon);
                        viewHolder.tvGiftName.setText(name);

                        if(!dataSnapshot.hasChild("gift_gems"))
                        {
                            viewHolder.tvGiftCoins.setText(R.string.free);
                        }else
                            if(dataSnapshot.hasChild("gift_gems") && Integer.parseInt(gift_gems)==0)
                            {
                                viewHolder.tvGiftCoins.setText(R.string.free);
                            }else{
                                viewHolder.tvGiftCoins.setText(gift_gems);
                            }

                        viewHolder.ivGiftIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(!dataSnapshot.hasChild("gift_gems"))
                                {
                                    sendGift(link_json);
                                }else
                                    if(Integer.parseInt(gift_gems)==0){
                                        sendGift(link_json);
                                    }else
                                if(Integer.parseInt(gift_gems) > 0) {
                                    dataFire.getDbRefUsers().child(dataFire.getUserID())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.hasChild("purchase") && snapshot.hasChild("gems")) {

                                                        String purchase = String.valueOf(snapshot.child("purchase").getValue());
                                                        String gems = String.valueOf(snapshot.child("gems").getValue());

                                                        if (purchase.equals("true") || Integer.parseInt(gems) >= Integer.parseInt(gift_gems)) {
                                                            sendGift(link_json,gift_gems);
                                                        } else {
                                                            DialogPurchase.dialog_purchase(dataFire.getmAuth(), bp, getActivity());
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                }

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        };
        rcvGiftList.setAdapter(firebaseRecyclerAdapter);
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
                mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }


    }

    private void sendGift(String linkjson, final String gift_gems){
        try {
            if (SearchRandomActivity.strcheckOption != null) {
                if (SearchRandomActivity.strcheckOption.equals("voice")) {

                    dataFire.getDbSendGift()
                            .child(VoiceCallActivity.userIDvisited)
                            .child(dataFire.getUserID())
                            .child("link_json").setValue(linkjson).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            try {
                                Toast.makeText(getContext(), getString(R.string.g_s_c), Toast.LENGTH_SHORT).show();
                                dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("gems")) {
                                            String gems = String.valueOf(dataSnapshot.child("gems").getValue());
                                            // purchase gems //
                                                dataFire.getDbRefUsers()
                                                        .child(dataFire.getUserID())
                                                        .child("gems")
                                                        .setValue(Integer.parseInt(gems)-Integer.parseInt(gift_gems));
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            } catch (Exception e) {
                            }
                        }
                    });

                } else
                    if(SearchRandomActivity.strcheckOption.equals("video"))
                    {
                        dataFire.getDbSendGift()
                                .child(VideoCallActivity.userIDvisited)
                                .child(dataFire.getUserID())
                                .child("link_json").setValue(linkjson).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                try {
                                    Toast.makeText(getContext(), getString(R.string.g_s_c), Toast.LENGTH_SHORT).show();
                                    dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild("gems")) {
                                                String gems = String.valueOf(dataSnapshot.child("gems").getValue());
                                                // purchase gems //
                                                dataFire.getDbRefUsers()
                                                        .child(dataFire.getUserID())
                                                        .child("gems")
                                                        .setValue(Integer.parseInt(gems)-Integer.parseInt(gift_gems));
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
            }
        }catch(Exception e){
        }

    }

    private void sendGift(String linkjson){
        try {
            if (SearchRandomActivity.strcheckOption != null) {
                if (SearchRandomActivity.strcheckOption.equals("voice")) {

                    dataFire.getDbSendGift()
                            .child(VoiceCallActivity.userIDvisited)
                            .child(dataFire.getUserID())
                            .child("link_json").setValue(linkjson).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            try {
                                Toast.makeText(getContext(), getString(R.string.g_s_c), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                            }
                        }
                    });

                } else
                if(SearchRandomActivity.strcheckOption.equals("video"))
                {
                    dataFire.getDbSendGift()
                            .child(VideoCallActivity.userIDvisited)
                            .child(dataFire.getUserID())
                            .child("link_json").setValue(linkjson).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            try {
                                Toast.makeText(getContext(), getString(R.string.g_s_c), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                            }
                        }
                    });
                }
            }
        }catch(Exception e){
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

}
