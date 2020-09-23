package com.rifcode.holalapremium.View.Profile;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.kyleduo.switchbutton.SwitchButton;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.DialogUtils;
import com.rifcode.holalapremium.View.Login.WelcomeActivity;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private DataFire dataFire;
    private ImageView imgvSettingsBack;
    private RelativeLayout rlNotifications,rlLanguages,rlSettingReview,rlSettingAbout
            ,rlSettingSendSuggestions,rlSettingLogout,rlSettingClearCache;
    private SwitchButton sbSettingsNotification;
    private View mViewInflatedialogReview;
    private View mViewInflatedialogLogout;
    private View mViewInflatedialogSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dataFire=new DataFire();
        wedgets();
        clickOnWidgets();
        sbSettingsNotification.setEnabled(false);
    }

    private void clickOnWidgets() {

        imgvSettingsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rlSettingReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReview();
            }
        });

        rlLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,LanguagesActivity.class));
            }
        });

        rlNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNotification();
            }
        });

        rlSettingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogout();
            }
        });

        rlSettingSendSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSuggestion();
            }
        });

        rlSettingAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,AboutAppActivity.class));
            }
        });

        rlSettingClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCache(SettingsActivity.this);
            }
        });
    }

    private void wedgets() {
        imgvSettingsBack=findViewById(R.id.imgvSettingsBack);
        rlNotifications=findViewById(R.id.rl_notifications);
        rlLanguages=findViewById(R.id.rl_languages);
        rlSettingReview=findViewById(R.id.rl_setting_review);
        rlSettingAbout=findViewById(R.id.rl_setting_about);
        rlSettingSendSuggestions=findViewById(R.id.rl_setting_send_suggestions);
        rlSettingLogout=findViewById(R.id.rl_setting_logout);
        rlSettingClearCache=findViewById(R.id.rl_setting_clear_cache);
        sbSettingsNotification=findViewById(R.id.sbSettingsNotification);
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void isNotificationEnabled() {
        if(!NotificationManagerCompat.from(this).areNotificationsEnabled()){
            sbSettingsNotification.setChecked(false);
        }else{
            sbSettingsNotification.setChecked(true);
        }
    }


    private void gotoNotification(){
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + SettingsActivity.this.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    private void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if(deleteDir(dir)){
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.clsucc), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.cldataf), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) { e.printStackTrace();}
    }

    private  boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void dialogReview(){

        mViewInflatedialogReview= getLayoutInflater().inflate(R.layout.dialog_review_us,null);
        TextView tv_dialog_review_us_cancel = mViewInflatedialogReview.findViewById(R.id.tv_dialog_review_us_cancel);
        TextView tv_dialog_review_us_confirm = mViewInflatedialogReview.findViewById(R.id.tv_dialog_review_us_confirm);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogReview,SettingsActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(!this.isFinishing()) {
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        tv_dialog_review_us_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMarket();
                    alertDialog.dismiss();

            }
        });

        tv_dialog_review_us_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                alertDialog.dismiss();
            }
        });

    }

    private void dialogLogout(){

        mViewInflatedialogLogout= getLayoutInflater().inflate(R.layout.dialog_log_out,null);
        TextView tv_dialog_logout_cancel = mViewInflatedialogLogout.findViewById(R.id.tv_dialog_logout_cancel);
        TextView tv_dialog_logout_confirm = mViewInflatedialogLogout.findViewById(R.id.tv_dialog_logout_confirm);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogLogout, SettingsActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(!this.isFinishing()) {
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        tv_dialog_logout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataFire.getCurrentUser()!=null) {
                    deleteCache(SettingsActivity.this);
                    dataFire.getmAuth().signOut();
                    Intent welcome = new Intent(SettingsActivity.this,WelcomeActivity.class);
                    welcome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(welcome);
                    finish();
                }
            }
        });

        tv_dialog_logout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                alertDialog.dismiss();
            }
        });

    }

    private void dialogSuggestion(){

        mViewInflatedialogSuggestion= getLayoutInflater().inflate(R.layout.dialog_send_suggestions,null);
        TextView tv_dialog_suggestion_cancel = mViewInflatedialogSuggestion.findViewById(R.id.tv_dialog_suggestion_cancel);
        TextView tv_dialog_suggestion_confirm = mViewInflatedialogSuggestion.findViewById(R.id.tv_dialog_suggestion_confirm);
        final EditText edtTextSuggestion = mViewInflatedialogSuggestion.findViewById(R.id.edtTextSuggestion);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogSuggestion, SettingsActivity.this);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(!this.isFinishing()) {

            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        tv_dialog_suggestion_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataFire.getCurrentUser()!=null) {
                    if (!TextUtils.isEmpty(edtTextSuggestion.getText().toString())){
                        String puchKey = dataFire.getdbRefSuggestions().push().getKey();
                        dataFire.getdbRefSuggestions().child(puchKey).child("userID").setValue(dataFire.getUserID());
                        dataFire.getdbRefSuggestions().child(puchKey).child("text").setValue(edtTextSuggestion.getText().toString());
                        dataFire.getdbRefSuggestions().child(puchKey).child("time").setValue(-1*(System.currentTimeMillis()/1000))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SettingsActivity.this, R.string.str_send_segg_test, Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                });
                    }else{
                        Toast.makeText(SettingsActivity.this, R.string.pu_seggestoion, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tv_dialog_suggestion_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                alertDialog.dismiss();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        isNotificationEnabled();
    }


}
