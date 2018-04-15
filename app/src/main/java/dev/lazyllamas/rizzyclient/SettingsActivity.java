package dev.lazyllamas.rizzyclient;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsActivity extends Fragment {


    public static SettingsActivity newInstance(int sectionNumber) {
        SettingsActivity fragment = new SettingsActivity();
        Bundle args = new Bundle();
        args.putInt(MainTabbedActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_settings, container, false);
        ConstraintLayout layout = v.findViewById(R.id.settingsId);
        Button button = layout.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ActivitiesActivity.class);
                startActivity(i);
            }
        });
        return v;
    }


}