package com.example.enlogty.communicationassistant.activity;

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.domain.ValidCode;
import com.example.enlogty.communicationassistant.utils.MyCountDownTimer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by enlogty on 2017/10/2.
 */

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private EditText regist_name,regist_phonenumber,regist_password,regist_password2,regist_code;
    private Button code_bn,regist_bn;
    private String phonenumber,username,password,code,password2;
    private TextInputLayout tl1;
    private TextInputLayout tl3;
    private TextInputLayout tl4;
    private final int PROGRESSDIALOG = 0;
    private final int TOASTSUCCESS = 1;
    private final int TOASTFail = 2;
    private ProgressDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case PROGRESSDIALOG:
                    dialog = new ProgressDialog(CreateUserActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("注册中，请稍等。。。");
                    dialog.show();
                    break;
                case TOASTSUCCESS:
                    dialog.dismiss();
                    Toast.makeText(CreateUserActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case TOASTFail:
                    dialog.dismiss();
                    Toast.makeText(CreateUserActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_createuser);
        initView();
    }

    private void initView() {
        regist_name = (EditText) findViewById(R.id.regist_name);
        regist_name.addTextChangedListener(this);
        regist_phonenumber = (EditText) findViewById(R.id.regist_phonenumber);
        regist_phonenumber.addTextChangedListener(this);
        regist_password = (EditText) findViewById(R.id.regist_password);
        regist_password.addTextChangedListener(this);
        regist_password2 = (EditText) findViewById(R.id.regist_password2);
        regist_password2.addTextChangedListener(this);
        regist_code = (EditText) findViewById(R.id.regist_code);
        regist_code.addTextChangedListener(this);
        code_bn = (Button) findViewById(R.id.code_bn);
        code_bn.setOnClickListener(this);
        regist_bn = (Button) findViewById(R.id.regist_bn);
        regist_bn.setOnClickListener(this);
        tl1 = (TextInputLayout) findViewById(R.id.til1);
        tl3 = (TextInputLayout) findViewById(R.id.til3);
        tl4 = (TextInputLayout) findViewById(R.id.til4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.code_bn:
                phonenumber = regist_phonenumber.getText().toString();
                if (phonenumber.length()==11)
                    new MyCountDownTimer(this,60000,1000,code_bn,phonenumber).start();
                break;
            case R.id.regist_bn:
                phonenumber = regist_phonenumber.getText().toString();
                username = regist_name.getText().toString();
                password = regist_password.getText().toString();
                password2 = regist_password2.getText().toString();
                code = regist_code.getText().toString();

                //访问服务器验证手机号和验证码
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url("http://192.168.1.112:9000/cloud/findCode").get().build();
                            Response response = client.newCall(request).execute();
                            String body = response.body().string();
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                List<ValidCode> validCodes = gson.fromJson(body, new TypeToken<List<ValidCode>>() {
                                }.getType());
                                for (ValidCode mcode : validCodes){
                                    if (mcode.getPhonenumber().equals(phonenumber)&&mcode.getValidcode() == Integer.parseInt(code)){
                                        if (checkUsername(username,4,20)&checkPassword(password)&password.equals(password2)){
                                            //开启动画
                                            handler.sendEmptyMessage(PROGRESSDIALOG);

                                            //满足注册条件进行注册
                                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                                    .addFormDataPart("username",username)
                                                    .addFormDataPart("password",password)
                                                    .addFormDataPart("phoneNumber",phonenumber)
                                                    .build();
                                            Request request2 = new Request.Builder().url("http://192.168.1.112:9000/cloud/user/insert").post(requestBody).build();
                                            final Response response2 = client.newCall(request2).execute();
                                            if (response2.isSuccessful()){
                                                //响应成功
                                                Log.d("insertUser" ,  "success");
                                                handler.sendEmptyMessageDelayed(TOASTSUCCESS,1500);
                                            }else {
                                                handler.sendEmptyMessageDelayed(TOASTFail,1000);
                                            }


                                        }
                                    }else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(CreateUserActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                //开启动画
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //code
        if (regist_phonenumber.getText().toString().length() == 11){
            code_bn.setBackground(getDrawable(R.drawable.code_bn_on_bg));
            code_bn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }else {
            code_bn.setBackground(getDrawable(R.drawable.code_bn_off_bg));
            code_bn.setTextColor(getResources().getColor(R.color.font_text));
        }
        //regist
        if (!regist_name.getText().toString().isEmpty()&&!regist_phonenumber.getText().toString().isEmpty()&&
                !regist_password.getText().toString().isEmpty()&&!regist_password2.getText().toString().isEmpty()&&
                !regist_code.getText().toString().isEmpty()){
            regist_bn.setBackground(getResources().getDrawable(R.drawable.regist_bn_on_bg));
            regist_bn.setClickable(true);
        }else {
            regist_bn.setBackground(getResources().getDrawable(R.drawable.regist_bn_off_bg));
            regist_bn.setClickable(false);
        }
        //检测用户名格式和密码格式
        if (!checkUsername(regist_name.getText().toString(),4,20)&!regist_name.getText().toString().isEmpty()){
            tl1.setErrorEnabled(true);
            tl1.setError("       取值范围4-20位为a-z,A-Z,0-9,\"_\",汉字，不能以\"_\"结尾");
        }else {
            tl1.setErrorEnabled(false);

        }
        if (!checkPassword(regist_password.getText().toString())&!regist_password.getText().toString().isEmpty()){
            tl3.setError("       取值范围6-16位为a-z,A-Z,0-9");
            tl3.setErrorEnabled(true);
        }else {
            tl3.setErrorEnabled(false);
        }
        if (!regist_password.getText().toString().equals(regist_password2.getText().toString())&!regist_password2.getText().toString().isEmpty()&!regist_password.getText().toString().isEmpty()){
            tl4.setErrorEnabled(true);
            tl4.setError("       两次输入密码不一致");
        }else {
            tl4.setErrorEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    public boolean startCheck(String reg,String string)
    {
          boolean tem=false;

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher=pattern.matcher(string);

        tem=matcher.matches();
        return tem;
        }

              /**  /^[a-zA-Z0-9_-]{4,16}$/
          * 检验用户名 
          * 取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾 
          * 用户名有最小长度和最大长度限制，比如用户名必须是4-20位 
          * */
             public boolean checkUsername(String username,int min,int max)
             {
              String regex="[\\w\u4e00-\u9fa5]{"+min+","+max+"}(?<!_)";
              return startCheck(regex,username);
            }
    public boolean checkPassword(String password)
    {
        String regex="[a-zA-Z0-9_-]{6,16}";
        return startCheck(regex,password);
    }
}
