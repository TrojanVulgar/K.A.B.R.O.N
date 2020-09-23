package com.rifcode.holalapremium.Adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rifcode.holalapremium.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatVoiceViewHolders extends RecyclerView.ViewHolder{

    public TextView tvInMessageTextChat,tvOutMessageTextChat;
    public LinearLayout lyMessageInText,lyMessageOutText;
    public CircleImageView imgVProfilePicChatVoice;

    public ChatVoiceViewHolders(View itemView) {
        super(itemView);

        //textview
        tvInMessageTextChat=itemView.findViewById(R.id.tvInMessageTextChat);
        tvOutMessageTextChat=itemView.findViewById(R.id.tvOutMessageTextChat);

        //ly in
        lyMessageInText=itemView.findViewById(R.id.lyMeesageInText);
        //ly out
        lyMessageOutText=itemView.findViewById(R.id.lyMeesageOutText);
        imgVProfilePicChatVoice=itemView.findViewById(R.id.imgvProfilePicChatVoice);

    }

}
