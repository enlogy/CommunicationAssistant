package com.example.enlogty.communicationassistant.utils;

import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.enlogty.communicationassistant.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by enlogty on 2017/10/3.
 */

public class MyCountDownTimer extends CountDownTimer{
    private Button bn;
    private Context mContext;
    private String phonenumber;
    private boolean isFirst = true;
    public MyCountDownTimer(Context context , long millisInFuture, long countDownInterval, Button bn, String phonenumber) {
        super(millisInFuture, countDownInterval);
        this.bn = bn;
        this.mContext = context;
        this.phonenumber = phonenumber;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (isFirst){
            isFirst = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                      OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url("http://192.168.1.112:9000/cloud/validCode?phoneNumber="+phonenumber).get().build();
                        Response response = client.newCall(request).execute();
                        response.close();
                        //Log.d("sendSms" ,  response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        bn.setClickable(false);
        bn.setBackground(mContext.getDrawable(R.drawable.code_bn_off_bg));
        bn.setTextColor(mContext.getResources().getColor(R.color.font_text));
        bn.setText("  "+millisUntilFinished/1000+"s后可重新发送  ");
    }

    @Override
    public void onFinish() {
        bn.setText("  重新获取验证码  ");
        bn.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        bn.setBackground(mContext.getDrawable(R.drawable.code_bn_on_bg));
        bn.setClickable(true);
        isFirst = true;
    }
}
