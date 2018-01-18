package com.example.enlogty.communicationassistant.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.Contact;

import java.util.List;

/**
 * Created by enlogty on 2017/9/5.
 */

public class ContactItemDecoration extends RecyclerView.ItemDecoration{
    private List<Contact> contactList;
    private int ItemHeight;
    private Paint bgPaint;
    private TextPaint txtPaint;
    public ContactItemDecoration(Context context , List<Contact> contactList) {
        this.contactList = contactList;
        Resources resources = context.getResources();
        ItemHeight = resources.getDimensionPixelSize(R.dimen.top);
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#52b1f7"));
        txtPaint = new TextPaint();
        txtPaint.setTextSize(60);
        txtPaint.setColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
           // if (isFirstInGroup(position)) {
                float top = view.getTop() - 120;
                float bottom = view.getTop();
                c.drawRect(left, view.getTop(), right, view.getBottom(), bgPaint);//绘制红色矩形
                //c.drawText(contactList.get(position).getFirstpingyin(),60, 80, txtPaint);//绘制文本
           // }
        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (isFirstInGroup(position)){
            outRect.set(0,view.getHeight(),0,0);
            //outRect.bottom = view.getHeight();
       }else {
            outRect.top = 0;
       }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        c.drawRect(left, 0, right, 120, bgPaint);//绘制红色矩形
        c.drawText(contactList.get(position).getFirstpingyin(), 60, 80, txtPaint);//绘制文本
        
    }
    private boolean isFirstInGroup(int position){
        if(position == 0){
            return true;
        }else if (!contactList.get(position).getFirstpingyin().equals(contactList.get(position-1).getFirstpingyin())){
            return true;
        }
        return false;
    }
}
