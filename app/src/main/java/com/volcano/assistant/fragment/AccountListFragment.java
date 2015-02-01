// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.widget.FloatingActionButton;

/**
 * Account list
 */
public class AccountListFragment extends AbstractFragment {

    private FloatingActionButton mCreateAccountButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account_list, container, false);

        mCreateAccountButton = (FloatingActionButton) view.findViewById(R.id.button_create_account);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intents.getCreateAccountIntent());
            }
        });
    }
}
