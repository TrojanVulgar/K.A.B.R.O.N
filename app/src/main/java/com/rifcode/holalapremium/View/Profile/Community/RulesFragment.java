package com.rifcode.holalapremium.View.Profile.Community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rifcode.holalapremium.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RulesFragment extends Fragment {

    private View view;
    private TextView tvRules;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_rules, container, false);

        tvRules= view.findViewById(R.id.tvRulesMain);
        StringBuilder text = new StringBuilder();

        InputStream input = null;

        try {
            input = getActivity().getAssets().open("community_rules");
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

        tvRules.setText(text);

        return view;
    }
}
