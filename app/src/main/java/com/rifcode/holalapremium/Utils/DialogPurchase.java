package com.rifcode.holalapremium.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.rifcode.holalapremium.R;

public class DialogPurchase {

    private static int selectPurchase;
    private static AlertDialog alertDialog;

    @SuppressLint("SetTextI18n")
    public static void dialog_purchase(final FirebaseAuth mAuth, final BillingProcessor bp, final Activity activity){

        View mViewInflatepurchase_likes = activity.getLayoutInflater().inflate(R.layout.dialog_gems,null);

        final android.widget.Button btnBuyGems = mViewInflatepurchase_likes.findViewById(R.id.btnBuyGems);
        TextView tvPrice100Gems = mViewInflatepurchase_likes.findViewById(R.id.tvPrice100Gems);
        TextView tvPrice500Gems = mViewInflatepurchase_likes.findViewById(R.id.tvPrice500Gems);
        TextView tvPrice1000Gems = mViewInflatepurchase_likes.findViewById(R.id.tvPrice1000Gems);
        LinearLayout ly100gems = mViewInflatepurchase_likes.findViewById(R.id.ly100gems);
        LinearLayout ly500gems = mViewInflatepurchase_likes.findViewById(R.id.ly500gems);
        LinearLayout ly1000gems = mViewInflatepurchase_likes.findViewById(R.id.ly1000gems);

        AlertDialog.Builder alertDialogBuilderpost = DialogUtils.CustomAlertDialog(mViewInflatepurchase_likes,activity);
        alertDialog = alertDialogBuilderpost.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

        // initial dialog
        selectPurchase=0;
        btnBuyGems.setText(activity.getString(R.string.buy)+" "+activity.getString(R.string._100_gems)+" "+activity.getString(R.string.gems));
        SkuDetails sku100Likes = bp.getPurchaseListingDetails(activity.getString(R.string.PRODUCT_ID_100_GEMS));
        tvPrice100Gems.setText(sku100Likes.priceText);

        SkuDetails sku500Likes = bp.getPurchaseListingDetails(activity.getString(R.string.PRODUCT_ID_500_GEMS));
        tvPrice500Gems.setText(sku500Likes.priceText);

        SkuDetails sku1000Likes = bp.getPurchaseListingDetails(activity.getString(R.string.PRODUCT_ID_1000_GEMS));
        tvPrice1000Gems.setText(sku1000Likes.priceText);


        ly100gems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnBuyGems.setText(activity.getString(R.string.buy)+" "+activity.getString(R.string._100_gems)+" "+activity.getString(R.string.gems));
                selectPurchase=0;
            }
        });
        ly500gems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnBuyGems.setText(activity.getString(R.string.buy)+" "+activity.getString(R.string._500_gems)+" "+activity.getString(R.string.gems));
                selectPurchase=1;
            }
        });
        ly1000gems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnBuyGems.setText(activity.getString(R.string.buy)+" "+activity.getString(R.string._1000_gems)+" "+activity.getString(R.string.gems));
                selectPurchase=2;
            }
        });

        btnBuyGems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getUid() != null){
                    alertDialog.dismiss();
                    //purchases
                    if(selectPurchase==0) {
                        bp.purchase(activity, activity.getString(R.string.PRODUCT_ID_100_GEMS));
                    }
                    else if(selectPurchase==1) {
                        bp.purchase(activity, activity.getString(R.string.PRODUCT_ID_500_GEMS));
                    }
                    else if(selectPurchase==2) {
                        bp.purchase(activity, activity.getString(R.string.PRODUCT_ID_1000_GEMS));
                    }
                }
            }
        });

      
    }
}
