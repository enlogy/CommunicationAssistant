package com.example.enlogty.communicationassistant.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.base.BaseActivity;
import com.example.enlogty.communicationassistant.bean.Image;
import com.example.enlogty.communicationassistant.presenter.AboutPresener;
import com.example.enlogty.communicationassistant.utils.ThreadUtil;
import com.example.enlogty.communicationassistant.view.IAbout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.TooManyListenersException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by enlogty on 2018/1/17.
 */

public class AboutActivity extends BaseActivity<IAbout,AboutPresener> implements IAbout, View.OnClickListener {
    private UpdateHandler mHandler;
    private String updateApkPath;
    private int apkLength;
    private ProgressDialog dialog;
    private final int UPDATEAPK = 0x11;
    private ImageView iv;
    private TextView check;
    private TextView agreement;
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
        setContentView(R.layout.activity_about);
        initView();
        initData();
    }

    private void initView() {
        iv = findViewById(R.id.about_picture);
        check = findViewById(R.id.about_check);
        agreement = findViewById(R.id.about_agreement);
    }

    private void initData() {
        dialog = new ProgressDialog(AboutActivity.this);
        Glide.with(this).load(R.mipmap.about_picturen).into(iv);
        check.setOnClickListener(this);
        agreement.setOnClickListener(this);
    }

    @Override
    public void fetchUpdateApk(String url) {
        if (url.equals("fail")){
            Toast.makeText(this,"已经是最新版本",Toast.LENGTH_SHORT).show();
        }else{
            Log.d("AboutActivity","fetchUpdateApk");
                new AlertDialog.Builder(this)
                    .setTitle("版本更新")
                    .setMessage("检查到有新版本，是否更新?")
                        .setPositiveButton("取消",null)
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //访问网络下载apk
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("更新apk","onFailure");
                                    e.printStackTrace();
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                //子线程中
                                    Looper.prepare();
                                    mHandler = new UpdateHandler(Looper.getMainLooper());
                                    Log.d("更新apk","------");
                                    InputStream inputStream = response.body().byteStream();
                                    apkLength = new Long(response.body().contentLength()).intValue();
                                    //  /storage/emulated/0
                                    String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                                    String apkName = "/communicationassistant"+System.currentTimeMillis()+".apk";
                                    updateApkPath = downloadPath+apkName;
                                    //显示dialog窗口展示apk下载进度
                                    dialog.setTitle("Update ...");
                                    dialog.setCancelable(false);
                                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    dialog.setMax(apkLength);
                                    RandomAccessFile raf = new RandomAccessFile(updateApkPath,"rw");
                                    byte[] buffer = new byte[1024];
                                    int len = -1;
                                    while ((len = inputStream.read(buffer)) != -1){
                                        Log.d("更新apk","...");
                                        raf.write(buffer,0,len);
                                        Message msg = new Message();
                                        msg.what = UPDATEAPK;
                                        msg.obj = raf.length();
                                        mHandler.sendMessage(msg);
                                    }
                                    inputStream.close();
                                    raf.close();
                                    Log.d("更新apk","完成");

                                }
                            });
                        }
                    }).create().show();
        }

    }

    @Override
    public AboutPresener createPresenter() {
        return new AboutPresener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about_check:
                mPresenter.fetchUpdateApk(this);
                break;
            case R.id.about_agreement:
                Intent license = new Intent(AboutActivity.this,LicenseActivity.class);
                startActivity(license);
                break;
            default:
                break;
        }
    }

    class UpdateHandler extends Handler{

        public UpdateHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case UPDATEAPK:
                    int realLength = new Long((long)msg.obj).intValue();
                    if (!dialog.isShowing()){
                        dialog.setProgress(realLength);
                        dialog.show();
                    }else {
                        dialog.setProgress(realLength);
                    }
                    if (apkLength == realLength){
                        dialog.dismiss();
                        //启动安装apk
                        File file = new File(updateApkPath);
                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        if (Build.VERSION.SDK_INT >= 24) {
                            //provider authorities
                            Uri apkUri = FileProvider.getUriForFile(AboutActivity.this, "com.example.enlogty.communicationassistant.provider", file);
                            //Granting Temporary Permissions to a URI
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        }
                        AboutActivity.this.startActivity(intent);
                    }




                    break;
                default:
                    break;
            }
        }
    }
}
