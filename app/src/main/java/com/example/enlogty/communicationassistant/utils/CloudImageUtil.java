package com.example.enlogty.communicationassistant.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.Image;
import com.example.enlogty.communicationassistant.bean.User;
import com.example.enlogty.communicationassistant.domain.CloudImage;
import com.example.enlogty.communicationassistant.service.SyncService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by enlogty on 2017/12/1.
 */

public class CloudImageUtil {
    public CloudImageUtil(Context context) {
        this.context = context;
    }

    private Context context;
    public interface DataListener{
         void onComplete(List<CloudImage> data);
    }
    public  void getDatas(DataListener listener) {
        if (uploadedData==null){
            uploadedData = getCloudImageData(User.localNumber);
                    listener.onComplete(uploadedData);
//Log.d("数据为空","获取最新数据");
        }else {
            listener.onComplete(uploadedData);
        }
    }
    public static List<CloudImage> uploadedData = new ArrayList<>();
    private  List<CloudImage> datas = new ArrayList<>();
    private int successCount = 0;
    public  List<CloudImage> getCloudImageData(String localNumber){
        datas.clear();
        try {
            OkHttpClient client = new OkHttpClient();
            String server_ip = context.getResources().getString(R.string.server_ip);
            Request request = new Request.Builder().url(server_ip+"cloud/image/findall").get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                String json =response.body().string();
                response.close();
                Gson gson = new Gson();
                //Log.d("json:",json);
                List<CloudImage> data = gson.fromJson(json,new TypeToken<List<CloudImage>>(){}.getType());
                //Log.d("多个手机的数据",""+data.size());
                for (CloudImage mdata : data){
                    if (mdata.getMynumber().equals(localNumber)){
                        datas.add(mdata);
                    }
                }
                //Log.d("我的手机的数据",""+datas.size());
                return datas;
            }else {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface onCompleteListener{
        void onComplete(List<CloudImage> data);
    }
    public void writePictureToLocal(Context context ,String url, String path,String type ,ArrayList<Image> images, downloadPictureListener listener){
        try {
            for (Image image : images){
                if (image.getPath().toLowerCase().equals(path.toLowerCase())){
                    successCount++;
                    listener.onComplite(successCount);
                    return;
                }
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            InputStream is = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            //String spath =Environment.getExternalStorageDirectory().getPath();
            File file = new File(StringUtil.getFolderAbsolutePath(path));
            if(!file.exists()){
                file.mkdirs();
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
            successCount++;
            SyncService.downloaded ++;
            listener.onComplite(successCount);
            String[] filePaths = {path};
            String[] typeMines = {type};
            MediaScanner scanner = new MediaScanner(context);
            scanner.scanFiles(filePaths, typeMines, new MediaScannerConnection.OnScanCompletedListener() {
                int scanneredSize = 0;
                @Override
                public void onScanCompleted(String s, Uri uri) {
                    Log.d("外部onScanCompleted","调用到");
                    scanneredSize++;
                    if (scanneredSize == filePaths.length){
                        scanner.disConnection();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   public interface downloadPictureListener{
        void onComplite(int count);
    }
}
