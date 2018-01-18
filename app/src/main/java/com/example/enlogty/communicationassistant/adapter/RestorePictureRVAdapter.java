package com.example.enlogty.communicationassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.sqlite.bean.RestorePicture;

import java.io.File;
import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public class RestorePictureRVAdapter extends RecyclerView.Adapter<RestorePictureRVAdapter.ViewHolder>{
    private List<RestorePicture> restorePictureList;
    private Context mContext;
    public RestorePictureRVAdapter(List<RestorePicture> restorePictureList) {
        this.restorePictureList = restorePictureList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_restorepicture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RestorePicture restorePicture = restorePictureList.get(position);
        holder.tv.setText(restorePicture.getFileName());
        Log.d("图片路径",restorePicture.getFilePath());
        Log.d("图片是否存在",new File(restorePicture.getFilePath()).exists()+" ");
        Glide.with(mContext).load(new File(restorePicture.getFilePath())).into(holder.iv);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File image = new File(restorePicture.getFilePath());
                if (image.exists()){
                    if (Build.VERSION.SDK_INT >= 24){
                        Intent openImage = new Intent(Intent.ACTION_VIEW);
                        //openImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        openImage.addCategory(Intent.CATEGORY_DEFAULT);
                        //赋予权限
                        openImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        openImage.setDataAndType(FileProvider.getUriForFile(mContext,"com.example.enlogty.communicationassistant.provider",image),"image/jpg");
                        mContext.startActivity(openImage);
                    }else {
                        Intent openImage = new Intent(Intent.ACTION_VIEW);
                        //openImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        openImage.addCategory(Intent.CATEGORY_DEFAULT);
                        openImage.setDataAndType(Uri.fromFile(image),"image/*");
                        mContext.startActivity(openImage);
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return restorePictureList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
         LinearLayout linearLayout;
         TextView tv;
         ImageView iv;
        public ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.riv);
            linearLayout = itemView.findViewById(R.id.ll_rp);
        }
    }
}
