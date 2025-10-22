package com.unisaver.unisaver;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewWithMaxHeight extends RecyclerView {

    private int maxHeight = 0;

    public RecyclerViewWithMaxHeight(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewWithMaxHeight(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewWithMaxHeight);
        maxHeight = a.getDimensionPixelSize(R.styleable.RecyclerViewWithMaxHeight_maxHeight, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (maxHeight > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, heightSpec);
    }
}
