// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.fragment.DisplayAccountFragment;
import com.volcano.assistant.widget.FloatingActionButton;
import com.volcano.assistant.widget.RobotoTextView;

/**
 * Display a account values
 */
public class DisplayAccountActivity extends AbstractActivity {

    private FloatingActionButton mEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_account);

        final Intent intent = getIntent();
        final String accountId = intent.getStringExtra(Intents.EXTRA_ACCOUNT_ID);
        final String color = intent.getStringExtra(Intents.EXTRA_CATEGORY_COLOR);
        setToolbarColor(color);

        final ImageView cancelImage = (ImageView) findViewById(R.id.image_cancel);
        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final RobotoTextView titleText = (RobotoTextView) findViewById(R.id.text_title);
        titleText.setText(intent.getStringExtra(Intents.EXTRA_ACCOUNT_TITLE));

        mEditButton = (FloatingActionButton) findViewById(R.id.button_edit);
        mEditButton.setColorNormal(color, true);
        mEditButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_zoom_in));
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(Intents.getEditAccountIntent(accountId, color));
            }
        });

        final DisplayAccountFragment fragment = (DisplayAccountFragment) getFragmentManager().findFragmentById(R.id.fragment_edit_account);
        fragment.setOnEnableEditListener(new DisplayAccountFragment.OnEnableEditListener() {
            @Override
            public void onEnableEdit(boolean enable) {
                mEditButton.setEnabled(enable);
            }
        });
        fragment.loadFields(accountId);
    }
}
