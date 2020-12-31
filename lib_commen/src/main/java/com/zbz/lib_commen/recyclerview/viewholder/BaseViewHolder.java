package com.zbz.lib_commen.recyclerview.viewholder;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 张本志
 * @date 2020/12/31 17:43
 * @description 封装BaseViewHolder
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> sparseArray = new SparseArray<>();

    public <V> V getView(int id, Class<V> clazz) {
        View view = sparseArray.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            sparseArray.put(id, view);
        }
        return clazz.cast(view);
    }

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
