package com.rifcode.holalapremium.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.rifcode.holalapremium.Models.CardMatches;
import com.rifcode.holalapremium.R;
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;


public class ArrayAdapterMatches extends RecyclerView.Adapter<ArrayAdapterMatches.ViewHolder> {


    private Context mContext;
    private ViewPagerImagesAdapter vpImagesAdapter;
    private CardMatches cardItem;
    private ArrayList<CardMatches> cardMatches;



    public ArrayAdapterMatches(Context mContext, CardMatches cardItem,ArrayList<CardMatches> mcardMatches) {
        this.mContext = mContext;
        this.cardItem = cardItem;
        this.cardMatches = mcardMatches;
    }

    @NonNull
    @Override
    public ArrayAdapterMatches.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe_card, parent, false);
        ArrayAdapterMatches.ViewHolder holder = new ArrayAdapterMatches.ViewHolder(view);
        return holder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ArrayAdapterMatches.ViewHolder holder, final int position) {



        vpImagesAdapter = new ViewPagerImagesAdapter(mContext, cardMatches.get(position).getImages());
        holder.viewPager.setAdapter(vpImagesAdapter);
        holder.indicatorView.setViewPager(holder.viewPager);

        holder.tvBarNameAge.setText(cardMatches.get(position).getUsername()+", "+cardMatches.get(position).getAge());
        holder.tvPopNameAge.setText(cardMatches.get(position).getUsername()+", "+cardMatches.get(position).getAge());

        holder.tvPopEducation.setText(cardMatches.get(position).getSchool());
        holder.tvPopCareer.setText(cardMatches.get(position).getJob());
        holder.tvPopDescription.setText(cardMatches.get(position).getAbout());
        if(cardMatches.get(position).getCity()==null)
            holder.tvPopLocation.setVisibility(View.GONE);
        else
        holder.tvPopLocation.setText(cardMatches.get(position).getCity()+", "+cardMatches.get(position).getCountry());

        if(cardMatches.get(position).getGender().equals("guy")){
            holder.tvBarNameAge.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_common_male_selected, 0);
            holder.tvPopNameAge.setCompoundDrawablesWithIntrinsicBounds( R.drawable.icon_common_male_selected, 0,0, 0);

        }else{
            holder.tvBarNameAge.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_common_female_selected, 0);
            holder.tvPopNameAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_common_female_selected, 0, 0, 0);

        }

        holder.lyRightSwipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.viewPager.setCurrentItem(holder.viewPager.getCurrentItem() + 1);
            }
        });

        holder.lyLeftSwipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.viewPager.setCurrentItem(holder.viewPager.getCurrentItem() - 1);
            }
        });


        holder.imgVInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.popCard.setVisibility(View.VISIBLE);
                holder.rlDefaultInfoContent.setVisibility(View.GONE);
            }
        });

        holder.ivPopArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.popCard.setVisibility(View.GONE);
                holder.rlDefaultInfoContent.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cardMatches.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        LinePageIndicator indicatorView;
        ViewPager viewPager;
        TextView tvBarNameAge,tvPopNameAge,tvPopEducation,tvPopCareer,tvPopDescription,tvPopLocation;
        ImageView imgVInfo,ivPopArrow;
        CardView popCard;
        RelativeLayout rlDefaultInfoContent;
        LinearLayout lyLeftSwipe,lyRightSwipe;

        public ViewHolder(View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.vpPreviewProfile);
            indicatorView = itemView.findViewById(R.id.indicator);
            tvBarNameAge = itemView.findViewById(R.id.tv_bar_name_age);
            imgVInfo = itemView.findViewById(R.id.iv_info_icon);
            popCard = itemView.findViewById(R.id.pop_card);
            ivPopArrow = itemView.findViewById(R.id.iv_pop_arrow);
            tvPopNameAge = itemView.findViewById(R.id.tv_pop_name_age);
            tvPopEducation = itemView.findViewById(R.id.tv_pop_education);
            tvPopCareer = itemView.findViewById(R.id.tv_pop_career);
            tvPopDescription = itemView.findViewById(R.id.tv_pop_description);
            tvPopLocation = itemView.findViewById(R.id.tv_pop_location);
            rlDefaultInfoContent = itemView.findViewById(R.id.rl_default_info_content);
            lyLeftSwipe = itemView.findViewById(R.id.lyLeftSwip);
            lyRightSwipe = itemView.findViewById(R.id.lyRightSwip);
        }


    }



}
