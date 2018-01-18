package com.example.enlogty.communicationassistant.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.Image;
import com.example.enlogty.communicationassistant.domain.CloudImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CloudImageAdapter extends RecyclerView.Adapter<CloudImageAdapter.ViewHolder>{

    private onItemClickListener listener;
    private Context mContext;
    private TextView  tv_selected;
    private List<CloudImage> selectImageList;
    public boolean bo[] ;

    public void setImageList(List<CloudImage> imageList) {
        this.imageList = imageList;
    }

    public List<CloudImage> getImageList() {
        return imageList;
    }

    private List<CloudImage> imageList;
    private ImageView download;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        CheckBox cb;
        public ViewHolder(View view) {
            super(view);
            iv = view.findViewById(R.id.iv);
            cb = view.findViewById(R.id.cb);
        }
    }
    public void setTv_selected(TextView tv_selected) {
        this.tv_selected = tv_selected;
    }

    public void setDownload(ImageView download) {
        this.download = download;
    }

    public CloudImageAdapter(List<CloudImage> imageList) {
        this.imageList = imageList;
        initData();
    }

    public List<CloudImage> getSelectImageList() {
        return selectImageList;
    }

    private void initData() {
        bo = new boolean[imageList.size()];
        selectImageList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_cloudimage, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CloudImage img = imageList.get(position);
        Glide.with(mContext).load(img.getUrl()).into(holder.iv);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
        holder.cb.setTag(position);
        holder.cb.setChecked(bo[position]);
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int pos = (int) compoundButton.getTag();
                bo[pos] = b;
                int i,j=0;
                selectImageList.clear();
                for ( i=0;i<bo.length;i++){
                    if (bo[i]){
                        j++;
                        selectImageList.add(imageList.get(i));
                    }
                }
                tv_selected.setText("已选("+j+")");
                if (selectImageList.size()>0){
                    download.setBackgroundResource(R.mipmap.bk_on);
                    download.setClickable(true);
                }else {
                    download.setBackgroundResource(R.mipmap.bk_off);
                    download.setClickable(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
    public interface onItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }
}
