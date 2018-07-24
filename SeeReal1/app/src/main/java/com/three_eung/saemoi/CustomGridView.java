package com.three_eung.saemoi;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

public class CustomGridView extends GridView {
    private final boolean isDynamicHeight = true;
    private int mContentHeight = 0;
    private DataSetObserver mDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mContentHeight = 0;
            super.onChanged();
        }
    };

    public CustomGridView(Context context) {
        super(context);
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getTotalContentHeight() {
        int verticalSpacing = getVerticalSpacing();
        int columns = getNumColumns();
        int rows = 0;
        int height = 0;
        ListAdapter adapter = getAdapter();
        if(adapter == null) {
            return 0;
        }
        rows = (adapter.getCount() / columns) + ((adapter.getCount()%columns)!=0?1:0);
        if(rows<1) {
            return 0;
        }

        for(int i=0;i<rows;i++) {
            View v = adapter.getView(i*columns,null,this);
            v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            height += v.getMeasuredHeight();
        }

        return height + (verticalSpacing*(rows-1));
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mContentHeight = 0;
        if(adapter != null) {
            try {
                adapter.registerDataSetObserver(mDataObserver);
            } catch (Exception e) {}
        }
        super.setAdapter(adapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(isDynamicHeight) {
            if(mContentHeight == 0) {
                mContentHeight = getTotalContentHeight();
            }
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            int width=getMeasuredWidth();
            int height=getMeasuredHeight();
            height = mContentHeight > height ? mContentHeight : height;
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = height;
            setLayoutParams(params);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
