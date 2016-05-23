// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.analytics.MixpanelManager;
import com.volcano.esecurebox.fragment.DisplayAccountFragment;
import com.volcano.esecurebox.widget.FloatingActionButton;

/**
 * Display a account values
 */
public final class DisplayAccountActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_account);

        setActionBar((Toolbar) findViewById(R.id.toolbar));

        final Intent intent = getIntent();
        final String accountId = intent.getStringExtra(Intents.EXTRA_ACCOUNT_ID);
        final String color = intent.getStringExtra(Intents.EXTRA_CATEGORY_COLOR);
        final int colorId = Color.parseColor(String.format("#%s", color));
        setToolbarColor(colorId);

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(intent.getStringExtra(Intents.EXTRA_ACCOUNT_TITLE));
        collapsingToolbar.setContentScrimColor(colorId);
        collapsingToolbar.setStatusBarScrimColor(colorId);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Text_RegularWhite30);

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setBackgroundColor(colorId);

        final FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.button_edit);
        editButton.setColorNormal(color, true);
        editButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_zoom_in));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Managers.getMixpanelManager().track(MixpanelManager.EVENT_EDIT_ITEM);
                startActivity(Intents.getEditAccountIntent(accountId, color));
            }
        });

        final DisplayAccountFragment fragment = (DisplayAccountFragment) getFragmentManager().findFragmentById(R.id.fragment_edit_account);
        fragment.setOnEnableEditListener(new DisplayAccountFragment.OnEnableEditListener() {
            @Override
            public void onEnableEdit(boolean enable) {
                editButton.setEnabled(enable);
            }
        });
        fragment.loadFields(accountId);
    }
}
