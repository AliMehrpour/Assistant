package com.volcano.assistant.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.widget.Toast;

/**
 * Created by alimehrpour on 1/5/15.
 */
public class AbstractFragment extends Fragment {

    protected void showToast(int id) {
        showToast(getString(id));
    }

    protected void showToast(CharSequence text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
