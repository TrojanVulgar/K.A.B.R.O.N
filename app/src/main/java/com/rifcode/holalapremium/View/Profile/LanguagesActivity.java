package com.rifcode.holalapremium.View.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.rifcode.holalapremium.Adapter.LanguageAdapter;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Login.SplashScreenActivity;

import java.util.Locale;

public class LanguagesActivity extends AppCompatActivity {

    private ListView lvLanguages;
    int[] flags = {
            R.drawable.flag_saudi_arabia,
            R.drawable.flag_united_kingdom,
            R.drawable.flag_france,
            R.drawable.flag_spain,
            R.drawable.flag_germany,
            R.drawable.flag_india,
            R.drawable.flag_italy,
            R.drawable.flag_indonesia,
            R.drawable.flag_portugal,
            R.drawable.flag_pakistan,
            R.drawable.flag_turkey,
    };

    String[] asolanguages = {
            "ar",
            "en",
            "fr",
            "es",
            "de",
            "in",
            "it",
            "id",
            "pt",
            "pk",
            "tr"};
    private LanguageAdapter languageAdapter;
    private ImageView imgvLanguagesBack,imgvLanguageSave;
    private int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
        widgets();
        languageAdapter = new LanguageAdapter(this,  getResources().getStringArray(R.array.arr_languages),asolanguages, flags);
        lvLanguages.setAdapter(languageAdapter);
        lvLanguages.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        imgvLanguagesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgvLanguageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage(languageAdapter.getItem(pos).toString());
            }
        });


        lvLanguages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                pos  = position;
            }
        });

    }

    private void changeLanguage(String codeLang){
        SharedPreferences.Editor editor = getSharedPreferences("changeLanguage", MODE_PRIVATE).edit();
        editor.putString("language_key", codeLang);
        editor.apply();

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(codeLang.toLowerCase()));
        }else{
            conf.locale = new Locale(codeLang.toLowerCase());
        }
        res.updateConfiguration(conf,dm);

        // restart app
        Intent i = new Intent(LanguagesActivity.this, SplashScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    private void widgets() {
        lvLanguages=findViewById(R.id.lvLanguages);
        imgvLanguageSave=findViewById(R.id.imgvLanguageSave);
        imgvLanguagesBack=findViewById(R.id.imgvLanguagesBack);
    }
}
