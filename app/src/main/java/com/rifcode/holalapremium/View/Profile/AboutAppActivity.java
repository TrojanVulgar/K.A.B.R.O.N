package com.rifcode.holalapremium.View.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.rifcode.holalapremium.Models.DataFire;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Login.PrivacyPolicyActivity;
import com.rifcode.holalapremium.View.Login.TermsOfServiceActivity;
import com.rifcode.holalapremium.View.Profile.Community.CommunityActivity;

public class AboutAppActivity extends AppCompatActivity {

    private ImageView imgvAboutAppBack;
    private DataFire dataFire;
    private RelativeLayout rlAboutPrivacyPolicy,rlAboutTermsOfService,rlAboutCommunity,rlDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        dataFire=new DataFire();
        widgets();
        clickOnWidgets();
    }

    private void clickOnWidgets() {
        imgvAboutAppBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlAboutPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutAppActivity.this, PrivacyPolicyActivity.class));
            }
        });
        rlAboutTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutAppActivity.this, TermsOfServiceActivity.class));
            }
        });
        rlAboutCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutAppActivity.this, CommunityActivity.class));
            }
        });

    }

    private void widgets() {
        imgvAboutAppBack=findViewById(R.id.imgvAboutAppBack);

        rlAboutPrivacyPolicy=findViewById(R.id.rl_about_privacy_policy);
        rlAboutCommunity=findViewById(R.id.rl_about_community);
        rlAboutTermsOfService=findViewById(R.id.rl_about_terms_of_service);
        rlDeleteAccount=findViewById(R.id.rl_delete_account);
    }

}
