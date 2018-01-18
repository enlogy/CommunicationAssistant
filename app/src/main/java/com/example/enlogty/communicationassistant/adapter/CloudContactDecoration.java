package com.example.enlogty.communicationassistant.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by enlogty on 2017/11/22.
 */

public class CloudContactDecoration extends RecyclerView.ItemDecoration{
    private Rect rect;
    private Paint paint;
    public CloudContactDecoration() {
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
//        int count = parent.getChildCount();
//
//        for ( int i = 0; i< count ; i++){
//            View childAt = parent.getChildAt(i);
//            int left = parent.getLeft()+ parent.getPaddingLeft();
//            int right = parent.getRight();
//            int top = childAt.getBottom();
//            int bottom = top+2;
//            rect = new Rect(left,top,right,bottom);
//            c.drawRect(rect,paint);
//        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //outRect.bottom = 100;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
                int count = parent.getChildCount();

        for ( int i = 0; i< count ; i++){
            View childAt = parent.getChildAt(i);
            int left = parent.getLeft()+ parent.getPaddingLeft();
            int right = parent.getRight();
            int top = childAt.getBottom();
            int bottom = top+2;
            rect = new Rect(left,top,right,bottom);
            c.drawRect(rect,paint);
        }
    }
}
