package com.rifcode.holalapremium.View.Login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rifcode.holalapremium.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private TextView tvPrivacyP;
    private ImageView imgvPrivacyPolicyBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        tvPrivacyP= findViewById(R.id.tvPrivacyPplicyMain);
        imgvPrivacyPolicyBack= findViewById(R.id.imgvPrivacyPolicyBack);
        StringBuilder text = new StringBuilder();

        InputStream input = null;

                try {
                    input = getAssets().open("privacy_policy");
                } catch (IOException e) {
                    e.printStackTrace();
                }

        try {

            InputStreamReader isr = new InputStreamReader(input);

            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        tvPrivacyP.setText(text);

        imgvPrivacyPolicyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
