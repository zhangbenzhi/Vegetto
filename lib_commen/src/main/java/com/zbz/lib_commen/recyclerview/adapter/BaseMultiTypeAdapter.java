package com.zbz.lib_commen.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zbz.lib_commen.recyclerview.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张本志
 * @date 2020/12/31 16:41
 * @description 有多种itemType的BaseAdapter:
 * 无需自己创建ViewHolder 所以也不需要在ViewHolder中findViewById查找View
 */
public abstract class BaseMultiTypeAdapter<T> extends RecyclerView.Adapter {

    private List<T> mData = new ArrayList<>();
    private Context mContext;

    public BaseMultiTypeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addData(T t) {
        mData.add(t);
        notifyItemInserted(getItemCount());
    }

    public void addDataList(List<T> list) {
        int preSize = getItemCount();
        if (list != null && list.size() > 0) {
            mData.addAll(list);
        }
        notifyItemRangeInserted(preSize, getItemCount());
    }

    public void setData(List<T> list) {
        this.mData = list == null ? new ArrayList<T>() : list;
        notifyDataSetChanged();
    }

    public void removeData(T t) {
        int index = mData.indexOf(t);
        if (index != -1) {
            mData.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void removeDataList(List<T> list) {
        if (list == null) {
            return;
        }
        int preCount = getItemCount();
        for (T t : list) {
            mData.remove(t);
        }
        notifyItemMoved(0, preCount);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(getLayoutByItemType(viewType), parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((BaseViewHolder) holder, mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public abstract int getItemViewType(int position);

    public abstract int getLayoutByItemType(int itemType);

    public abstract void onBindViewHolder(BaseViewHolder baseViewHolder, T t, int position);
}
