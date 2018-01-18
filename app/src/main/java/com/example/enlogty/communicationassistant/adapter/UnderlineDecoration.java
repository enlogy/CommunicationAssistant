package com.example.enlogty.communicationassistant.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by enlogty on 2017/12/27.
 */

public class UnderlineDecoration extends RecyclerView.ItemDecoration{
    private float mDividerHeight;
    private Paint paint;

    public UnderlineDecoration() {
        mDividerHeight = 1;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        for (int i=0 ;i<count ;i++){
            View childView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(childView);
            if (position == 0){
                continue;
            }
            float divierRight = childView.getWidth() - childView.getPaddingRight();
            float divierLeft = childView.getPaddingLeft();
            float divierTop = childView.getTop() - mDividerHeight;
            float divierButtom = childView.getTop();
            c.drawRect(divierLeft,divierTop,divierRight,divierButtom,paint);
        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) != 0){
            outRect.top = 3;
        }else {
            outRect.top = 1;
        }
    }
}
