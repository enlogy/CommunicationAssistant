package com.example.enlogty.communicationassistant.activity;


import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.adapter.CloudContactAdapter;
import com.example.enlogty.communicationassistant.adapter.CloudContactDecoration;
import com.example.enlogty.communicationassistant.adapter.CloudSmsAdapter;
import com.example.enlogty.communicationassistant.adapter.HeaderAndFooterWrapper;
import com.example.enlogty.communicationassistant.adapter.UnderlineDecoration;
import com.example.enlogty.communicationassistant.base.BaseActivity;
import com.example.enlogty.communicationassistant.bean.User;
import com.example.enlogty.communicationassistant.domain.DataContact;
import com.example.enlogty.communicationassistant.domain.Sms;
import com.example.enlogty.communicationassistant.presenter.CloudContactPresenter;
import com.example.enlogty.communicationassistant.presenter.CloudSmsPresenter;
import com.example.enlogty.communicationassistant.utils.CloudContactsUtil;
import com.example.enlogty.communicationassistant.utils.ContactsUtil;
import com.example.enlogty.communicationassistant.utils.SmsUtil;
import com.example.enlogty.communicationassistant.view.ICloudContact;
import com.example.enlogty.communicationassistant.view.ISms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enlogty on 2017/11/22.
 */

public class CloudSmsActivity extends BaseActivity<ISms,CloudSmsPresenter> implements ISms{
    private TextView  textView;
    private TextView  tv_selected;
    private TextView  all_select;
    private String localNumber;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Sms> selectSmss;
    private List<Sms> AllDatas;
    private boolean AllSelected = false;
    private int cloudSmsSize;
    private boolean loadCompleted;
    private CloudSmsAdapter adapter;
    private HeaderAndFooterWrapper defaultAdapter;
    private ImageView download;
    private final int INITDATALIST = 0;
    private final int SWIPEREFRESH = 1;
    private final int SUCCESSPROGRESS = 2;
    private ProgressDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case INITDATALIST:
                    List<Sms> smsList = (List<Sms>) msg.obj;
                    initDataList(smsList);

                    break;
                case SWIPEREFRESH:
                    List<Sms> smslist = (List<Sms>) msg.obj;
                    if (smslist.size()>0){
                        adapter.setCloudSmsList(smslist);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    break;
                case SUCCESSPROGRESS:
                    int count = (int) msg.obj;
                    showProgressbar(count);
                    break;
                default:
                    break;
            }
        }
    };

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

    @Override
    public CloudSmsPresenter createPresenter() {
        return new CloudSmsPresenter();
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
        setContentView(R.layout.activity_cloudsms);
        initView();
        mPresenter.fetchSms(this);

    }

    @Override
    public void loadSms(List<Sms> smsList) {
        Message msg = new Message();
        msg.what = INITDATALIST;
        msg.obj = smsList;
        handler.sendMessage(msg);
    }


    public interface MainListener{
        void onMain();
    }

    private void initView() {
        tv_selected = (TextView) findViewById(R.id.tv_selected);
        all_select = (TextView) findViewById(R.id.all_select);
        all_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadCompleted){
                    if (AllSelected){
                        all_select.setText("全选");
                        AllSelected = false;
                        tv_selected.setText("已选("+0+")");
                        int count = adapter.bo.length;
                        for (int i=0 ;i<count ;i++){
                            adapter.bo[i] = false;
                        }
                        defaultAdapter.notifyDataSetChanged();

                        download.setBackgroundResource(R.mipmap.bk_off);
                        download.setClickable(false);
                    }else {

                        //全选状态
                        all_select.setText("全不选");
                        AllSelected = true;
                        tv_selected.setText("已选("+cloudSmsSize+")");
                        int count = adapter.bo.length;
                        for (int i=0 ;i<count ;i++){
                            adapter.bo[i] = true;
                        }
                        defaultAdapter.notifyDataSetChanged();

                        download.setBackgroundResource(R.mipmap.bk_on);
                        download.setClickable(true);
                    }
                }
            }
        });
        textView = (TextView) findViewById(R.id.tv_hint);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        swipeRefreshLayout  = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Sms> smsList = new SmsUtil(CloudSmsActivity.this).findRemoteDataAll();
                        Message msg = new Message();
                        msg.what = SWIPEREFRESH;
                        msg.obj = smsList;
                        handler.sendMessage(msg);
                    }
                }).start();
            //刷新成功

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rv_cloudcontact);
        download = (ImageView) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Sms> list = adapter.getSelectSmsList();
                SmsUtil util = new SmsUtil(CloudSmsActivity.this);
                ArrayList<Sms> smsList = util.querrySmsToList(getContentResolver());
                dialog.setMax(list.size());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i =0;i<list.size();i++){
                            Sms sms = list.get(i);
                            util.writeToSmsDatabase(getContentResolver(), smsList, sms.getAddress(), sms.getType(), sms.getDate(), sms.getBody(), new SmsUtil.recoverSmsLinstener() {
                                @Override
                                public void onSuccess(int count) {
                                    Message msg = new Message();
                                    msg.obj = count;
                                    msg.what = SUCCESSPROGRESS;
                                    handler.sendMessage(msg);
                                }
                            });
                        }
                    }
                }).start();
            }
        });
         dialog = new ProgressDialog(CloudSmsActivity.this);
         dialog.setTitle("恢复进度");
         dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         dialog.setCancelable(false);
    }


    private void initDataList( List<Sms> smsList) {
        cloudSmsSize = smsList.size();
        selectSmss = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new UnderlineDecoration());
        adapter = new CloudSmsAdapter(this,smsList);
        adapter.setTv_selected(tv_selected);
        adapter.setDownload(download);
        defaultAdapter = new HeaderAndFooterWrapper(adapter);
        View footerView = LayoutInflater.from(CloudSmsActivity.this).inflate(R.layout.footer_sms,recyclerView,false);
        defaultAdapter.addFooterView(footerView);
        recyclerView.setAdapter(defaultAdapter);
        loadCompleted = true;
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }
}
