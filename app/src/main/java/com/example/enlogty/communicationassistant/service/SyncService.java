package com.example.enlogty.communicationassistant.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.activity.MainActivity;
import com.example.enlogty.communicationassistant.activity.SettingsActivity;
import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.bean.Image;
import com.example.enlogty.communicationassistant.bean.SyncTask;
import com.example.enlogty.communicationassistant.bean.UploadState;
import com.example.enlogty.communicationassistant.bean.User;
import com.example.enlogty.communicationassistant.domain.CloudImage;
import com.example.enlogty.communicationassistant.domain.DataContact;
import com.example.enlogty.communicationassistant.domain.Sms;
import com.example.enlogty.communicationassistant.rx.RxBus;
import com.example.enlogty.communicationassistant.sqlite.MySQLiteOpenHelper;
import com.example.enlogty.communicationassistant.utils.CloudContactsUtil;
import com.example.enlogty.communicationassistant.utils.CloudImageUtil;
import com.example.enlogty.communicationassistant.utils.ContactsUtil;
import com.example.enlogty.communicationassistant.utils.ImageUtil;
import com.example.enlogty.communicationassistant.utils.MediaScanner;
import com.example.enlogty.communicationassistant.utils.SmsUtil;
import com.example.enlogty.communicationassistant.utils.StringUtil;
import com.example.enlogty.communicationassistant.utils.UploadImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by enlogty on 2017/12/2.
 */

public class SyncService extends Service{
    private Object lock = new Object();
    public int downloadTotal;
    public int uploadTotal;
    private boolean uploading;
    private boolean downloading;
    public static int uploaded;
    public static int downloaded;
    //服务线程池
    //private ExecutorService service;
    private UploadImageUtil uploadImageUtil;

    public ImageBinder getBinder() {
        return binder;
    }

    private ImageBinder binder = new ImageBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //service = Executors.newFixedThreadPool(3);
        uploadImageUtil = new UploadImageUtil(SyncService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopForeground(1);
        Log.d("SyncService","onDestroy");
    }

    private  void uploadPicture(OSS oss, PutObjectRequest put,Context applicationContext, String localPath, String type, List<CloudImage> data) {
                if (data != null){
                    for (CloudImage image : data){
                        if (image.getPath().toLowerCase().equals(localPath.toLowerCase())){
                            return;
                        }
                    }
                }
                Log.d("uploadPicture",localPath);
//                service.submit(new Runnable() {
//                    @Override
//                    public void run() {

        if (!new File(localPath).exists()){
                    Log.d("上传","fileNotFound");
                 return;
        }
                        uploadImageUtil.uploadImage(oss,put,getApplicationContext(), User.localNumber,localPath,type);
//                    }
//                });
    }

    public class ImageBinder extends Binder{
        public void setSync(boolean sync) {
            this.sync = sync;
            Log.d("debug-sync",sync+"");
        }
        public void stopForegroudNotification(){
            stopForeground(true);
        }
        public void cancelNotification(int id){
            getNotificationManager().cancel(id);
        }
        private volatile boolean sync = true;
        public void synchronizeData(){
            startForeground(1,getNotification("正在同步",-1));
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (sync){
                        //更新notification
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Log.d("线程sync",sync+"");
                        int taskTotal = SyncTask.getInstance().getTaskTotal();
                        int taskedTotal = SyncTask.getInstance().getTaskedTotal();
                        float adv = (float)taskedTotal/(float)taskTotal;
                        int progress = (int)(adv *100);
                        getNotificationManager().notify(1,getNotification("正在同步",progress));
                        if (progress ==100 ){
                            SyncTask.getInstance().Downloading = false;
                            stopForeground(true);
                            cancelNotification(1);
                            getNotificationManager().notify(3,getNotificationBuilder("同步状态").setContentText("同步完成").setAutoCancel(true).build());
                            sync =false;
                            long timeMillis = System.currentTimeMillis();
                            Date date = new Date(timeMillis);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String time = sdf.format(date);
//                            MyApplication.getInstances().setDatabase();
//                            SynchroRecordDao synchroRecordDao = MyApplication.getInstances().getDaoSession().getSynchroRecordDao();
                            //插入数据库
                            ContentValues values = new ContentValues();
                            values.put("contactSize","联系人 : "+(SyncTask.getInstance().ContactDownloadCount+SyncTask.getInstance().ContactUploadCount));
                            values.put("smsSize","短信 : "+(SyncTask.getInstance().SmsDownloadCount+SyncTask.getInstance().SmsUploadCount));
                            values.put("pictureSize","图片 : "+(SyncTask.getInstance().PictureUploadCount+SyncTask.getInstance().PictureDownloadCount));
                            values.put("total","总数 : "+(SyncTask.getInstance().getTaskTotal()));
                            values.put("time","时间 : "+time);
                            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(SyncService.this,"history.db",null,1);
                            SQLiteDatabase db = helper.getWritableDatabase();
                            db.insert("SynchroRecord",null,values);
                            //设置最后一次同步的时间
                            SharedPreferences sp = getSharedPreferences("state_prf",MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("lasttime",time);
                            edit.commit();
//                            synchroRecordDao.insert(sr);
                            //播放完成的提示音
                           SharedPreferences sound = getSharedPreferences("cb_pref", MODE_PRIVATE);
                            boolean cb2 = sound.getBoolean("cb2", true);
                            if (cb2){
                                try {
                                MediaPlayer player = new MediaPlayer();
                                    AssetFileDescriptor afd = getAssets().openFd("CrystalRing.ogg");
                                    player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                                    player.prepare();
                                    player.start();
                                    Thread.sleep(2000);
                                    if (player != null&&!player.isPlaying()){
                                        player.release();
                                        Log.d("release","release");
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences cb_sp = getSharedPreferences("cb_pref", MODE_PRIVATE);
                    boolean cb_contact = cb_sp.getBoolean("cb3", true);
                    boolean cb_sms = cb_sp.getBoolean("cb4", true);
                    boolean cb_image = cb_sp.getBoolean("cb5", true);
                    //查询出总执行任务数量
                    //重置task数据
                        SyncTask.getInstance().reset();
                        if (cb_contact)
                        setContactTask();
                        if (cb_sms)
                        setSmsTask();
                        if (cb_image)
                        setPictureTask();
//                    int taskTotal = SyncTask.getInstance().getTaskTotal();
//                    int taskedTotal = SyncTask.getInstance().getTaskedTotal();
//                    if (taskedTotal>taskTotal){
//                        SyncTask.getInstance().reset();
//                    }
                        RxBus.getInstance().post(SyncTask.getInstance());
                    if (cb_contact)
                    syncContact();
                    if (cb_sms)
                    syncSms();
                    if (cb_image)
                    syncPicture();

                }
            }).start();
        }

        private void setPictureTask() {
            ArrayList<Image> imageLocal = new ImageUtil().getImagePath(getContentResolver());
            List<CloudImage> imageRemote = new CloudImageUtil(SyncService.this).getCloudImageData(User.localNumber);
            //需要上传的图片数量
            if (imageLocal != null){
                for (Image imageL : imageLocal){
                    setPictureUploadCount(imageRemote, imageL);
                }
            }
            if (imageRemote != null){
                //需要下载的图片数量
                for (CloudImage imageR: imageRemote){
                    setPictureDownloadCount(imageLocal, imageR);
                }
            }

        }

        private void setPictureDownloadCount(ArrayList<Image> imageLocal, CloudImage imageR) {
            for (Image imageL : imageLocal){
                if (imageL.getPath().equals(imageR.getPath())){
                    return ;
                }
            }
            if (new File(imageR.getPath()).exists()){
                return;
            }
            Log.d("需要下载","");
           // synchronized (lock){
                SyncTask.getInstance().PictureDownloadCount++;
           // }

        }

        private void setPictureUploadCount(List<CloudImage> imageRemote, Image imageL) {
            for (int i=0;i<imageRemote.size();i++){
                if (imageL.getPath().toLowerCase().equals(imageRemote.get(i).getPath().toLowerCase())){
                    return ;
                }
            }
             if (!new File(imageL.getPath()).exists()){
                return;
            }
            //synchronized (lock) {
            Log.d("需要上传","+1");
                SyncTask.getInstance().PictureUploadCount++;
           // }
        }

        private void setSmsTask() {
            ArrayList<Sms> smsLocal = new SmsUtil(SyncService.this).querrySmsToList(getContentResolver());
            List<Sms> smsRemote = new SmsUtil(SyncService.this).findRemoteDataAll();
            //需要上传的短信数量
            if (smsLocal !=null){
                for (Sms smsL : smsLocal){
                    setSmsUploadCount(smsRemote, smsL);
                }
            }

            //需要下载的短信数量
            if (smsRemote != null){
                for (Sms smsR : smsRemote){
                    setSmsDownloadCount(smsLocal, smsR);
                }
            }

        }

        private void setSmsDownloadCount(ArrayList<Sms> smsLocal, Sms smsR) {
            if (smsLocal != null){
                for (Sms smsL : smsLocal){
                    if (smsL.getDate()==smsR.getDate()&&smsL.getBody().equals(smsR.getBody())){
                        return ;
                    }
                }
            }
            SyncTask.getInstance().SmsDownloadCount++;
        }

        private void setSmsUploadCount(List<Sms> smsRemote, Sms smsL) {
            if (smsRemote!=null){
                for (Sms smsR : smsRemote){
                    if (smsL.getDate()==smsR.getDate()&&smsL.getBody().equals(smsR.getBody())){
                        return ;
                    }
                }
            }
            SyncTask.getInstance().SmsUploadCount++;
        }

        private void setContactTask() {
            //获取本地联系人的条目
            List<Contact> contactsData = new ContactsUtil().getContactsData(getContentResolver());
            //获取服务端联系人条目
            List<DataContact> remoteData = new CloudContactsUtil(SyncService.this).getRemoteData(User.localNumber);
            //计算出需要上传和下载的联系人总数
            //1.需要上传的总数
            for (int i =0;i<contactsData.size();i++){
                setContactUploadCount(contactsData.get(i),remoteData);
            }
            //2.需要下载的总数
            if (remoteData !=null){
                for (DataContact remoteContact : remoteData){
                    setContactDownloadCount(remoteContact,contactsData);
                }
            }

        }

        private void setContactDownloadCount(DataContact remoteContact,List<Contact> contactsData){
            for (Contact contact : contactsData){
                if (contact.getNumber().equals(remoteContact.getPhonenumber())){
                    return;
                }
            }
            SyncTask.getInstance().ContactDownloadCount++;
        }
        private void setContactUploadCount(Contact contact,List<DataContact> remoteData){
            for (DataContact remoteContact : remoteData){
                if (contact.getNumber().equals(remoteContact.getPhonenumber())){
                    return;
                }
            }
            SyncTask.getInstance().ContactUploadCount++;
        }
        private void syncSms() {
            SmsUtil smsUtil = new SmsUtil(SyncService.this);
            ArrayList<Sms> smsLocalList = smsUtil.querrySmsToList(getContentResolver());
            List<Sms> remoteDataAll = smsUtil.findRemoteDataAll();
            for (int i=0;i<smsLocalList.size();i++){
                uploadSms(smsLocalList.get(i),remoteDataAll,smsUtil);
            }
            for (int i=0;i<remoteDataAll.size();i++){
                downloadSms(remoteDataAll.get(i),smsLocalList,smsUtil);
            }
        }

        private void downloadSms(Sms smsRemote, ArrayList<Sms> smsLocalList, SmsUtil smsUtil) {
            if (smsLocalList!=null){
                for (Sms localSms: smsLocalList){
                    if (localSms.getDate()==smsRemote.getDate()&&smsRemote.getAddress().equals(localSms.getAddress())&&smsRemote.getType()==localSms.getType()&&smsRemote.getBody().equals(localSms.getBody()))
                    {
                        return;
                    }
                }
            }
            smsUtil.writeToSmsDatabase(getContentResolver(),smsLocalList,smsRemote.getAddress(),smsRemote.getType(),smsRemote.getDate(),smsRemote.getBody(),null);
        }

        private void uploadSms(Sms smsLocal ,List<Sms> remoteData,SmsUtil smsUtil) {
            if (remoteData!=null){
                for (Sms remoteSms : remoteData){
                    if (smsLocal.getDate()==remoteSms.getDate()&&smsLocal.getBody().equals(remoteSms.getBody())&&smsLocal.getAddress().equals(remoteSms.getAddress())&&smsLocal.getType()==remoteSms.getType()){
                        return;
                    }
                }
            }
            smsUtil.insertSmsToRemoteDB(User.localNumber,smsLocal.getAddress(),smsLocal.getDate(),smsLocal.getType(),smsLocal.getBody());
        }

        private void syncContact() {
            List<Contact> localData = new ContactsUtil().getContactsData(getContentResolver());
            CloudContactsUtil contactsUtil = new CloudContactsUtil(SyncService.this);
            List<DataContact> remoteData = contactsUtil.getRemoteData(User.localNumber);
            ContactsUtil util = new ContactsUtil();
            //执行上传
            for (int i =0 ;i<localData.size();i++){
                contactsUtil.uploadData(getContentResolver(),localData.get(i).getName(),localData.get(i).getNumber(),User.localNumber,remoteData);
            }
            //执行下载
            for (int i =0 ;i<remoteData.size();i++){
            util.writeContactToLocalDB(SyncService.this,remoteData.get(i).getName(),remoteData.get(i).getPhonenumber(),localData,null);
            }
        }

        private void syncPicture() {
            ArrayList<Image> localImages = new ImageUtil().getImagePath(getContentResolver());
            Log.d("本地图片的log",localImages.get(1).getType());
            List<CloudImage> cloudImageData = new CloudImageUtil(SyncService.this).getCloudImageData(User.localNumber);
                    //执行上传
            OSSFederationToken ossFederationToken = getFederationToken();
            long expiration = ossFederationToken.getExpiration();
            Date date = new Date(expiration);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String extime = sdf.format(date);
            Log.d("令牌期望时间",extime);
            OSSCredentialProvider credentialProvider = new
                        OSSStsTokenCredentialProvider(ossFederationToken.getTempAK(),
                        ossFederationToken.getTempSK(), ossFederationToken.getSecurityToken());
                ClientConfiguration conf = new ClientConfiguration();
                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
                conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
                OSS oss = new OSSClient(getApplicationContext(), "http://oss-cn-shenzhen.aliyuncs.com", credentialProvider,conf);
            PutObjectRequest put = new PutObjectRequest("","","");
            if (SyncTask.getInstance().PictureUploadCount>0){
                //执行上传
                for (int i = 0;i<localImages.size();i++) {
                    uploadPicture(oss,put,getApplicationContext(), localImages.get(i).getPath(), localImages.get(i).getType(),cloudImageData);
                }
            }
            if (SyncTask.getInstance().PictureDownloadCount>0){
                //执行下载
                for (int i = 0;i<cloudImageData.size();i++) {
                    writePictureToLocal(getApplicationContext(), cloudImageData.get(i).getUrl(), cloudImageData.get(i).getPath(), cloudImageData.get(i).getType(),localImages);
                }
            }
        }
        public  OSSFederationToken getFederationToken() {
            OSSFederationToken federationToken;
            String stsJson;
            OkHttpClient client = new OkHttpClient();
            String token_ip = getResources().getString(R.string.token_ip);
            Request request = new Request.Builder().url(token_ip).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    stsJson = response.body().string();
                    response.close();
                } else {
                    response.close();
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("GetSTSTokenFail", e.toString());
                return null;
            }
            try {
                JSONObject jsonObjs = new JSONObject(stsJson);
                String ak = jsonObjs.getString("AccessKeyId");
                String sk = jsonObjs.getString("AccessKeySecret");
                String token = jsonObjs.getString("SecurityToken");
                String expiration = jsonObjs.getString("Expiration");
                return federationToken = new OSSFederationToken(ak, sk, token, expiration);
            } catch (JSONException e) {
                federationToken = null;
                Log.e("GetSTSTokenFail", e.toString());
                e.printStackTrace();
                return null;
            }
        }
        public void writePictureToLocal(Context context ,String url, String path,String type,ArrayList<Image> localImages){
            try {
                if (localImages != null){
                    for (Image image : localImages){
                        if (image.getPath().toLowerCase().equals(path.toLowerCase())){
                            return;
                        }
                    }
                }
                if (new File(path).exists()){
                    Log.d("SyncService.writePictureToLocal","file is exists");
//                MediaScanner scanner = new MediaScanner(SyncService.this);
//                scanner.scanFiles(new String[]{path},new String[]{type},scanner);
                    return;
                }
            Log.d("writePicture",path);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).get().build();
                Response response = client.newCall(request).execute();
                InputStream is = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                //String spath =Environment.getExternalStorageDirectory().getPath();
                File file = new File(StringUtil.getFolderAbsolutePath(path));
                if(!file.exists()){
                    Log.d("创建文件夹",file.mkdirs()+"");
                }
                FileOutputStream fos = new FileOutputStream(path);
                if (type.contains("png")){
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
                }else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                }

                fos.flush();
                fos.close();
                response.close();
                SyncService.downloaded ++;
                SyncTask.getInstance().PictureDownloadedCount++;
                RxBus.getInstance().post(SyncTask.getInstance());
            //通知相册单独扫描这个文件4.4之后不能用
               // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));

                String[] filePaths = {path};
                String[] typeMines = {type};
                MediaScanner scanner = new MediaScanner(context);
                scanner.scanFiles(filePaths, typeMines, new MediaScannerConnection.OnScanCompletedListener() {
                    int scanneredSize = 0;
                    @Override
                    public void onScanCompleted(String s, Uri uri) {

                        Log.d("外部onScanCompleted","调用到");
                        Uri uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        Cursor cursor = getContentResolver().query(uri2, null, null, null, null);
                        int count = cursor.getCount();
                        Log.d("保存图片后的count",count+"");
                        if (cursor!=null){
                            cursor.close();
                        }

                        scanneredSize++;
                        if (scanneredSize == filePaths.length){
                            scanner.disConnection();
                        }
                    }
                });

//                Uri uri2 = ContentUris.withAppendedId(uri,count);
//                getContentResolver().update(uri2)


                //插入到图库,同时会新增图片
    //            MediaStore.Images.Media.insertImage(context.getContentResolver(),path,StringUtil.getFileName(path),null);


            } catch (IOException e) {
                e.printStackTrace();
            }
//            MyApplication.getInstances().setDatabase();
//            RestorePictureDao restorePictureDao = MyApplication.getInstances().getDaoSession().getRestorePictureDao();
            //插入数据库
            ContentValues values = new ContentValues();
            values.put("filename",StringUtil.getFileName(path));
            values.put("filepath",path);
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(SyncService.this,"history.db",null,1);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.insert("restorepicture",null,values);

//            restorePictureDao.insert(rp);
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(), fileName, null);
            //此种行为数据保存图片了，以上已经保存过了，因此注掉
//            try {
//                MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                        path, StringUtil.getFileName(path), null);
                //Log.d("DownloadFileName",StringUtil.getFileName(path));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
          }
        private NotificationManager getNotificationManager() {
            return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        private Notification getNotification(String title, int progress) {
            Intent intent = new Intent(SyncService.this, MainActivity.class);
            intent.putExtra("notification",true);
            PendingIntent pi = PendingIntent.getActivity(SyncService.this, 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(SyncService.this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setContentIntent(pi);
            builder.setContentTitle(title);
            builder.setAutoCancel(true);
            if (progress >= 0) {
                // 当progress大于或等于0时才需显示下载进度
                builder.setContentText(progress + "%");
                builder.setProgress(100, progress, false);
            }
            return builder.build();
        }
        private NotificationCompat.Builder getNotificationBuilder(String title) {
            Intent intent = new Intent(SyncService.this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(SyncService.this, 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(SyncService.this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setContentIntent(pi);
            builder.setContentTitle(title);
//            if (progress >= 0) {
//                // 当progress大于或等于0时才需显示下载进度
//                builder.setContentText(progress + "%");
//                builder.setProgress(100, progress, false);
//            }
            return builder;
        }
        public void download(){
//            new Thread()
        }
        public int getDownloadTotal() {
            return downloadTotal;
        }

        public int getUploadTotal() {
            return uploadTotal;
        }

        public int getUploaded() {
            return uploaded;
        }

        public int getDownloaded() {
            return downloaded;
        }
        public boolean isUploading(){
            return uploading;
        }
        public boolean isDownloading(){
            return downloading;
        }

        }

    }

