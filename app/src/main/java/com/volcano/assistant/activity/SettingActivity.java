package com.volcano.assistant.activity;

import android.os.Bundle;

import com.volcano.assistant.fragment.SettingFragment;

/**
 * Created by Sherry on 1/30/2015 to support Application Setting
 */
public class SettingActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingFragment()).commit();


    }
}
