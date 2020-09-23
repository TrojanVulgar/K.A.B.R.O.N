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

public class TermsOfServiceActivity extends AppCompatActivity {


    private TextView tvTermsOfService;
    private ImageView imgvTermsOfServiceBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        tvTermsOfService=findViewById(R.id.tvterms_of_serviceMain);
        imgvTermsOfServiceBack=findViewById(R.id.imgvterms_of_serviceBack);
        StringBuilder text = new StringBuilder();

        InputStream input = null;

        try {
            input = getAssets().open("term_of_service");
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
        tvTermsOfService.setText(text);

        imgvTermsOfServiceBack.setOnClickListener(new View.OnClickListener() {
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
