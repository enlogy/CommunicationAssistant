package com.example.enlogty.communicationassistant.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.enlogty.communicationassistant.R;

/**
 * Created by enlogty on 2018/1/1.
 */

public class HeaderAndFooterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public HeaderAndFooterWrapper(RecyclerView.Adapter mRealAdapter) {
        this.mRealAdapter = mRealAdapter;
    }

    private final int BASE_ITEM_TYPE_HEADER = 100000;
    private final int BASE_ITEM_TYPE_FOOTER = 200000;
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();
    private RecyclerView.Adapter mRealAdapter;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null){
            return new HeaderViewHolder(mHeaderViews.get(viewType));
        }else if (mFooterViews.get(viewType) != null){
            Log.d("onCreateViewHolder","FooterViewHolder");
            return new FooterViewHolder(mFooterViews.get(viewType));
        }
        Log.d("onCreateViewHolder","mRealAdapter");
        return mRealAdapter.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderItemPos(position)){
            Log.d("Adapter","Header");
        } else if (isFooterItemPos(position)) {
            Log.d("Adapter","Footer");
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.contactSize.setText(mRealAdapter.getItemCount()+"");
        }else {
            Log.d("Adapter","Real "+mFooterViews.size());
            mRealAdapter.onBindViewHolder(holder,position-mHeaderViews.size());
        }

    }

    @Override
    public int getItemCount() {
        return mHeaderViews.size()+mRealAdapter.getItemCount()+mFooterViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderItemPos(position)){
            return mHeaderViews.keyAt(position);
        }else if (isFooterItemPos(position)){
            return mFooterViews.keyAt(position - mRealAdapter.getItemCount() - mHeaderViews.size());
        }
        return mRealAdapter.getItemViewType(position - mHeaderViews.size());
    }

    private boolean isFooterItemPos(int position) {
        return position >= mRealAdapter.getItemCount() + mHeaderViews.size();
    }

    private boolean isHeaderItemPos(int position) {
        return position < mHeaderViews.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        private TextView contactSize;
        public FooterViewHolder(View itemView) {
            super(itemView);
            contactSize = itemView.findViewById(R.id.contentSize);
        }
    }
    public void addHeaderView(View view){
        mHeaderViews.put(getHeaderTypeView(),view);
    }
    public void addFooterView(View view){
        mFooterViews.put(getFooterTypeView(),view);
    }

    private int getFooterTypeView() {
        return BASE_ITEM_TYPE_FOOTER + mFooterViews.size();
    }

    private int getHeaderTypeView() {
        return BASE_ITEM_TYPE_HEADER + mHeaderViews.size();
    }
}
