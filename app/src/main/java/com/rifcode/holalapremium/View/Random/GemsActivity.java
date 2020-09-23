package com.rifcode.holalapremium.View.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;

public class GemsActivity extends AppCompatActivity {

    private ImageView imgvGemsBack;
    private TextView tvCountMyGems;
    private LinearLayout ly100gems,ly500gems,ly1000gems,ly1month,ly3month,ly12months;
    private DataFire dataFire;
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gems);
        widgets();
        dataFire=new DataFire();
        dataFire.getDbRefUsers().child(dataFire.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("gems")) {
                    String gems = String.valueOf(dataSnapshot.child("gems").getValue());
                    tvCountMyGems.setText(gems);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        bp = new BillingProcessor(this, getString(R.string.LICENSE_KEY), getString(R.string.MERCHANT_KEY)
                ,new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull final String productId, @Nullable TransactionDetails details) {

                // check if subscribed
                if (bp.loadOwnedPurchasesFromGoogle() && bp.isSubscribed(productId)){
                    showToast(getString(R.string.subseccf));
                    // subscribed  //
                    dataFire.getDbRefUsers().child(dataFire.getUserID()).child("purchase").setValue("true");
                }


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
            }
            @Override
            public void onPurchaseHistoryRestored() {
            }
        });




        imgvGemsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ly100gems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bp.purchase(GemsActivity.this, getString(R.string.PRODUCT_ID_100_GEMS));

            }
        });
        ly500gems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(GemsActivity.this, getString(R.string.PRODUCT_ID_500_GEMS));

            }
        });
        ly1000gems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.purchase(GemsActivity.this, getString(R.string.PRODUCT_ID_1000_GEMS));

            }
        });

        ly1month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(GemsActivity.this, getString(R.string.SUBSCRIBE_ID_1_months));
            }
        });
        ly3month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(GemsActivity.this, getString(R.string.SUBSCRIBE_ID_3_months));
            }
        });
        ly12months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(GemsActivity.this, getString(R.string.SUBSCRIBE_ID_12_months));
            }
        });
    }

    private void widgets() {
        imgvGemsBack=findViewById(R.id.imgvGemsBack);
        tvCountMyGems=findViewById(R.id.tvCountMyGems);

        ly1month=findViewById(R.id.ly1month);
        ly3month=findViewById(R.id.ly3month);
        ly12months=findViewById(R.id.ly12months);

        ly100gems=findViewById(R.id.ly100gems);
        ly500gems=findViewById(R.id.ly500gems);
        ly1000gems=findViewById(R.id.ly1000gems);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
