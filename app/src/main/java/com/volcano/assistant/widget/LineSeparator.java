package com.volcano.assistant.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.volcano.assistant.R;

/**
 * Created by alimehrpour on 1/19/15.
 */
public class LineSeparator extends View {

    public LineSeparator(Context context) {
        super(context);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider)));
        setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0 ,0);
        setBackgroundColor(getResources().getColor(R.color.divider));
    }
}
