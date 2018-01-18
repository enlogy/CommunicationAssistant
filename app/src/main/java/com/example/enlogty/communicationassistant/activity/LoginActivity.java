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
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.domain.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by enlogty on 2017/8/15.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText phonenumber,password;
    private Button loginBn;
    private TextView createuser,forgetpassword;
    private String mPhoneNumber,mPassword;
    private ProgressDialog dialog;
    private final int PROGRESSDIALOG = 0;
    private final int TOASTSUCCESS = 1;
    private final int TOASTFAIL = 2;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case PROGRESSDIALOG:
                    dialog = new ProgressDialog(LoginActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("登录中");
                    dialog.show();
                    break;
                case TOASTSUCCESS:
                    dialog.dismiss();
                    //Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case TOASTFAIL:
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
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
        setContentView(R.layout.activity_login);
        phonenumber = (EditText) findViewById(R.id.login_phonenumber);
        password = (EditText) findViewById(R.id.login_password);
        loginBn = (Button) findViewById(R.id.login_button);
        createuser = (TextView) findViewById(R.id.tv_create_user);
        forgetpassword = (TextView) findViewById(R.id.tv_forget);
        loginBn.setOnClickListener(this);
        loginBn.setClickable(false);
        createuser.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
        phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!phonenumber.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                loginBn.setBackgroundColor(Color.parseColor("#1296db"));
            }else {
                loginBn.setBackgroundColor(Color.parseColor("#501296db"));
            }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!phonenumber.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    loginBn.setBackgroundColor(Color.parseColor("#1296db"));
                    loginBn.setClickable(true);
                }else {
                    loginBn.setBackgroundColor(Color.parseColor("#501296db"));
                    loginBn.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_create_user:
                Intent createIntent = new Intent(LoginActivity.this,CreateUserActivity.class);
                startActivity(createIntent);
                break;
            case R.id.tv_forget:
                Intent forgetIntent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(forgetIntent);
                break;
            case R.id.login_button:
                mPhoneNumber = phonenumber.getText().toString();
                mPassword = password.getText().toString();
                    handler.sendEmptyMessage(PROGRESSDIALOG);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder().url("http://192.168.1.112:9000/cloud/user/queryAll").get().build();
                                Response response = client.newCall(request).execute();
                                String jsonbody = response.body().string();
                                if (response.isSuccessful()){
                                    Gson gson = new Gson();
                                    List<User> users = gson.fromJson(jsonbody,new TypeToken<List<User>>(){}.getType());
                                    String sPhoneNumber;
                                    String sPassword;
                                    String sUsername;
                                    boolean isLogin = false;
                                    for (User user : users){
                                        sPhoneNumber= user.getPhoneNumber();
                                        sPassword = user.getPassword();
                                        if (mPhoneNumber.equals(sPhoneNumber)&&mPassword.equals(sPassword)){
                                            sUsername = user.getUsername();
                                            //密码正确
                                            handler.sendEmptyMessageDelayed(TOASTSUCCESS,1000);
                                            isLogin = true;
                                            Intent intent = new Intent();
                                            intent.putExtra("phonenumber",mPhoneNumber);
                                            intent.putExtra("username",sUsername);
                                            setResult(RESULT_OK,intent);
                                            return;
                                        }
                                    }
                                    //密码错误
                                    if (!isLogin)
                                    handler.sendEmptyMessageDelayed(TOASTFAIL,1000);

                                }else {
                                    handler.sendEmptyMessageDelayed(TOASTFAIL,1000);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();



                break;
            default:
                break;
        }
    }
}
