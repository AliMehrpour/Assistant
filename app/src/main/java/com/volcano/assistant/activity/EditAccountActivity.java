package com.volcano.assistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.fragment.EditAccountFragment;
import com.volcano.assistant.widget.FloatingActionButton;
import com.volcano.assistant.widget.RobotoTextView;

/**
 * Created by alimehrpour on 2/15/15.
 */
public class EditAccountActivity extends AbstractActivity {

    private FloatingActionButton mEditButton;
    private EditAccountFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        final Intent intent = getIntent();
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
        final Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_zoom_in);
        mEditButton.startAnimation(buttonAnimation);

        mFragment = (EditAccountFragment) getFragmentManager().findFragmentById(R.id.fragment_edit_account);
        mFragment.setOnEnableEditListener(new EditAccountFragment.OnEnableEditListener() {
            @Override
            public void onEnableEdit(boolean enable) {
                mEditButton.setEnabled(enable);
            }
        });
        mFragment.loadFields(intent.getStringExtra(Intents.EXTRA_ACCOUNT_ID));

    }
}
