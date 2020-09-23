package com.rifcode.holalapremium.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.Models.Images;
import com.rifcode.holalapremium.Models.Users;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.BSDSelectPhoto;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Images> mImages ;
    private Context mContext;
    private Users mUsers;
    private DataFire dataFire;
    public static String posImage;



    public ImagesRecyclerViewAdapter(Context context, ArrayList<Images> images,Users users) {
        mImages = images;
        mContext = context;
        mUsers = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_profile_photo, parent, false);
        ViewHolder holder = new ViewHolder(view);
        dataFire=new DataFire();
        return holder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        if(mImages.size()>0) {
            holder.tvEditProfileCount.setText(String.valueOf(position+1));
            Picasso.get().load(mImages.get(position).getThumb_picture()).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.ivPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(mImages.get(position).getThumb_picture())
                                    .into(holder.ivPhoto);
                        }
                    });

            if(!mImages.get(position).getThumb_picture().equals("none")){
                holder.ivEditProfileAdd.setVisibility(View.GONE);
                holder.ivEditProfileDelete.setVisibility(View.VISIBLE);
            }else{
                holder.ivEditProfileAdd.setVisibility(View.VISIBLE);
                holder.ivEditProfileDelete.setVisibility(View.GONE);
            }

        }

        holder.ivEditProfileAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posImage=String.valueOf(position);
                BSDSelectPhoto bsdSelectPhoto = new BSDSelectPhoto();
                bsdSelectPhoto.show(((FragmentActivity)mContext).getSupportFragmentManager(), "exampleBottomSheet");
            }
        });



        holder.ivEditProfileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataFire.getDbRefUsers()
                        .child(dataFire.getUserID())
                        .child("images")
                        .child(String.valueOf(position))
                        .child("thumb_picture").setValue("none").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

               notifyItemRemoved(position);
               if(position==0){
                   dataFire.getDbRefUsers().child(dataFire.getUserID()).child("photoProfile")
                           .setValue("none");
               }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivPhoto,ivEditProfileAdd,ivEditProfileDelete;
        TextView tvEditProfileCount;
        CardView cvItemImages;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_edit_profile_photo);
            ivEditProfileAdd = itemView.findViewById(R.id.iv_edit_profile_add);
            ivEditProfileDelete = itemView.findViewById(R.id.iv_edit_profile_delete);
            tvEditProfileCount = itemView.findViewById(R.id.tv_edit_profile_count);
            cvItemImages = itemView.findViewById(R.id.cvItemImages);
        }


    }

}















