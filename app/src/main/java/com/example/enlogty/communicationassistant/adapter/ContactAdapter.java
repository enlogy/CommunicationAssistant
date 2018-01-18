package com.example.enlogty.communicationassistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.enlogty.communicationassistant.R;
import com.example.enlogty.communicationassistant.bean.Contact;

import java.util.List;

/**
 * Created by enlogty on 2017/9/5.
 */

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private onItemClickListener listener;
    public ContactAdapter(List<Contact> contactList, Context mContext) {
        this.contactList = contactList;
        this.mContext = mContext;
    }
    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }
    private List<Contact> contactList;
    private Context mContext;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_contact_item,parent,false);
        return new ViewHolder(view);
    }
    public interface onItemClickListener{
        void onItemClick(int position);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Contact contact = contactList.get(position);
        ViewHolder mholder = (ViewHolder) holder;
        mholder.itemTxt.setText(contact.getName());
        mholder.itemll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemTxt;
        LinearLayout itemll;
        public ViewHolder(View itemView) {
            super(itemView);
            itemTxt  = (TextView) itemView.findViewById(R.id.contact_item_txt);
            itemll = (LinearLayout) itemView.findViewById(R.id.contact_item_ll);
        }
    }
}
