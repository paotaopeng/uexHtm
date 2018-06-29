package com.wisemen.demo;

import android.app.Application;

import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.model.HttpHeaders;
import com.wisemen.taskwalker.model.HttpParams;
import com.wisemen.taskwalker.utils.NetStatusTool;

public class GApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");
        params.put("commonParamsKey2", "中文参数");

        TaskWalker.init(this);
        NetStatusTool.getInstance().findWifi(GApp.this);
    }

}