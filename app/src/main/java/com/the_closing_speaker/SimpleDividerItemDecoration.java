package com.the_closing_speaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jacob on 3/5/16.
 */
public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final String LOG_TAG = SimpleDividerItemDecoration.class.getSimpleName();
    private Drawable mDivider;

    public SimpleDividerItemDecoration(Context context) {
        //TODO add a .divider_horizontal_dark for a possible dark theme
        mDivider = ContextCompat.getDrawable(context, android.R.drawable.divider_horizontal_bright);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getLeft();
        int right = parent.getWidth();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            if (isDecorated(child, parent)) {

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            } else {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mDivider.setBounds(left+parent.getPaddingStart(), top, right+parent.getPaddingEnd(), bottom);
                } else {
                    mDivider.setBounds(left+parent.getPaddingLeft(), top, right+parent.getPaddingRight(), bottom);
                }
                mDivider.draw(c);
            }
        }
    }

    public boolean isDecorated(View view, RecyclerView parent) {
        RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
        return holder instanceof AToZListHolder;
    }

//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        if (isDecorated(view, parent)) {
//            if (mOrientation == VERTICAL_LIST) {
//                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
//            } else {
//                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
//            }
//        } else {
//            super.getItemOffsets(outRect, view, parent, state);
//        }
//    }
}
