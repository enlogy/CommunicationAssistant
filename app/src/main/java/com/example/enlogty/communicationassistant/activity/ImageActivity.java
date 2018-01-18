package com.example.enlogty.communicationassistant.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.adapter.ImageAdapter;
import com.example.enlogty.communicationassistant.bean.Image;
import com.example.enlogty.communicationassistant.utils.ImageUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enlogty on 2017/9/20.
 */

public class ImageActivity extends AppCompatActivity implements ImageAdapter.onItemClickListener {
    private ImageAdapter imageAdapter;
    private ArrayList<Image> imageList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("相册");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        imageList = new ImageUtil().getImagePath(getContentResolver());
        imageAdapter = new ImageAdapter(imageList);
        imageAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(imageAdapter);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent imageIntent = new Intent(ImageActivity.this,ImageInfoActivity.class);
        imageIntent.putParcelableArrayListExtra("imagelist",imageList);
        imageIntent.putExtra("position",position);
        startActivity(imageIntent);
    }
}
