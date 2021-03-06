package com.wisemen.demo;

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

import com.wisemen.demo.base.BaseActivity;
import com.wisemen.demo.base.BaseRecyclerAdapter;
import com.wisemen.demo.base.DividerItemDecoration;
import com.wisemen.demo.taskwalker.FileDownloadActivity;
import com.wisemen.demo.taskwalker.FormUploadActivity;
import com.wisemen.demo.taskwalker.JsonRequestActivity;
import com.wisemen.demo.taskwalker.OkHttpActivity;
import com.wisemen.demo.taskrunner.DownloadActivity;
import com.wisemen.demo.taskrunner.UploadActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    private ArrayList<OkHttpModel> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar(toolbar, false, R.string.app_name);

        initData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MainAdapter(this));
    }

    @OnClick(R.id.fab)
    public void fab(View view) {
        WebActivity.runActivity(this, "慧话宝", "http://www.huihuabao.com");
    }

    private void initData() {
        items = new ArrayList<>();

        OkHttpModel model1 = new OkHttpModel();
        model1.title = "";
        model1.des = "通用功能演示";
        model1.type = 1;
        items.add(model1);

        OkHttpModel model2 = new OkHttpModel();
        model2.title = "基本功能";
        model2.des = "支持发送get、post请求、https请求、同步请求（会阻塞主线程）等";
        model2.type = 0;
        items.add(model2);

        OkHttpModel model3 = new OkHttpModel();
        model3.title = "自动解析Json对象";
        model3.des = "自动解析JavaBean对象、List<JavaBean>集合对象等";
        model3.type = 0;
        items.add(model3);

        OkHttpModel model4 = new OkHttpModel();
        model4.title = "文件下载";
        model4.des = "支持大文件下载，不会发生OOM。监听下载进度和下载网速。支持自定义下载目录和下载文件名";
        model4.type = 0;
        items.add(model4);

        OkHttpModel model5 = new OkHttpModel();
        model5.title = "文件上传";
        model5.des = "支持同时上传多个文件以及多个文件多个参数同时上传。大文件上传不会发生OOM。监听上传进度和上传网速";
        model5.type = 0;
        items.add(model5);

        OkHttpModel model6 = new OkHttpModel();
        model6.title = "";
        model6.des = "批量上传下载管理演示";
        model6.type = 1;
        items.add(model6);

        OkHttpModel model7 = new OkHttpModel();
        model7.title = "下载管理";
        model7.des = "支持多任务下载、断点下载和下载状态的管理";
        model7.type = 0;
        items.add(model7);

        OkHttpModel model8 = new OkHttpModel();
        model8.title = "上传管理";
        model8.des = "支持多任务上传";
        model8.type = 0;
        items.add(model8);
    }

    @Override
    protected boolean translucentStatusBar() {
        return true;
    }

    private class MainAdapter extends BaseRecyclerAdapter<OkHttpModel, ViewHolder> {

        public MainAdapter(Context context) {
            super(context, items);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = inflater.inflate(R.layout.item_main_list, parent, false);
            } else {
                view = inflater.inflate(R.layout.item_main_type, parent, false);
            }
            return new ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position, items.get(position));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView des;
        TextView divider;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            des = (TextView) itemView.findViewById(R.id.des);
            divider = (TextView) itemView.findViewById(R.id.divider);
        }

        public void bind(int position, OkHttpModel model) {
            this.position = position;
            if (model.type == 0) {
                title.setText(model.title);
                des.setText(model.des);
            } else {
                divider.setText(model.des);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (position == 1) startActivity(new Intent(MainActivity.this, OkHttpActivity.class));
            if (position == 2) startActivity(new Intent(MainActivity.this, JsonRequestActivity.class));
            if (position == 3) startActivity(new Intent(MainActivity.this, FileDownloadActivity.class));
            if (position == 4) startActivity(new Intent(MainActivity.this, FormUploadActivity.class));
            if (position == 6) startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            if (position == 7) startActivity(new Intent(MainActivity.this, UploadActivity.class));
        }
    }

    private class OkHttpModel {
        public String title;
        public String des;
        public int type;
    }
}