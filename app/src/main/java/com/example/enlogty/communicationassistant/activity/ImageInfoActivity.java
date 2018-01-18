package com.example.enlogty.communicationassistant.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.Image;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by enlogty on 2017/9/29.
 */

public class ImageInfoActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window=getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //获取样式中的属性值
            TypedValue typedValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            int[] attribute = new int[] { android.R.attr.colorPrimary };
            TypedArray array = this.obtainStyledAttributes(typedValue.resourceId, attribute);
            int color = array.getColor(0, Color.TRANSPARENT);
            array.recycle();
            window.setStatusBarColor(getResources().getColor(R.color.black_color));
        }
        setContentView(R.layout.activity_imageinfo);
        ImageView iv = (ImageView) findViewById(R.id.image_info);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        boolean cloud = intent.getBooleanExtra("cloud", false);
        String type = intent.getStringExtra("type");
        if (cloud){
            String url = intent.getStringExtra("url");
            if (type.contains("gif")){
                Glide.with(this).load(url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
            }else {
                Glide.with(this).load(url).into(iv);
            }

        }else {
            ArrayList<Image> imagelist = intent.getParcelableArrayListExtra("imagelist");
            if (imagelist.get(position).getType().contains("gif")){
                Glide.with(this).load(new File(imagelist.get(position).getPath())).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
            }else {
                Glide.with(this).load(new File(imagelist.get(position).getPath())).into(iv);
            }
        }


        RelativeLayout rvl = (RelativeLayout) findViewById(R.id.rvl);
        rvl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
