package com.rifcode.holalapremium.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rifcode.holalapremium.R;

public class LanguageAdapter extends BaseAdapter {

    Context context;
    String[] languagesList;
    String[] languagesListASO;
    int[] flags;
    LayoutInflater inflter;

    public LanguageAdapter(Context applicationContext, String[] mlanguagesList,String[] mlanguagesASO, int[] flags) {
        this.context = context;
        this.languagesList = mlanguagesList;
        this.flags = flags;
        this.languagesListASO = mlanguagesASO;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return languagesList.length;
    }

    @Override
    public Object getItem(int i) {
        return languagesListASO[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.layout_list_row_language, null);
        TextView tvlanguage = view.findViewById(R.id.tvLanguage);
        tvlanguage.setText(languagesList[i]);
        tvlanguage.setCompoundDrawablesWithIntrinsicBounds(flags[i], 0, 0, 0);

        return view;
    }


}
