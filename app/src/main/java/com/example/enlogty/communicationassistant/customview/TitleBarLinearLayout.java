package com.example.enlogty.communicationassistant.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.enlogty.communicationassistant.R;

/**
 * Created by enlogty on 2017/8/19.
 */

public class TitleBarLinearLayout extends LinearLayout{
    public TitleBarLinearLayout(Context context) {
        this(context ,null);
    }
    public TitleBarLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs , 0);

    }

    public TitleBarLinearLayout(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a=context.getTheme().obtainStyledAttributes(attrs,R.styleable.TitleBarLinearLayout,defStyleAttr,0);
        String title = a.getString(a.getIndex(0));
        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.titlebar,this);
        ImageView titleBarBack = (ImageView) findViewById(R.id.titlebar_back);
        TextView titleTextView = (TextView) findViewById(R.id.titlebar_title);
        titleTextView.setText(title);
        titleBarBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });
    }
}
