package com.example.enlogty.communicationassistant.adapter;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.Contact;
import com.example.enlogty.communicationassistant.domain.DataContact;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by enlogty on 2017/11/22.
 */

public class CloudContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public void setTv_selected(TextView tv_selected) {
        this.tv_selected = tv_selected;
    }
    public void setDownload(ImageView download) {
        this.download = download;
    }
    public boolean bo[] ;
    private TextView  tv_selected;
    private ImageView download;
    public void setCloudContactList(List<DataContact> cloudContactList) {
        this.cloudContactList = cloudContactList;
    }
    private List<DataContact> cloudContactList;
    private List<DataContact> selectContactList = new ArrayList<>();
    private Context mContext;

    public CloudContactAdapter(Context mContext, List<DataContact> cloudContactList) {
        this.mContext = mContext;
        this.cloudContactList = cloudContactList;
        initMap();
    }

    private void initMap() {
        bo = new boolean[cloudContactList.size()];
    }
    public List<DataContact> getSelectContactList() {
        return selectContactList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(mContext).inflate(R.layout.rv_cloudcontact_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final   int position) {
        DataContact contact = cloudContactList.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tv_name.setText(contact.getName());
        viewHolder.tv_number.setText(contact.getPhonenumber());
        viewHolder.checkBox.setTag(position);
        viewHolder.checkBox.setChecked(bo[position]);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                bo[pos] = isChecked;
                int i,j=0;
                selectContactList.clear();
                for ( i=0;i<bo.length;i++){
                    if (bo[i]){
                        j++;
                        selectContactList.add(cloudContactList.get(i));
                    }
                }
                Log.d("selectContactList.SIZE",""+selectContactList.size());
                Log.d("cloudContactList.SIZE",""+cloudContactList.size());
                tv_selected.setText("已选("+j+")");
                if (selectContactList.size()>0){
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
        return cloudContactList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView tv_name;
        TextView tv_number;
        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_contact);
            tv_name = (TextView) itemView.findViewById(R.id.tv_cname);
            tv_number = (TextView) itemView.findViewById(R.id.tv_cnumber);
        }
    }
}
