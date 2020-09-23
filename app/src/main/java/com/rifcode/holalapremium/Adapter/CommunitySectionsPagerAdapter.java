package com.rifcode.holalapremium.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rifcode.holalapremium.R;
import com.rifcode.holalapremium.View.Profile.Community.AnswersFragment;
import com.rifcode.holalapremium.View.Profile.Community.RulesFragment;


public class CommunitySectionsPagerAdapter extends FragmentPagerAdapter {

    Context context;

    public CommunitySectionsPagerAdapter(FragmentManager fm, Context nContext) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        context = nContext;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                RulesFragment rulesFrag = new RulesFragment();
                return rulesFrag;
            case 1:
                AnswersFragment answersFrag = new AnswersFragment();
                return answersFrag;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){
        switch (position) {

            case 0:
                return  context.getString(R.string.string_rules);
            case 1:
                return context.getString(R.string.string_answers);

            default:
                return null;
        }
    }


}
