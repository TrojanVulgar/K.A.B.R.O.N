package com.rifcode.holalapremium.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.rifcode.holalapremium.Models.CardMatches;
import com.rifcode.holalapremium.Models.Images;
import com.rifcode.holalapremium.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPagerImagesAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Images> arrayList = new ArrayList<>();

    public ViewPagerImagesAdapter(Context context, ArrayList<Images> marrayList) {
        this.context = context;
        this.arrayList = marrayList;
    }



    @Override
    public int getCount() {
        return arrayList.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_list_images_item_swip, null);
        final ImageView image = view.findViewById(R.id.imgvOneImageUser);

        if(!arrayList.get(position).getThumb_picture().equals("none")) {
            Picasso.get().load(arrayList.get(position).getThumb_picture()).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.icon_register_select_header).into(image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(arrayList.get(position).getThumb_picture()).placeholder(R.drawable.icon_register_select_header)
                            .into(image);
                }
            });
        }



        container.addView(view);
        return view;
    }
    /*
    This callback is responsible for destroying a page. Since we are using view only as the
    object key we just directly remove the view from parent container
    */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


}