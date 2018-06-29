package com.wisemen.demo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wisemen.taskrunner.download.DownloadManager;
import com.wisemen.taskrunner.download.DownloadService;
import com.wisemen.taskwalker.utils.NetStatusTool;

public class NetStatusBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isWifi = NetStatusTool.getInstance().findWifi(context);

        if(isWifi) {
            DownloadManager downloadManager = DownloadService.getDownloadManager();
            downloadManager.startAllTask();
        }
    }

}

