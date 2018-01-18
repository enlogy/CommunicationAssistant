package com.example.enlogty.communicationassistant.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.sqlite.bean.SynchroRecord;

import java.util.List;

/**
 * Created by enlogty on 2017/12/23.
 */

public class SynchroRecordRVAdapter extends RecyclerView.Adapter<SynchroRecordRVAdapter.ViewHolder>{
    private List<SynchroRecord> synchroRecordList;

    public SynchroRecordRVAdapter(List<SynchroRecord> synchroRecordList) {
        this.synchroRecordList = synchroRecordList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_synchrorecord, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SynchroRecord synchroRecord = synchroRecordList.get(position);
        holder.tv1.setText(synchroRecord.getContactSize());
        holder.tv2.setText(synchroRecord.getSmsSize());
        holder.tv3.setText(synchroRecord.getPictureSize());
        holder.tv4.setText(synchroRecord.getTotal());
        holder.tv5.setText(synchroRecord.getTime());
    }

    @Override
    public int getItemCount() {
        return synchroRecordList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
         TextView tv1,tv2,tv3,tv4,tv5;
        public ViewHolder(View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.t1);
            tv2 = itemView.findViewById(R.id.t2);
            tv3 = itemView.findViewById(R.id.t3);
            tv4 = itemView.findViewById(R.id.t4);
            tv5 = itemView.findViewById(R.id.t5);
        }
    }
}
