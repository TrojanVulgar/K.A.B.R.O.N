package com.rifcode.holalapremium.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rifcode.holalapremium.R;

public class BSDSelectPhoto extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private View v;
    private LinearLayout llPictureSelectDialogPhotos,llPictureSelectDialogCamera,llPictureSelectDialogCancel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_picture_select, container, false);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.bottomSheetDialogTheme);
        wedgets();

        llPictureSelectDialogPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("ll_photos");
            }
        });

        llPictureSelectDialogCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onButtonClicked("ll_camera");
            }
        });

        llPictureSelectDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        return v;
    }


    private void wedgets() {

        //ll
        llPictureSelectDialogPhotos = v.findViewById(R.id.llPictureSelectDialogPhotos);
        llPictureSelectDialogCamera = v.findViewById(R.id.llPictureSelectDialogCamera);
        llPictureSelectDialogCancel = v.findViewById(R.id.llPictureSelectDialogCancel);

    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
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

}
