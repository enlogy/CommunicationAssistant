package com.example.enlogty.communicationassistant.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.bean.Image;
import com.example.enlogty.communicationassistant.bean.SyncTask;
import com.example.enlogty.communicationassistant.bean.User;
import com.example.enlogty.communicationassistant.domain.CloudImage;
import com.example.enlogty.communicationassistant.domain.DataContact;
import com.example.enlogty.communicationassistant.domain.Sms;
import com.example.enlogty.communicationassistant.rx.RxBus;
import com.example.enlogty.communicationassistant.service.SyncService;
import com.example.enlogty.communicationassistant.utils.CloudContactsUtil;
import com.example.enlogty.communicationassistant.utils.CloudImageUtil;
import com.example.enlogty.communicationassistant.utils.ContactsUtil;
import com.example.enlogty.communicationassistant.utils.ImageUtil;
import com.example.enlogty.communicationassistant.utils.MediaScanner;
import com.example.enlogty.communicationassistant.utils.SmsUtil;
import com.tinkerpatch.sdk.TinkerPatch;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;//
    private DrawerLayout drawerLayout;
    private ImageView navBackImageView , navSettingsImageView;
    private de.hdodenhof.circleimageview.CircleImageView headicon;
    private NavigationView navigationView;
    private View headerView;
    private boolean isInit = false;
    private ImageView tongbu;
    private TextView usernameTv;
    private TextView phonenumberTv;
    private TextView mainTv1;
    private TextView mainTv2;
    private TextView lastDate;
    private TextView phoneTv;
    private TextView phoneCloudTv;
    private TextView smsTv;
    private TextView smsCloudTv;
    private TextView pictureTv;
    private TextView pictureCloudTv;
    private Subscription subscribe;
    private boolean isLogin = false;
    private boolean isExit = false;
    private final int EXIT = 0;
    private String mphonenumber;
    private RotateAnimation animation;
    private Object lock = new Object();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EXIT:
                    isExit = false;
                    break;
                default:
                    break;
            }
        }
    };
    private SyncService.ImageBinder imageBinder;
    private ServiceConnection connection =new  ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            imageBinder = (SyncService.ImageBinder)iBinder;
            //imageBinder.synchronizeData();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","onCreate");
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

        setContentView(R.layout.activity_main);
        initView();
        Log.d("onScanCompleted前",Environment.getExternalStorageDirectory().getAbsolutePath());

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("手机云助手");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        }
        //icon 图标恢复颜色
        navigationView.setItemIconTintList(null);
        initData();
        syncSimpleData();
        MainActivityPermissionsDispatcher.getPermissionsWithCheck(this);
        getPermissions();
        showState();
       // Toast.makeText(this,"TinkerPath",Toast.LENGTH_SHORT).show();
        //TinkerPatch.with().fetchPatchUpdate(true);
    }

    private void showState() {
        //boolean notification = getIntent().getBooleanExtra("notification", false);
        //Log.d("notification",notification+"-----------+");
        SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
        boolean state = spn.getBoolean("state", false);
        boolean exit = spn.getBoolean("exit", false);
        SharedPreferences sp = getSharedPreferences("state_prf",MODE_PRIVATE);
        boolean synced = sp.getBoolean("synced", false);
        boolean downloading = SyncTask.getInstance().Downloading;
//        if (subscribe==null&&state&&downloading&&!synced){
        if (subscribe==null&&downloading){
            Log.d("showState","subscribe--->showState");
            tongbu.setClickable(false);
            startAnimation();
            mainTv1.setVisibility(View.VISIBLE);
            mainTv2.setVisibility(View.VISIBLE);
            subscribe = RxBus.getInstance().tObservable(SyncTask.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SyncTask>() {
                        @Override
                        public void call(SyncTask syncTask) {
                            //创建systemUi的notification常驻直到完成任务
                            //点击notification，进去mainactivty，并设置按钮不可点击，判断动画状态，开启动画

                            Log.d("rxjava","Call"+syncTask.toString());
                            int taskTotal = syncTask.getTaskTotal();
                            int taskedTotal = syncTask.getTaskedTotal();
                            Log.d("任务详情","任务数"+taskTotal+","+taskedTotal);
                            float adv = (float)taskedTotal/(float)taskTotal;
                            String tv2Content = (int)(adv *100) + "%";
                            Log.d("tv2Content的值",tv2Content);
                            mainTv2.setText(tv2Content);
                            Log.d("TextView的值",mainTv2.getText().toString());
                            //mainTv2.
                            if (taskTotal == taskedTotal || taskTotal==0){
                                mainTv1.setVisibility(View.GONE);
                                mainTv2.setVisibility(View.GONE);
                                tongbu.setClickable(true);
                                animation.cancel();
                                Toast.makeText(MainActivity.this,"同步完成",Toast.LENGTH_SHORT).show();
                                //设置最后一次同步的时间
                                long timeMillis = System.currentTimeMillis();
                                Date date = new Date(timeMillis);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String time = sdf.format(date);
                                SharedPreferences sp = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("lasttime",time);
                                edit.commit();
                                //设置同步后的数据
                                syncSimpleData();
                                //设置当前状态为 非同步中
                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor editn = spn.edit();
                                editn.putBoolean("state",false);
                                editn.commit();
                                //设置exit
                                SharedPreferences exitsp = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor exitedit = exitsp.edit();
                                exitedit.putBoolean("exit",true);
                                exitedit.commit();
                            }

                        }
                    });
        }
        SharedPreferences.Editor editn = spn.edit();
        editn.putBoolean("exit",false);
        editn.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission({Manifest.permission.READ_SMS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_CONTACTS})
     void getPermissions() {
    }
    //{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS}
    @OnPermissionDenied({Manifest.permission.READ_SMS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_CONTACTS})
     void showDenied() {
        Toast.makeText(this,"你拒绝了改权限！",Toast.LENGTH_SHORT).show();
    }
    @OnShowRationale({Manifest.permission.READ_SMS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_CONTACTS})
     void showWhy(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("不开启相关权限，应用将无法正常工作！")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();//此时会出现不在询问对话框
                    }
                }).show();
    }
    @OnNeverAskAgain({Manifest.permission.READ_SMS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_CONTACTS})
        void showNotAsk(){
        new AlertDialog.Builder(this)
        .setMessage("该应用需要访问核心的权限，不开启将无法正常工作！")
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialogInterface, int i) {
        }
        }).show();
        }
private void initData() {
        SharedPreferences sp = getSharedPreferences("user_pref",MODE_PRIVATE);
        String username = sp.getString("username", null);
        String localnumber = sp.getString("localnumber", null);
        if (localnumber!=null){
        usernameTv.setText(username);
        phonenumberTv.setText(localnumber);
        isLogin = true;
        headicon.setClickable(false);
        headicon.setImageResource(R.mipmap.ic_user2);
        User.setLocalNumber(localnumber);
        }
        Intent serviceIntent = new Intent(MainActivity.this,SyncService.class);
        bindService(serviceIntent,connection,BIND_AUTO_CREATE);
        }

    @SuppressLint("WrongViewCast")
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navBackImageView = (ImageView) headerView.findViewById(R.id.nav_back);
        navBackImageView.setOnClickListener(this);
        navSettingsImageView = (ImageView) headerView.findViewById(R.id.nav_settings);
        navSettingsImageView.setOnClickListener(this);
        headicon = (CircleImageView) headerView.findViewById(R.id.user_headicon);
        headicon.setOnClickListener(this);
        usernameTv = (TextView) headerView.findViewById(R.id.m_username);
        phonenumberTv = (TextView) headerView.findViewById(R.id.m_phonenumber);
        tongbu = (ImageView) findViewById(R.id.tongbu);
        tongbu.setOnClickListener(this);
        mainTv1 = findViewById(R.id.main_tv1);
        mainTv2 = findViewById(R.id.main_tv2);
        lastDate = findViewById(R.id.rl1_tv2);
        phoneTv = findViewById(R.id.rl3_tv1);
        phoneCloudTv = findViewById(R.id.rl3_tv2);
        smsTv = findViewById(R.id.rl4_tv1);
        smsCloudTv = findViewById(R.id.rl4_tv2);
        pictureTv = findViewById(R.id.rl2_tv1);
        pictureCloudTv = findViewById(R.id.rl2_tv2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_back:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.user_headicon:
                if (!isLogin){
                    Intent headiconIntent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(headiconIntent,1);
                }
                break;
            case R.id.tongbu:
                boolean notification = getIntent().getBooleanExtra("notification", false);
                SharedPreferences sp = getSharedPreferences("loginInfo_pref",MODE_PRIVATE);
                String phonenumber = sp.getString("phonenumber", "");
                boolean islogin = sp.getBoolean("isLogin", false);

                SharedPreferences spm = getSharedPreferences("state_prf",MODE_PRIVATE);
                boolean synced = spm.getBoolean("synced", false);
                if (islogin){
                    SharedPreferences cb_sp = getSharedPreferences("cb_pref", MODE_PRIVATE);
                    boolean cb_contact = cb_sp.getBoolean("cb3", true);
                    boolean cb_sms = cb_sp.getBoolean("cb4", true);
                    boolean cb_image = cb_sp.getBoolean("cb5", true);
                        if (!cb_contact&&!cb_sms&&!cb_image){
                            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Tips")
                                    .setMessage("请进入设置->开启同步选项")
                                    .setCancelable(false)
                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                                            startActivity(settings);
                                        }
                                    }).create();
                            dialog.show();
                    }else if (!synced&&isInit){
                        //创建notification 显示同步进度
                        SyncTask.getInstance().Downloading = true;
                        tongbu.setClickable(false);
                        startAnimation();
                        mainTv1.setVisibility(View.VISIBLE);
                        mainTv2.setVisibility(View.VISIBLE);
                        if (subscribe==null){
                            subscribe = RxBus.getInstance().tObservable(SyncTask.class)
                                    //.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<SyncTask>() {
                                        @Override
                                        public void call(SyncTask syncTask) {
                                            //创建systemUi的notification常驻直到完成任务
                                            //点击notification，进去mainactivty，并设置按钮不可点击，判断动画状态，开启动画

                                            Log.d("rxjava","Call"+syncTask.toString());
                                            int taskTotal = syncTask.getTaskTotal();
                                            int taskedTotal = syncTask.getTaskedTotal();
                                            Log.d("任务详情","任务数"+taskTotal+","+taskedTotal);
                                            float adv = (float)taskedTotal/(float)taskTotal;
                                            String tv2Content = (int)(adv *100) + "%";
                                            Log.d("tv2Content的值",tv2Content);
                                            mainTv2.setText(tv2Content);
                                            Log.d("TextView的值",mainTv2.getText().toString());
                                            //mainTv2.
                                            if (taskTotal == taskedTotal){
                                                if (taskTotal>0){
                                                SyncTask.getInstance().Downloading = false;
                                                mainTv1.setVisibility(View.GONE);
                                                mainTv2.setVisibility(View.GONE);
                                                tongbu.setClickable(true);
                                                animation.cancel();
                                                    Toast.makeText(MainActivity.this,"同步完成",Toast.LENGTH_SHORT).show();
                                                }

                                                //设置最后一次同步的时间
                                                long timeMillis = System.currentTimeMillis();
                                                Date date = new Date(timeMillis);
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                String time = sdf.format(date);
                                                SharedPreferences sp = getSharedPreferences("state_prf",MODE_PRIVATE);
                                                SharedPreferences.Editor edit = sp.edit();
                                                edit.putString("lasttime",time);
                                                edit.commit();
                                                //设置同步后的数据
                                                syncSimpleData();
                                                //设置当前状态为 非同步中
                                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                                SharedPreferences.Editor editn = spn.edit();
                                                editn.putBoolean("state",false);
                                                editn.commit();
//
                                            }

                                        }
                                    });
                        }
                        SyncTask.getInstance().reset();
                        RxBus.getInstance().post(SyncTask.getInstance());
                        imageBinder.setSync(true);
                        imageBinder.synchronizeData();
                        SyncTask.getInstance().Downloading = true;
                        //设置当前状态为同步中
                        SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                        SharedPreferences.Editor edit = spn.edit();
                        edit.putBoolean("state",true);
                        edit.commit();
                    }else if (synced&&isInit){
                        Toast.makeText(MainActivity.this,"已同步最新数据",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(loginIntent,1);
                }

                //同步contacts

                //同步picture

                //同步sms

                break;
            default:
                break;
        }
    }

    private void syncSimpleData() {
        isInit = true;
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        Log.d("扫描的路径",path);
//        sendBroadcast(ne w Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
        SharedPreferences sp1 = getSharedPreferences("state_prf",MODE_PRIVATE);
        String time = sp1.getString("lasttime", "2017-01-01");
        lastDate.setText(time);
        SharedPreferences sp2 = getSharedPreferences("loginInfo_pref",MODE_PRIVATE);
        boolean isLogin = sp2.getBoolean("isLogin", false);
        if (isLogin){
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    synchronized (lock){
                        int    localContactSize = new ContactsUtil().getContactsData(getContentResolver()).size();
//                        for (int i=0;i<localContactSize;i++){
//                            Log.d("本地联系人数据","i = "+i+"---"+new ContactsUtil().getContactsData(getContentResolver()).get(i).toString());
//                        }
                        int   cloudContactSize = new CloudContactsUtil(MainActivity.this).getRemoteData(User.localNumber).size();
                        int   localSmsSize = new SmsUtil(MainActivity.this).querrySmsToList(getContentResolver()).size();
                        int   cloudSmsSize = new SmsUtil(MainActivity.this).findRemoteDataAll().size();
                        int   localPictureSize = new ImageUtil().getImagePath(getContentResolver()).size();
                        int   cloudPictureSize = new CloudImageUtil(MainActivity.this).getCloudImageData(User.localNumber).size();
                        Log.d("syncSimpleData初步数据",localContactSize+","+cloudContactSize+","+localSmsSize+","+cloudSmsSize+","+localPictureSize+","+cloudPictureSize);
                        SharedPreferences sp = getSharedPreferences("state_prf",MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        SharedPreferences cb_sp = getSharedPreferences("cb_pref", MODE_PRIVATE);
                        boolean cb_contact = cb_sp.getBoolean("cb3", true);
                        boolean cb_sms = cb_sp.getBoolean("cb4", true);
                        boolean cb_image = cb_sp.getBoolean("cb5", true);
                        if (cb_contact&&cb_sms&&cb_image){
                            if (localContactSize==cloudContactSize&&localSmsSize==cloudSmsSize&&localPictureSize==cloudPictureSize){
                                edit.putBoolean("synced",true);
                                edit.commit();
                                //设置当前状态为 非同步中
                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor editn = spn.edit();
                                editn.putBoolean("state",false);
                                editn.commit();
                            }
                        }
                        else if (!cb_contact&&!cb_sms&&!cb_image){
                            edit.putBoolean("synced",false);
                            edit.commit();
                        }else if (cb_contact&&cb_sms&&!cb_image){
                            if (localContactSize==cloudContactSize&&localSmsSize==cloudSmsSize){
                                edit.putBoolean("synced",true);
                                edit.commit();
                                //设置当前状态为 非同步中
                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor editn = spn.edit();
                                editn.putBoolean("state",false);
                                editn.commit();
                            }else {
                                edit.putBoolean("synced",false);
                                edit.commit();
                            }
                        }else if (cb_contact&&!cb_sms&&cb_image){
                            if (localContactSize==cloudContactSize&&localPictureSize==cloudPictureSize){
                                edit.putBoolean("synced",true);
                                edit.commit();
                                //设置当前状态为 非同步中
                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor editn = spn.edit();
                                editn.putBoolean("state",false);
                                editn.commit();
                            }else {
                                edit.putBoolean("synced",false);
                                edit.commit();
                            }
                        }else if (!cb_contact&&cb_sms&&cb_image){
                            if (localSmsSize==cloudSmsSize&&localPictureSize==cloudPictureSize){
                                edit.putBoolean("synced",true);
                                edit.commit();
                                //设置当前状态为 非同步中
                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor editn = spn.edit();
                                editn.putBoolean("state",false);
                                editn.commit();
                            }else {
                                edit.putBoolean("synced",false);
                                edit.commit();
                            }
                        }else if (cb_contact&&!cb_sms&&!cb_image){
                            if (localContactSize==cloudContactSize){
                                edit.putBoolean("synced",true);
                                edit.commit();
                                //设置当前状态为 非同步中
                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor editn = spn.edit();
                                editn.putBoolean("state",false);
                                editn.commit();
                            }else {
                                edit.putBoolean("synced",false);
                                edit.commit();
                            }
                        }else if (!cb_contact&&cb_sms&&!cb_image){
                            if (localSmsSize==cloudSmsSize){
                                edit.putBoolean("synced",true);
                                edit.commit();
                                //设置当前状态为 非同步中
                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor editn = spn.edit();
                                editn.putBoolean("state",false);
                                editn.commit();
                            }else {
                                edit.putBoolean("synced",false);
                                edit.commit();
                            }
                        }else if (!cb_contact&&!cb_sms&&cb_image){
                            if (localPictureSize==cloudPictureSize){
                                edit.putBoolean("synced",true);
                                edit.commit();
                                //设置当前状态为 非同步中
                                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                                SharedPreferences.Editor editn = spn.edit();
                                editn.putBoolean("state",false);
                                editn.commit();
                            }else {
                                edit.putBoolean("synced",false);
                                edit.commit();
                            }
                        }

//                    List<Contact> contactsData = new ContactsUtil().getContactsData(getContentResolver());
//                    List<DataContact> remoteData = CloudContactsUtil.getRemoteData(User.localNumber);
//                    ArrayList<Sms> sms = new SmsUtil().querrySmsToList(getContentResolver());
//                    List<Sms> remoteDataAll = new SmsUtil().findRemoteDataAll();
//                    ArrayList<Image> imagePath = new ImageUtil().getImagePath(getContentResolver());
//                    List<CloudImage> cloudImageData = CloudImageUtil.getCloudImageData(User.localNumber);

                        // Log.d()
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //设置当前的联系人数据
                                phoneTv.setText(localContactSize+"");
                                phoneCloudTv.setText(cloudContactSize+"");
                                //设置当前短信数据
                                smsTv.setText(localSmsSize+"");
                                smsCloudTv.setText(cloudSmsSize+"");
                                //设置当前照片数据
                                pictureTv.setText(localPictureSize+"");
                                pictureCloudTv.setText(cloudPictureSize+"");
                            }
                        });
                    }
                    }

            }).start();
        }
    }

    private void startAnimation() {
        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1*1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        tongbu.startAnimation(animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK){
                    String username = data.getStringExtra("username");
                    mphonenumber = data.getStringExtra("phonenumber");
                    Log.d("onActivityResult",username+mphonenumber);
                    usernameTv.setText(username);
                    phonenumberTv.setText(mphonenumber);
                    isLogin = true;
                    headicon.setClickable(false);
                    headicon.setImageResource(R.mipmap.ic_user2);
                    User.setLocalNumber(mphonenumber);
                    //保存密码
                    SharedPreferences sp = getSharedPreferences("user_pref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username",username);
                    editor.putString("localnumber",mphonenumber);
                    editor.commit();
                    SharedPreferences sp2 = getSharedPreferences("loginInfo_pref",MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sp2.edit();
                    editor2.putBoolean("isLogin",true);
                    editor2.putString("phonenumber",mphonenumber);
                    editor2.commit();
                    syncSimpleData();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncSimpleData();
        Log.d("onStart","onStart");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            if (!isExit){
                Toast.makeText(MainActivity.this,"再次点击退出",Toast.LENGTH_SHORT).show();
                isExit = true;
                handler.sendEmptyMessageDelayed(EXIT,2000);
            }else {
                SharedPreferences spn = getSharedPreferences("state_prf",MODE_PRIVATE);
                SharedPreferences.Editor editn = spn.edit();
                editn.putBoolean("exit",true);
                editn.commit();
                finish();
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_contact:
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setData(Contacts.People.CONTENT_URI);
//                startActivity(intent);
                startActivity(new Intent(MainActivity.this,ContactsActivity.class));
                break;
            case R.id.nav_cloud:
                if (isLogin){
                    Intent cloudIntent = new Intent(MainActivity.this,CloudDataActivity.class);
                    cloudIntent.putExtra("localNumber",mphonenumber);
                    startActivity(cloudIntent);
                }else {
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(loginIntent,1);
                }
                break;
            case R.id.nav_myimage:
                Intent imageintent = new Intent(MainActivity.this,ImageActivity.class);
                startActivity(imageintent);
                break;
            case R.id.nav_sms:
                Intent sintent = new Intent(Intent.ACTION_MAIN);
                sintent.addCategory(Intent.CATEGORY_DEFAULT);
                sintent.setType("vnd.android-dir/mms-sms");
                startActivity(sintent);
                break;
            case R.id.nav_about:
                    Intent about = new Intent(MainActivity.this,AboutActivity.class);
                    startActivity(about);
                break;
            case R.id.nav_exit:
                if (isLogin){
                    isLogin = false;
                    headicon.setClickable(true);
                    headicon.setImageResource(R.mipmap.ic_user);
                    usernameTv.setText("手机云助手");
                    phonenumberTv.setText("00000000000");
                    SharedPreferences sp = getSharedPreferences("user_pref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username",null);
                    editor.putString("localnumber",null);
                    editor.commit();
                    SharedPreferences sp2 = getSharedPreferences("loginInfo_pref",MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sp2.edit();
                    editor2.putBoolean("isLogin",false);
                    editor2.putString("phonenumber","00000000000");
                    editor2.commit();
                    Toast.makeText(MainActivity.this,"注销成功",Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backup_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                 drawerLayout.openDrawer(GravityCompat.START);
               // Intent intent = new Intent(MainActivity.this,LoginActivity.class);
               // startActivity(intent);
                break;
            case R.id.backup_menu:
                Intent syncNoteIntent = new Intent(MainActivity.this,SyncNoteActivity.class);
                startActivity(syncNoteIntent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity","onDestroy");
        if (subscribe!=null &&!subscribe.isUnsubscribed()){
            subscribe.unsubscribe();
        }
        if (connection!=null){
            unbindService(connection);
        }
       /* Intent intent = new Intent(MainActivity.this,SyncService.class);
        stopService(intent);*/
    }
}
