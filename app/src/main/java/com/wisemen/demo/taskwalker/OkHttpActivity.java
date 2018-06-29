package com.wisemen.demo.taskwalker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseActivity;
import com.wisemen.demo.base.BaseRecyclerAdapter;
import com.wisemen.demo.base.DividerItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OkHttpActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    private ArrayList<String[]> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        initToolBar(toolbar, true, "基础功能演示");

        initData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MainAdapter(this));
    }

    private void initData() {
        data = new ArrayList<>();
        data.add(new String[]{"请求方法演示", "支持GET、POST等"});
        data.add(new String[]{"请求图片", "请求服务器返回bitmap对象"});
        data.add(new String[]{"上传大文本Json数据", "可以向服务器上传任意类型的文本数据，包括 String，JSONObject，JSONArray，byte数组等"});
        data.add(new String[]{"https请求", "支持 cer,bks 证书，支持双向认证"});
        data.add(new String[]{"同步请求", "允许直接返回Response对象，会阻塞主线程，需要自行开启子线程"});
    }

    private class MainAdapter extends BaseRecyclerAdapter<String[], ViewHolder> {

        public MainAdapter(Context context) {
            super(context, data);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_main_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String[] strings = mDatas.get(position);
            holder.bind(position, strings);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.title) TextView title;
        @Bind(R.id.des) TextView des;

        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position, String[] strings) {
            this.position = position;
            title.setText(strings[0]);
            des.setText(strings[1]);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (position == 0) startActivity(new Intent(OkHttpActivity.this, MethodActivity.class));
            if (position == 1) startActivity(new Intent(OkHttpActivity.this, BitmapRequestActivity.class));
            if (position == 2) startActivity(new Intent(OkHttpActivity.this, PostTextActivity.class));
            if (position == 3) startActivity(new Intent(OkHttpActivity.this, HttpsActivity.class));
            if (position == 4) startActivity(new Intent(OkHttpActivity.this, SyncActivity.class));
        }
    }
}