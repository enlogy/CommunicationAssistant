package com.example.enlogty.communicationassistant.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.adapter.CloudImageAdapter;
import com.example.enlogty.communicationassistant.adapter.HeaderAndFooterWrapper;
import com.example.enlogty.communicationassistant.adapter.PictureFooterWrapper;
import com.example.enlogty.communicationassistant.base.BaseActivity;
import com.example.enlogty.communicationassistant.bean.Image;
import com.example.enlogty.communicationassistant.bean.User;
import com.example.enlogty.communicationassistant.domain.CloudImage;
import com.example.enlogty.communicationassistant.presenter.CloudImagePresenter;
import com.example.enlogty.communicationassistant.utils.CloudImageUtil;
import com.example.enlogty.communicationassistant.utils.ImageUtil;
import com.example.enlogty.communicationassistant.utils.UploadImageUtil;
import com.example.enlogty.communicationassistant.view.ICloudImage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by enlogty on 2017/11/30.
 */

public class CloudImageActivity extends BaseActivity<ICloudImage,CloudImagePresenter> implements ICloudImage , View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, CloudImageAdapter.onItemClickListener {
    private TextView tv_selected;
    private TextView  all_select;
    private ImageView download;
    private String localNumber;
    private boolean loadCompleted;
    private boolean isSelected;
    private RecyclerView recyclerView;
    private int cloudImageSize;
    private UploadImageUtil uploadImageUtil;
    private Handler handler;
    private ProgressDialog dialog;
    private List<CloudImage> selectImageList;
    private final int SHOWIMAGELIST = 0x1;
    private final int SWIPEREFRESH = 0x2;
    private final int DOWNLOADPICTURE = 0x3;
    private CloudImageAdapter adapter;
    private PictureFooterWrapper defaultAdapter;
    private SwipeRefreshLayout refreshLayout;


    @Override
    public CloudImagePresenter createPresenter() {
        return new CloudImagePresenter();
    }

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
            window.setStatusBarColor(color);
        }
        setContentView(R.layout.activity_cloudimage);
        initView();
        initData();
        mPresenter.fetch(this);
        //测试上传图片
        //testUpload();
    }

    private void initData() {
        uploadImageUtil = new UploadImageUtil(this);
        localNumber = User.localNumber;
        isSelected = true;
    }

    private void initView() {
        handler = new ImageHandler();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("云相册");
        ImageView iv = findViewById(R.id.title_iv);
        Glide.with(this).load(R.mipmap.iv3).into(iv);
        recyclerView = findViewById(R.id.rv_cloudimage);
        tv_selected = findViewById(R.id.tv_selected);
        tv_selected.setOnClickListener(this);
        all_select = findViewById(R.id.all_select);
        all_select.setOnClickListener(this);
        download = findViewById(R.id.download);
        download.setOnClickListener(this);
        refreshLayout = findViewById(R.id.swipelayout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(this);
        dialog = new ProgressDialog(CloudImageActivity.this);
        dialog.setTitle("恢复进度");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
    }

    @Override
    public void getDataFromRemoteService(List<CloudImage> cloudImageList) {
        //fetch complete
      //服务来代替上传

        //
        cloudImageSize = cloudImageList.size();
            Message msg = new Message();
            msg.obj = cloudImageList;
            msg.what = SHOWIMAGELIST;
            handler.sendMessage(msg);
    }

    @Override
    public void onClick(View view) {
        if (loadCompleted){
        switch (view.getId()){
            case R.id.download:
                //恢复图片到手机
                selectImageList = adapter.getSelectImageList();
                //setMax
                dialog.setMax(selectImageList.size());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CloudImageUtil imageUtil = new CloudImageUtil(CloudImageActivity.this);
                        ArrayList<Image> images = new ImageUtil().getImagePath(getContentResolver());
                        for (int i=0; i<selectImageList.size(); i++){
                            CloudImage cloudImage = selectImageList.get(i);
                            imageUtil.writePictureToLocal(CloudImageActivity.this,cloudImage.getUrl(), cloudImage.getPath(),cloudImage.getType(), images ,new CloudImageUtil.downloadPictureListener() {
                                @Override
                                public void onComplite(int count) {
                                    Message msg = new Message();
                                    msg.what = DOWNLOADPICTURE;
                                    msg.obj = count;
                                    handler.sendMessage(msg);
                                }
                            });
                        }
                    }
                }).start();
                break;
            case R.id.all_select:

                if (isSelected){
                    //全选状态
                    isSelected = false;
                    all_select.setText("全不选");
                    tv_selected.setText("已选("+cloudImageSize+")");
                    int count = adapter.bo.length;
                    for (int i=0 ;i<count ;i++){
                        adapter.bo[i] = true;
                    }
                    defaultAdapter.notifyDataSetChanged();
                    download.setBackgroundResource(R.mipmap.bk_on);
                    download.setClickable(true);

                }else {
                    //全不选
                    isSelected = true;
                    all_select.setText("全选");
                    tv_selected.setText("已选("+0+")");
                    int mcount = adapter.bo.length;
                    for (int i=0 ;i<mcount ;i++){
                        adapter.bo[i] = false;
                    }
                    defaultAdapter.notifyDataSetChanged();
                    download.setBackgroundResource(R.mipmap.bk_off);
                    download.setClickable(false);
                }

                break;
            case R.id.tv_selected:

                break;

            default:
                break;
        }
        }
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CloudImage> data = new CloudImageUtil(CloudImageActivity.this).getCloudImageData(User.localNumber);
                Message msg = new Message();
                msg.obj = data;
                msg.what = SWIPEREFRESH;
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onItemClick(int position) {
        List<CloudImage> imageList = adapter.getImageList();
        Intent intent = new Intent(CloudImageActivity.this,ImageInfoActivity.class);
        intent.putExtra("cloud",true);
        intent.putExtra("url",imageList.get(position).getUrl());
        intent.putExtra("type",imageList.get(position).getType());
        startActivity(intent);
    }

    class ImageHandler  extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOWIMAGELIST:
                   List<CloudImage> data = (List<CloudImage>) msg.obj;
                    showImageList(data);
                    break;
                case SWIPEREFRESH:
                    List<CloudImage> idata = (List<CloudImage>) msg.obj;
                    swiperefresh(idata);
                    break;
                case DOWNLOADPICTURE:
                    int count = (int) msg.obj;
                    showProgressbar(count);
                    break;
                default:
                    break;
            }
        }
    }

    private void showProgressbar(int count) {
        if (!dialog.isShowing()){
            dialog.show();
        }
        dialog.setProgress(count);
        if (count ==dialog.getMax()){
            dialog.dismiss();
            Toast.makeText(this,"数据恢复成功",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void swiperefresh(List<CloudImage> idata) {
        if (idata.size()>0){
            adapter.setImageList(idata);
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        }else {
            refreshLayout.setRefreshing(false);
        }
    }

    private void showImageList(List<CloudImage> data) {
        adapter = new CloudImageAdapter(data);
        adapter.setOnItemClickListener(this);
        adapter.setDownload(download);
        adapter.setTv_selected(tv_selected);
        GridLayoutManager manager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(manager);
        defaultAdapter = new PictureFooterWrapper(adapter);
        View footerView = LayoutInflater.from(CloudImageActivity.this).inflate(R.layout.footer_picture,recyclerView,false);
        defaultAdapter.addFooterView(footerView);
        recyclerView.setAdapter(defaultAdapter);
        loadCompleted = true;
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
}
