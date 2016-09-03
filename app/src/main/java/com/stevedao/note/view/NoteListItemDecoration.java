package com.stevedao.note.view;

import android.content.Context;
import android.firebase.note.R;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by thanh.dao on 4/21/2016.
 *
 */
@SuppressWarnings("unused")
public class NoteListItemDecoration extends RecyclerView.ItemDecoration {
    private final Drawable mDivider;
    private final Paint mPaint;
    private int mOrientation;

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    //    private static final int[] ATTRS = new int[]{
    //            android.R.attr.listDivider
    //    };

    //    @SuppressWarnings("deprecation")
    public NoteListItemDecoration(Context context, int orientation) {
        this.mOrientation = orientation;

        mDivider = ContextCompat.getDrawable(context, R.drawable.note_item_divider);
        mPaint = new Paint();
        mPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.note_list_divider_height));
        mPaint.setColor(context.getResources().getColor(R.color.color_black_opacity_75));
    }

    //    @Override
    //    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    //        if (mOrientation == VERTICAL_LIST) {
    //            drawVertical(canvas, parent);
    //        } else {
    //            drawHorizontal(canvas, parent);
    //        }
    //    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingTop();
        final int right = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getRight() + params.rightMargin;
            final int bottom = left + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    //    private void drawVertical(Canvas canvas, RecyclerView parent) {
    //        int offset = (int) (mPaint.getStrokeWidth()/2);
    //        final int left = parent.getPaddingLeft();
    //        final int right = parent.getWidth() - parent.getPaddingRight();
    //
    //        final int childCount = parent.getChildCount();
    //
    //        for (int i = 0; i < childCount; i++) {
    //            final View child = parent.getChildAt(i);
    //
    //            float positionY = child.getBottom() + offset + child.getTranslationY();
    //            mDivider.setBounds((int) (child.getLeft() + child.getTranslationX()),
    //                               (int) (positionY - mDivider.getIntrinsicHeight() / 2),
    //                               (int) (child.getRight() + child.getTranslationX()), (int) positionY);
    //            mDivider.draw(canvas);
    //        }
    //    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }

    //    public NoteListItemDecoration(Context context) {
    //        mContext = context;
    //        mDivider = ContextCompat.getDrawable(context, R.drawable.note_item_divider);
    //        mPaint = new Paint();
    //        mPaint.setStrokeWidth(mContext.getResources().getDimension(R.dimen.note_list_divider_height));
    //        mPaint.setColor(mContext.getResources().getColor(R.color.color_black_opacity_75));
    //    }
    //
    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int offset = (int) (mPaint.getStrokeWidth() / 2);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            float positionY = child.getBottom() + offset + child.getTranslationY();
            mDivider.setBounds((int) (child.getLeft() + child.getTranslationX()),
                               (int) (positionY - mDivider.getIntrinsicHeight()),
                               (int) (child.getRight() + child.getTranslationX()), (int) positionY);
            mDivider.draw(canvas);
        }
    }
}
