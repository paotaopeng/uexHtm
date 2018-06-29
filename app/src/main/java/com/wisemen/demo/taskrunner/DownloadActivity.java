package com.wisemen.demo.taskrunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseActivity;
import com.wisemen.demo.base.BaseRecyclerAdapter;
import com.wisemen.demo.base.DividerItemDecoration;
import com.wisemen.demo.model.ResourceInfo;
import com.wisemen.taskrunner.download.DownloadManager;
import com.wisemen.taskrunner.download.DownloadService;
import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.request.GetRequest;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DownloadActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.targetFolder) TextView targetFolder;
    @Bind(R.id.tvCorePoolSize) TextView tvCorePoolSize;
    @Bind(R.id.sbCorePoolSize) SeekBar sbCorePoolSize;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    private ArrayList<ResourceInfo> resourceInfos;
    private DownloadManager downloadManager;
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initToolBar(toolbar, true, "下载管理");

        initData();
        downloadManager = DownloadService.getDownloadManager();
//        downloadManager.setTargetFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaa/");

        targetFolder.setText("下载路径: " + downloadManager.getTargetFolder());
        sbCorePoolSize.setMax(5);
        sbCorePoolSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                downloadManager.getThreadPool().setCorePoolSize(progress);
                tvCorePoolSize.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sbCorePoolSize.setProgress(3);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new MainAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private class MainAdapter extends BaseRecyclerAdapter<ResourceInfo, ViewHolder> {

        public MainAdapter(Context context) {
            super(context, resourceInfos);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_download_details, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ResourceInfo resourceInfo = mDatas.get(position);
            holder.bind(resourceInfo);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.name) TextView name;
        @Bind(R.id.download) Button download;

        private ResourceInfo resourceInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ResourceInfo resourceInfo) {
            this.resourceInfo = resourceInfo;
            if (downloadManager.getDownloadInfo(resourceInfo.getUrl()) != null) {
                download.setText("已在队列");
                download.setEnabled(false);
            } else {
                download.setText("下载");
                download.setEnabled(true);
            }
            name.setText(resourceInfo.getName());
            download.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.download) {
                if (downloadManager.getDownloadInfo(resourceInfo.getUrl()) != null) {
                    Toast.makeText(getApplicationContext(), "任务已经在下载列表中", Toast.LENGTH_SHORT).show();
                } else {
                    GetRequest request = TaskWalker.get(resourceInfo.getUrl())
                            .headers("headerKey1", "headerValue1")
                            .headers("headerKey2", "headerValue2")
                            .params("paramKey1", "paramValue1")
                            .params("paramKey2", "paramValue2");
                    downloadManager.addTask(resourceInfo.getUrl(), resourceInfo, request, null);
                    download.setText("已在队列");
                    download.setEnabled(false);
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), DesActivity.class);
                intent.putExtra("resource", resourceInfo);
                startActivity(intent);
            }
        }
    }

    private void initData() {
        resourceInfos = new ArrayList<>();
        ResourceInfo resourceInfo1 = new ResourceInfo();
        resourceInfo1.setName("PEP三年级上册");
        resourceInfo1.setUrl("http://admin.huihuabao.com/appdata/textbook/S1_B1.zip");
        resourceInfos.add(resourceInfo1);
        ResourceInfo resourceInfo2 = new ResourceInfo();
        resourceInfo2.setName("JOIN IN 三年级上册");
        resourceInfo2.setUrl("http://admin.huihuabao.com/appdata/textbook/S2_B1.zip");
        resourceInfos.add(resourceInfo2);
        ResourceInfo resourceInfo3 = new ResourceInfo();
        resourceInfo3.setName("教学视频");
        resourceInfo3.setUrl("http://admin.huihuabao.com/video/teacher-720.mp4");
        resourceInfos.add(resourceInfo3);
    }
}