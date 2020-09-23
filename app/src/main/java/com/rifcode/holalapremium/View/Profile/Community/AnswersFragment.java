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

public class AnswersFragment extends Fragment {


    private View view;
    private TextView tvAnswers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_answers, container, false);

        tvAnswers= view.findViewById(R.id.tvAnswersMain);
        StringBuilder text = new StringBuilder();

        String file  = getActivity().getIntent().getStringExtra("file");
        InputStream input = null;

        try {
            input = getActivity().getAssets().open("community_answers");
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

        tvAnswers.setText(text);

        return view;
    }

}
