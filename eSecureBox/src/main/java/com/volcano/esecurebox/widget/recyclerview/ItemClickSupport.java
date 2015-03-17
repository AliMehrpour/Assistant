// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;

import com.volcano.esecurebox.R;
import com.volcano.esecurebox.util.LogUtils;

/**
 * Handle item click actions on a RecyclerView.<br />
 * Define a state list drawable for highlight selected row in list item, also don't use margin in
 * definition of list item layout, instead use padding.
 */
public class ItemClickSupport {
    private final static String TAG = LogUtils.makeLogTag(ItemClickSupport.class);

    private final RecyclerView mRecyclerView;
    private final TouchListener mTouchListener;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked
     */
    public interface OnItemClickListener {
        /**
         * Callback method to be invoked when an item in the RecyclerView
         * has been clicked
         *
         * @param parent The RecyclerView where the click happened.
         * @param view The view within the RecyclerView that was clicked
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        void onItemClick(RecyclerView parent, View view, int position, long id);
    }

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in the RecyclerView
         * has been clicked and held.
         *
         * @param parent The RecyclerView where the click happened
         * @param view The view within the RecyclerView that was clicked
         * @param position The position of the view in the list
         * @param id The row id of the item that was clicked
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(RecyclerView parent, View view, int position, long id);
    }

    private ItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        mTouchListener = new TouchListener(recyclerView);
        recyclerView.addOnItemTouchListener(mTouchListener);
    }

    /**
     * Register a callback to be invoked when an item in the
     * RecyclerView has been clicked.
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an item in the
     * RecyclerView has been clicked and held.
     * @param listener The callback that will be invoked.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!mRecyclerView.isLongClickable()) {
            mRecyclerView.setLongClickable(true);
        }

        mItemLongClickListener = listener;
    }

    /**
     * Add ItemClickSupport to the recyclerview
     * @param recyclerView The recyclerview
     * @return The new {@link ItemClickSupport}
     */
    public static ItemClickSupport addTo(RecyclerView recyclerView) {
        ItemClickSupport itemClickSupport = from(recyclerView);
        if (itemClickSupport == null) {
            itemClickSupport = new ItemClickSupport(recyclerView);
            recyclerView.setTag(R.id.recyclerview_item_click_support, itemClickSupport);
        }
        else {
            LogUtils.LogW(TAG, "Error in add ItemClickSupport to " + recyclerView);
        }

        return itemClickSupport;
    }

    /**
     * Remove add ItemClickSupport from the recyclerview
     * @param recyclerView The recyclerview
     */
    public static void removeFrom(RecyclerView recyclerView) {
        final ItemClickSupport itemClickSupport = from(recyclerView);
        if (itemClickSupport == null) {
            LogUtils.LogW(TAG, "Error in remove ItemClickSupport from " + recyclerView);
            return;
        }

        recyclerView.removeOnItemTouchListener(itemClickSupport.mTouchListener);
        recyclerView.setTag(R.id.recyclerview_item_click_support, null);
    }

    public static ItemClickSupport from(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return null;
        }

        return (ItemClickSupport) recyclerView.getTag(R.id.recyclerview_item_click_support);
    }

    private class TouchListener extends ClickItemTouchListener {
        TouchListener(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        boolean performItemClick(RecyclerView parent, final View view, int position, long id) {
            if (mItemClickListener != null) {
                view.playSoundEffect(SoundEffectConstants.CLICK);
                mItemClickListener.onItemClick(parent, view, position, id);
                return true;
            }

            return false;
        }

        @Override
        boolean performItemLongClick(RecyclerView parent, View view, int position, long id) {
            if (mItemLongClickListener != null) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return mItemLongClickListener.onItemLongClick(parent, view, position, id);
            }

            return false;
        }
    }
}
