package com.rifcode.holalapremium.View.Main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.rifcode.holalapremium.Adapter.MainVPAdapter;
import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.Utils.BSDFiltersRandom;
import com.rifcode.holalapremium.Utils.ServiceDestroyApp;

public class MainActivity extends AppCompatActivity implements BSDFiltersRandom.BottomSheetListener{

    private TabLayout tabLayout,tabLayout1;
    public static ViewPager viewPager;
    private int index;
    private View mainSeparateLine;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wedgets();

        //Add fragments
        MainVPAdapter adapter = new MainVPAdapter(getSupportFragmentManager());
        adapter.addFragment(new RandomPeopleFragment());
        adapter.addFragment(new MessagingFragment());
        adapter.addFragment(new AccountFragment());
        viewPager.setCurrentItem(0);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout1.setupWithViewPager(viewPager);
        //Add tabs icon with setIcon() or simple text with .setText()
        tabLayout.getTabAt(0).setIcon(R.drawable.main_discover_p);
        tabLayout.getTabAt(1).setIcon(R.drawable.main_chat_plan);
        tabLayout.getTabAt(2).setIcon(R.drawable.main_me_plan);

        //Add tabs icon with setIcon() or simple text with .setText()
        tabLayout1.getTabAt(0).setIcon(R.drawable.main_discover);
        tabLayout1.getTabAt(1).setIcon(R.drawable.main_chat);
        tabLayout1.getTabAt(2).setIcon(R.drawable.main_me);

        startService(new Intent(getBaseContext(), ServiceDestroyApp.class));
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("state_app").setValue("start");


        addIconTabLayout();
        addIconTabLayout1();

        //set selector tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayout.setVisibility(View.GONE);
                tabLayout1.setVisibility(View.VISIBLE);
                mainSeparateLine.setVisibility(View.VISIBLE);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });

        tabLayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                    index = tab.getPosition();

                if (tab.getPosition()==1) {
                    tab.setIcon(R.drawable.main_chat_p);
                    tabLayout1.getTabAt(0).setIcon(R.drawable.main_discover);
                    tabLayout1.getTabAt(2).setIcon(R.drawable.main_me);
                }
                if (tab.getPosition()==2) {
                    tab.setIcon(R.drawable.main_me_p);
                    tabLayout1.getTabAt(0).setIcon(R.drawable.main_discover);
                    tabLayout1.getTabAt(1).setIcon(R.drawable.main_chat);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    if(position==0){
                        convertToTabLayout();
                    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void convertToTabLayout(){
        if(index ==0){
            tabLayout1.getTabAt(0).setIcon(R.drawable.main_discover_p);
            tabLayout1.getTabAt(2).setIcon(R.drawable.main_me);
            tabLayout1.getTabAt(1).setIcon(R.drawable.main_chat);
            tabLayout.setVisibility(View.VISIBLE);
            tabLayout1.setVisibility(View.GONE);
            mainSeparateLine.setVisibility(View.GONE);
        }
    }

    private void addIconTabLayout(){
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) tab.setCustomView(R.layout.view_home_tab);
        }
    }

    private void addIconTabLayout1(){
        for (int i = 0; i < tabLayout1.getTabCount(); i++) {
            TabLayout.Tab tab1 = tabLayout1.getTabAt(i);
            if (tab1 != null) tab1.setCustomView(R.layout.view_home_tab);
        }
    }

    private void wedgets() {
        tabLayout = findViewById(R.id.tl_main_plan_indicator);
        tabLayout1 = findViewById(R.id.tl_main_indicator);
        viewPager = findViewById(R.id.main_viewpager);
        mainSeparateLine = findViewById(R.id.main_seperate_line);
    }

    @Override
    public void onButtonClicked(String text) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.press_back, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;

            }
        }, 2000);
    }
}
