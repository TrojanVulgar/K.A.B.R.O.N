package com.rifcode.holalapremium.View.Profile.Community;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rifcode.holalapremium.Adapter.CommunitySectionsPagerAdapter;
import com.rifcode.holalapremium.R;

public class CommunityActivity extends AppCompatActivity {

    private CommunitySectionsPagerAdapter sectionPagerAdapter;
    private ViewPager vPager;
    private TabLayout tabLayout;
    private ImageView imgvCommunityBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        widgets();
        //Tabs :
        sectionPagerAdapter = new CommunitySectionsPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        /// set adapter from section pager adapter class
        vPager.setAdapter(sectionPagerAdapter);
        /// install View pager :
        tabLayout.setupWithViewPager(vPager);
        changeTabsFont();

        imgvCommunityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void widgets() {
        vPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabLayout);
        imgvCommunityBack = findViewById(R.id.imgvCommunityBack);
    }

    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {

                    Typeface typeface =  Typeface.createFromAsset(getAssets(),"fonts/avenir_next_demibold.otf");
                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }


}
