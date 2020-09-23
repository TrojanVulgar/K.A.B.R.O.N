package com.rifcode.holalapremium.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rifcode.holalapremium.R;

public class GiftViewHolders extends RecyclerView.ViewHolder{

    public TextView tvGiftName,tvGiftCoins;
    public ImageView ivGiftIcon;

    public GiftViewHolders(View itemView) {
        super(itemView);

        //textview
        tvGiftName=itemView.findViewById(R.id.tv_gift_name);
        tvGiftCoins=itemView.findViewById(R.id.tv_gift_coins);

        //imageview
        ivGiftIcon=itemView.findViewById(R.id.iv_gift_icon);

    }

}
