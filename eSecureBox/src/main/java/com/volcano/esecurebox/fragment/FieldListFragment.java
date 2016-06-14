package com.volcano.esecurebox.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.model.Field;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.CompatUtils;
import com.volcano.esecurebox.widget.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Display a list of fields
 */
public final class FieldListFragment extends AbstractFragment {

    private ListView mListView;

    private final static ArrayList<Field> mOriginalFields = new ArrayList<>();
    private final ArrayList<Field> mFields = new ArrayList<>();
    private final FieldListAdapter mAdapter = new FieldListAdapter();
    private final ArrayList<Field> mSelectedFields = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = (ListView)inflater.inflate(R.layout.fragment_field_list, container, false);
        return mListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Field field = (Field) parent.getItemAtPosition(position);
                final boolean selected;

                if (mSelectedFields.contains(field)) {
                    mSelectedFields.remove(field);
                    selected = false;
                }
                else {
                    mSelectedFields.add(field);
                    selected = true;
                }

                ((FieldListItem) view).setField(field, selected);
            }
        });
    }

    /**
     * Load field list filtered by a query
     * @param query The query. null will load all fields from server
     */
    public void loadFields(String query) {
        if (TextUtils.isEmpty(query)) {
            clearSelected();

            if (mOriginalFields.isEmpty()) {
                Field.findInBackground(this, new FindCallback<Field>() {
                    @Override
                    public void done(List<Field> fields, ParseException e) {
                        if (e == null) {
                            if (fields.size() > 0) {
                                mFields.clear();
                                mFields.addAll(fields);
                                mAdapter.notifyDataSetChanged();

                                // Used for search
                                mOriginalFields.clear();
                                mOriginalFields.addAll(fields);
                            }
                            else {
                                setErrorState();
                            }
                        }
                        else {
                            setErrorState();
                        }
                    }
                });
            }
            else {
                mFields.clear();
                mFields.addAll(mOriginalFields);
                mAdapter.notifyDataSetChanged();
            }
        }
        else {
            mFields.clear();
            final int size = mOriginalFields.size();
            for (int i = 0; i < size; i++) {
                final Field field = mOriginalFields.get(i);
                if (field.getName().toLowerCase().matches(String.format("(\\w*\\W+)*%s.*", query.toLowerCase()))) {
                    mFields.add(field);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @return selected fields
     */
    public ArrayList<Field> getSelectedFields() {
        return mSelectedFields;
    }

    /**
     * Clear selected list
     */
    public void clearSelected() {
        mSelectedFields.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void addNewField(Field field) {
        mOriginalFields.add(0, field);
        mFields.add(0, field);
        mSelectedFields.add(0, field);
        mAdapter.notifyDataSetChanged();

        mListView.smoothScrollToPosition(0);

    }
    private void setErrorState() {
        // TODO: handle error state
    }

    private class FieldListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFields.size();
        }

        @Override
        public Object getItem(int position) {
            return mFields.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FieldListItem view;

            if (convertView != null) {
                view = (FieldListItem) convertView;
            }
            else {
                view = new FieldListItem(parent.getContext());
            }

            final Field field = (Field) getItem(position);
            view.setField(field, mSelectedFields.contains(field));

            return view;
        }
    }

    private class FieldListItem extends RelativeLayout {

        private ImageView mIcon;
        private RobotoTextView mNameText;
        private ImageView mCheckMarkImage;

        public FieldListItem(Context context) {
            super(context);
            View.inflate(context, R.layout.list_item_field, this);

            mIcon = (ImageView) findViewById(R.id.icon);
            mNameText = (RobotoTextView) findViewById(R.id.text_name);
            mCheckMarkImage = (ImageView) findViewById(R.id.image_check_mark);
        }

        private void setField(Field field, boolean selected) {
            mIcon.setImageDrawable(BitmapUtils.getFieldDrawable(getContext(), field.getIconName(), field.getName().charAt(0), CompatUtils.getColor(R.color.grey_1)));
            mNameText.setText(field.getName());
            mCheckMarkImage.setVisibility(selected ? VISIBLE : INVISIBLE);
        }
    }
}
