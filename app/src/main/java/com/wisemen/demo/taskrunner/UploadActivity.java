package com.wisemen.demo.taskrunner;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseActivity;
import com.wisemen.demo.utils.Urls;
import com.wisemen.taskrunner.download.db.DownloadInfoHelper;
import com.wisemen.taskrunner.listener.LocalListener;
import com.wisemen.taskrunner.listener.UploadListener;
import com.wisemen.taskrunner.local.LocalInfo;
import com.wisemen.taskrunner.local.LocalManager;
import com.wisemen.taskrunner.local.call.ParamCallable;
import com.wisemen.taskrunner.local.db.DataBaseExecutor;
import com.wisemen.taskrunner.task.ExecutorWithListener;
import com.wisemen.taskrunner.upload.UploadInfo;
import com.wisemen.taskrunner.upload.UploadManager;
import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.request.PostRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Response;

public class UploadActivity extends BaseActivity implements ExecutorWithListener.OnAllTaskEndListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tvCorePoolSize) TextView tvCorePoolSize;
    @Bind(R.id.sbCorePoolSize) SeekBar sbCorePoolSize;
    private UploadManager uploadManager;
    private LocalManager localManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initToolBar(toolbar, true, "上传管理");

        uploadManager = UploadManager.getInstance();
        localManager = LocalManager.getInstance();
        sbCorePoolSize.setMax(3);
        sbCorePoolSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                uploadManager.getThreadPool().setCorePoolSize(progress);
                tvCorePoolSize.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sbCorePoolSize.setProgress(1);

        uploadManager.getThreadPool().getExecutor().addOnAllTaskEndListener(this);
        localManager.getThreadPool().getExecutor().addOnAllTaskEndListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uploadManager.getThreadPool().getExecutor().removeOnAllTaskEndListener(this);
        localManager.getThreadPool().getExecutor().removeOnAllTaskEndListener(this);
    }

    @Override
    public void onAllTaskEnd() {
        Toast.makeText(getApplicationContext(), "所有上传任务完成", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.upload)
    public void upload(View view) {
        //测试数据
        ArrayList<File> files = new ArrayList<>();
        File file1 = new File("/storage/emulated/0/Download/apache-jmeter-2.9.zip");
        File file2 = new File("/storage/emulated/0/Download/AxureRP.zip");
        files.add(file1);
        files.add(file2);

        for (int i = 0; i < files.size(); i++) {
            MyUploadListener listener = new MyUploadListener();
            File file = files.get(i);
            PostRequest postRequest = TaskWalker.post(Urls.URL_FORM_UPLOAD)
                    .headers("headerKey1", "headerValue1")
                    .headers("headerKey2", "headerValue2")
                    .params("paramKey1", "paramValue1")
                    .params("paramKey2", "paramValue2")
                    .params("file", file);
            uploadManager.addTask(file.getPath(), postRequest, listener);
        }

/*        MyUploadListener listener = new MyUploadListener();
        PostRequest postRequest = TaskWalker.post(Urls.URL_FORM_UPLOAD)
                .headers("headerKey1", "headerValue1")
                .headers("headerKey2", "headerValue2")
                .params("paramKey1", "paramValue1")
                .params("paramKey2", "paramValue2")
                .addFileParams("upload", files);
        uploadManager.addTask(files.get(0).getPath(), postRequest, listener);*/
    }

    @OnClick(R.id.local)
    public void local(View view) {

       // for (int i = 0; i < 10; i++) {
            String taskKey = "task_" + 0;
            MyLocalListener listener = new MyLocalListener();
            MyCallable callable = new MyCallable();
            Map<String, String> params = new HashMap<String, String>();
            params.put("taskKey", taskKey);
            callable.setParams(params);
            localManager.addTask(taskKey, listener, callable);
       // }
    }

    @OnClick(R.id.cancel)
    public void cancel(View view) {
        List<UploadInfo> infoList = uploadManager.getAllTask();
//        for(UploadInfo info : infoList) {
//            info.getTask().cancel(true);
//        }
        infoList.get(0).getTask().cancel(true);
    }

    private class MyCallable extends ParamCallable<String, String> {
        public String call() throws Exception {
             System.out.println(params.get("taskKey") + "... has been called");
             List<String> sqlList = new ArrayList<String>();
//             sqlList.add("insert into download_table(taskKey, url) values(\"111\", \"www.baidu.com\")");
//             sqlList.add("insert into download_table(taskKey, url) values(\"222\", \"www.baidu.com\")");
//             sqlList.add("insert into download_table(taskKey, url) values(\"333\", \"www.baidu.com\")");
             for(int i = 1; i < 1000; i++) {
                  String sql = new StringBuilder().append("insert into download_table(taskKey, url) values(")
                           .append("\"").append(i).append("\", \"www.baidu.com\")").toString();
                  sqlList.add(sql);
             }

             long startTime = System.currentTimeMillis();
             DownloadInfoHelper helper = new DownloadInfoHelper();
             DataBaseExecutor.execBatch(helper, sqlList);
             Log.i("insertTest", String.valueOf(System.currentTimeMillis()- startTime));
             startTime = System.currentTimeMillis();
             DataBaseExecutor.exec(helper, "delete from download_table");
             Log.i("deleteTest", String.valueOf(System.currentTimeMillis()- startTime));

             String sql = "insert into download_table(taskKey, url) values(?, \"www.baidu.com\")";
             List<String[]> bindArgsList = new ArrayList<String[]>();
             for(int i = 1; i < 1000; i++) {
                 String[] bindArgs = {String.valueOf(i)};
                 bindArgsList.add(bindArgs);
             }
             startTime = System.currentTimeMillis();
             DataBaseExecutor.execBatch(helper, sql, bindArgsList);
             Log.i("preparedInsertTest", String.valueOf(System.currentTimeMillis()- startTime));
             return "the results of " + params.get("taskKey");
        }
    }

    private class MyLocalListener extends LocalListener<String> {
        @Override
        public void onFinish(String s) {
            Log.e("MyLocalListener", "finish:" + s);
        }

        @Override
        public void onError(LocalInfo localInfo, String errorMsg, Exception e) {
            Log.e("MyLocalListener", "onError:" + errorMsg);
        }
    }

    private class MyUploadListener extends UploadListener<String> {

        @Override
        public void onProgress(UploadInfo uploadInfo) {
            Log.e("MyUploadListener", "onProgress:" + uploadInfo.getTaskKey() + " " + uploadInfo.getTotalLength() + " "
                    + uploadInfo.getUploadLength() + " " + uploadInfo.getProgress() +  " " + uploadInfo.getNetworkSpeed());
            //TODO
        }

        @Override
        public void onFinish(UploadInfo uploadInfo, String s) {
            Log.e("MyUploadListener", "finish: url: " + uploadInfo.getTaskKey() + "response: " + s);
        }

        @Override
        public void onError(UploadInfo uploadInfo, String errorMsg, Exception e) {
            Log.e("MyUploadListener", "onError:" + errorMsg);
        }

        @Override
        public String parseNetworkResponse(Response response) throws Exception {
            Log.e("MyUploadListener", "convertSuccess");
            return response.body().string();
        }
    }
}