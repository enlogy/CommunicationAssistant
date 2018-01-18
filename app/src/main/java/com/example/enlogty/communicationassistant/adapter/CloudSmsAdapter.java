package com.example.enlogty.communicationassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.domain.DataContact;
import com.example.enlogty.communicationassistant.domain.Sms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by enlogty on 2017/11/22.
 */

public class CloudSmsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public void setTv_selected(TextView tv_selected) {
        this.tv_selected = tv_selected;
    }
    public void setDownload(ImageView download) {
        this.download = download;
    }
    public boolean bo[] ;
    private TextView  tv_selected;
    private ImageView download;
    public void setCloudSmsList(List<Sms> cloudSmsList) {
        this.cloudSmsList = cloudSmsList;
    }
    private List<Sms> cloudSmsList;
    private List<Sms> selectSmsList = new ArrayList<>();
    private Context mContext;

    public CloudSmsAdapter(Context mContext, List<Sms> cloudSmsList) {
        this.mContext = mContext;
        this.cloudSmsList = cloudSmsList;
        initMap();
    }

    private void initMap() {
        bo = new boolean[cloudSmsList.size()];
    }
    public List<Sms> getSelectSmsList() {
        return selectSmsList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(mContext).inflate(R.layout.rv_sms,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final   int position) {
        Sms sms = cloudSmsList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tv_s1.setText(sms.getAddress());
        viewHolder.tv_s2.setText(sms.getBody());
        //格式化时间
        Date date = new Date(sms.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = sdf.format(date);
        viewHolder.tv_s3.setText(time);
        viewHolder.checkBox.setTag(position);
        viewHolder.checkBox.setChecked(bo[position]);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                bo[pos] = isChecked;
                int i,j=0;
                selectSmsList.clear();
                for ( i=0;i<bo.length;i++){
                    if (bo[i]){
                        j++;
                        selectSmsList.add(cloudSmsList.get(i));
                    }
                }
                Log.d("selectSmsList.SIZE",""+selectSmsList.size());
                Log.d("cloudSmsList.SIZE",""+cloudSmsList.size());
                tv_selected.setText("已选("+j+")");
                if (selectSmsList.size()>0){
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
        return cloudSmsList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView tv_s1;
        TextView tv_s2;
        TextView tv_s3;
        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_sms);
            tv_s1 = (TextView) itemView.findViewById(R.id.tv_s1);
            tv_s2 = (TextView) itemView.findViewById(R.id.tv_s2);
            tv_s3 = (TextView) itemView.findViewById(R.id.tv_s3);
        }
    }
}
