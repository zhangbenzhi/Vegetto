package com.zbz.lib_commen.recyclerview.adapter;

import android.content.Context;

import com.zbz.lib_commen.recyclerview.viewholder.BaseViewHolder;

/**
 * @author 张本志
 * @date 2020/12/31 18:07
 * @description 只有一种itemType的baseAdapter:
 */
public abstract class BaseSimpleAdapter<T> extends BaseMultiTypeAdapter<T> {

    private int mLayoutId;

    public BaseSimpleAdapter(Context mContext, int layoutId) {
        super(mContext);
        this.mLayoutId = layoutId;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getLayoutByItemType(int itemType) {
        return mLayoutId;
    }

    @Override
    public abstract void onBindViewHolder(BaseViewHolder baseViewHolder, T t, int position);
}
