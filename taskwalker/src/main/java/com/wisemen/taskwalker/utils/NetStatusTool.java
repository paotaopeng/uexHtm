package com.wisemen.taskwalker.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络类型检测工具
 */
public class NetStatusTool {
    private static NetStatusTool mInstance;
    private boolean isWifi;

    public static NetStatusTool getInstance() {
        if (null == mInstance) {
            synchronized (NetStatusTool.class) {
                if (null == mInstance) {
                    mInstance = new NetStatusTool();
                }
            }
        }
        return mInstance;
    }

    /**
     * 直接读取当前打开的网络类型是否WIFI
     * @return 是不是Wifi上网
     */
    public boolean isWifi() {
        return isWifi;
    }

    /**
     * 检测当前打开的网络类型是否WIFI
     *
     * @param context 上下文
     * @return 是不是Wifi上网
     */
    public boolean findWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            isWifi = true;
        } else {
            isWifi = false;
        }

        return isWifi;
    }

}
