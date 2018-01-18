package com.example.enlogty.communicationassistant.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.SyncTask;
import com.example.enlogty.communicationassistant.bean.UploadState;
import com.example.enlogty.communicationassistant.bean.User;
import com.example.enlogty.communicationassistant.rx.RxBus;
import com.example.enlogty.communicationassistant.service.SyncService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by enlogty on 2017/11/18.
 */

public class UploadImageUtil {
    public UploadImageUtil(Context context) {
        this.context = context;
    }

    private Context context;
    private  OSSFederationToken federationToken;
    private OSSAsyncTask task;
    private Object lock = new Object();
    public int successSize = 0;
    private final String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";

    public void uploadImage(OSS oss,PutObjectRequest put,final Context applicationContext,final String username, final String realFilePath,String type){
                //上传文件
                // 构造上传请求
                //PutObjectRequest put = new PutObjectRequest("file-enlogy", username+realFilePath, realFilePath);
                put.setBucketName("file-enlogy");
                put.setObjectKey(username+realFilePath);
                put.setUploadFilePath(realFilePath);

                // 异步上传时可以设置进度回调
                 task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        Log.d("PutObject", "UploadSuccess");
                        try {
                        OkHttpClient client = new OkHttpClient();
                           RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("mynumber", User.localNumber)
                                .addFormDataPart("path",realFilePath)
                                   .addFormDataPart("url","http://file-enlogy.oss-cn-shenzhen.aliyuncs.com/"+User.localNumber+realFilePath)
                                .addFormDataPart("type",type)
                                   .build();
                            String server_ip = context.getResources().getString(R.string.server_ip);
                            Request request2 = new Request.Builder().url(server_ip+"cloud/image/insert").post(requestBody).build();
                            Response response2 = client.newCall(request2).execute();
                            response2.close();

                                SyncTask.getInstance().PictureUploadedCount++;
                            Log.d("UploadSu时的数据SyncTask",SyncTask.getInstance().toString());
                            RxBus.getInstance().post(SyncTask.getInstance());
//                            if (response2.isSuccessful()){
//                                Log.d("post", "Success");
//                                SyncService.uploaded ++;
//                                response2.close();
//                            }else {
//                                response2.close();
//                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        //强制退出应用
                        Log.d("崩了","boom");
                        //UploadState.isFail = true;
                        //System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        Boolean canceledException = clientExcepion.isCanceledException();
                        Log.d("崩了","clientExcepion.isCanceledException()"+canceledException);
                        if (clientExcepion != null) {
                            // 本地异常如网络异常等
                            clientExcepion.printStackTrace();
                            //Log.d("崩了clientExcepion","clientExcepion---------boom");
                           // CloudImageUtil.uploadedData.clear();
                            //Log.d("崩了.clear()","clear"+CloudImageUtil.uploadedData.size());
                            //((SyncService)applicationContext).getBinder().synchronizeData();
                        }
                        if (serviceException != null) {
                            // 服务异常
                            serviceException.printStackTrace();
                            Log.e("ErrorCode", serviceException.getErrorCode());
                            Log.e("RequestId", serviceException.getRequestId());
                            Log.e("HostId", serviceException.getHostId());
                            Log.e("RawMessage", serviceException.getRawMessage());
                        }
                    }
                });

    }

}
