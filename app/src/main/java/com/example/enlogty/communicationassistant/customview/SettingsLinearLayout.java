package com.example.enlogty.communicationassistant.customview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enlogty.communicationassistant.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by enlogty on 2017/9/3.
 */

public class SettingsLinearLayout extends LinearLayout implements View.OnClickListener{
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ImageView cb;
    private String cb_key;
    public SettingsLinearLayout(Context context) {
        this(context,null);
    }

    public SettingsLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingsLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setBackground(context.getDrawable(R.drawable.settings_item_background));
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,R.styleable.SettingsLinearLayout,defStyleAttr,0);
        int resid = ta.getResourceId(R.styleable.SettingsLinearLayout_src,R.mipmap.ic_clock);
        String title = ta.getString(R.styleable.SettingsLinearLayout_itemTitle);
        cb_key = ta.getString(R.styleable.SettingsLinearLayout_cb_key);
        ta.recycle();
        //载入布局
        LayoutInflater.from(context).inflate(R.layout.customview_settings,this);
        ImageView iv = (ImageView) findViewById(R.id.settings_iv);
        TextView tv = (TextView) findViewById(R.id.settings_tv);
        cb = (ImageView) findViewById(R.id.settings_cb);
        iv.setImageResource(resid);
        tv.setText(title);
        this.setOnClickListener(this);

        sp = context.getSharedPreferences("cb_pref", MODE_PRIVATE);
        editor = sp.edit();

        boolean isOn = sp.getBoolean(cb_key, true);
        if (isOn){
            cb.setImageResource(R.mipmap.ic_on);
        }else {
            cb.setImageResource(R.mipmap.ic_off);
        }
    }

    @Override
    public void onClick(View v) {
        switch (cb_key){
            case "cb1":
                setCbCheck(cb_key);
                break;
            case "cb2":
                setCbCheck(cb_key);
                break;
            case "cb3":
                setCbCheck(cb_key);
                break;
            case "cb4":
                setCbCheck(cb_key);
                break;
            case "cb5":
                setCbCheck(cb_key);
                break;
            default:
                break;
        }

    }

    private void setCbCheck(String key) {
        boolean isOn = sp.getBoolean(key, true);
        if (!isOn){
            editor.putBoolean(key,true);
            cb.setImageResource(R.mipmap.ic_on);
        }else {
            editor.putBoolean(key,false);
            cb.setImageResource(R.mipmap.ic_off);
        }
        editor.commit();
    }
}
