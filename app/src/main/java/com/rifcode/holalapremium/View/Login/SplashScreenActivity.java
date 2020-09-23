package com.rifcode.holalapremium.View.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import com.rbddevs.splashy.Splashy;
import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Main.MainActivity;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    private  DataFire dataFire;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataFire=new DataFire();

        SharedPreferences prefs = getSharedPreferences("changeLanguage", MODE_PRIVATE);
        String language_key = prefs.getString("language_key", null);
        if(language_key!=null){
            changeLanguage(language_key);
        }

        setSplashy();
        setContentView(R.layout.activity_splash_screen);


    }

    private void changeLanguage(String codeLang){

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(codeLang.toLowerCase()));
        }else{
            conf.locale = new Locale(codeLang.toLowerCase());
        }
        res.updateConfiguration(conf,dm);
    }

    private void  setSplashy(){

       Splashy splashy = new Splashy(this);

       splashy.setLogo(R.drawable.holala_icon_max)
                .setTitle(R.string.app_name)
                .setTitleColor(R.color.red_ff5346)
                .showProgress(true)
                .setProgressColor(R.color.red_ff5346)
                .setSubTitle(getString(R.string.sutitl_splash))
                .setSubTitleColor(R.color.main_text)
                .setBackgroundResource(R.color.white)
                .setFullScreen(true)
                .setTime(5000)
                .setAnimation(Splashy.Animation.GROW_LOGO_FROM_CENTER,800)
                .setTitleFontStyle("fonts/avenir_next_bold.otf")
                .setSubTitleFontStyle("fonts/avenir_next_medium.otf");

        splashy.show();


        new Splashy.OnComplete() {
            @Override
            public void onComplete() {

                /* New Handler to start the MainActivity
                 * and close this Splash-Screen after some seconds.*/
                /** Duration of wait **/ // 3.5 seconds
                int SPLASH_DISPLAY_LENGTH = 5500;
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {

                        if(dataFire.getCurrentUser()!=null){
                            /* Create an Intent that will start the MainActivity. */
                            Intent homeintent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(homeintent);
                            finish();
                        }else{
                            Intent homeintent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                            startActivity(homeintent);
                            finish();
                        }

                    }
                }, SPLASH_DISPLAY_LENGTH);


            }
        }.onComplete();



    }

}
