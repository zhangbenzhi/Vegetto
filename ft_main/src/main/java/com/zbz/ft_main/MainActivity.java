package com.zbz.ft_main;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zbz.lib_commen.BaseActivity;
import com.zbz.lib_commen.config.RouterConfig;
import com.zbz.lib_commen.recyclerview.adapter.BaseSimpleAdapter;
import com.zbz.lib_commen.recyclerview.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

@Route(path = RouterConfig.MainModule.MainRouter)
public class MainActivity extends BaseActivity {

    private BaseSimpleAdapter<String> baseSimpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();
    }

    private void setData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("哈哈哈哈哈你大爷的" + i);
        }
        baseSimpleAdapter.addDataList(list);
    }

    private void initView() {
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        baseSimpleAdapter = new BaseSimpleAdapter<String>(this, R.layout.item_main_rv) {
            @Override
            public void onBindViewHolder(BaseViewHolder baseViewHolder, String item, int position) {
                TextView textView = baseViewHolder.getView(R.id.tv_name, TextView.class);
                textView.setText(item);
            }
        };
        rv.setAdapter(baseSimpleAdapter);
    }

}
