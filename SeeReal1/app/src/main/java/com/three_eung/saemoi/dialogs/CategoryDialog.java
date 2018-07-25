package com.three_eung.saemoi.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;

import java.util.ArrayList;

public class CategoryDialog extends DialogFragment {
    private CateListener mListener;
    private View mView;
    private Dialog dialog;

    public static CategoryDialog newInstance(CateListener mListener) {
        CategoryDialog categoryDialog = new CategoryDialog();
        categoryDialog.mListener = mListener;

        return categoryDialog;
    }

    public interface CateListener {
        void onSelectComplete(int value);
    }

    private CategoryAdapter mAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.dialog_category_list, null);

        ArrayList<Integer> items = new ArrayList<>();
        for (int i = 1; i < 60; i++)
            items.add(i);

        CategoryAdapter mAdapter = new CategoryAdapter(items);
        GridView gridView = (GridView) mView.findViewById(R.id.grid_category);
        gridView.setAdapter(mAdapter);

        builder.setView(mView).setNegativeButton("닫기", null);

        dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    class CategoryAdapter extends BaseAdapter implements View.OnClickListener {
        private LayoutInflater inflater;
        private ArrayList<Integer> items;

        private class ViewHolder {
            public ImageView imageView;
            public int position;
        }

        public CategoryAdapter(ArrayList<Integer> items) {
            inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_add_category, viewGroup, false);

                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.category_image);
                viewHolder.position = position;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.imageView.setImageResource(Utils.getCategoryDrawable(items.get(position)));

            convertView.setOnClickListener(this);

            return convertView;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();

            mListener.onSelectComplete(viewHolder.position + 1);

            dialog.dismiss();
        }
    }
}