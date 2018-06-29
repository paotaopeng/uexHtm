package com.wisemen.taskrunner.listener;

import com.wisemen.taskrunner.download.DownloadInfo;
import com.wisemen.taskwalker.request.BaseRequest;

/**
 * 全局的下载监听
 */
public abstract class DownloadListener {

    private Object userTag;

    /** 下载进行时回调 */
    public abstract void onProgress(DownloadInfo downloadInfo);

    /** 下载完成时回调 */
    public abstract void onFinish(DownloadInfo downloadInfo);

    /** 下载出错时回调 */
    public abstract void onError(DownloadInfo downloadInfo, String errorMsg, Exception e);

    /** 成功添加任务的回调 */
    public void onAdd(DownloadInfo downloadInfo) {
    }

    /** 成功移除任务回调 */
    public void onRemove(DownloadInfo downloadInfo) {
    }

    /**
     * 下载前的回调
     * @param request
     * 返回true表示在非wifi的环境中继续下载，false表示停止下载
     */
    public boolean onBefore(BaseRequest request) {
        return false;
    }

    public Object getUserTag() {
        return userTag;
    }

    public void setUserTag(Object userTag) {
        this.userTag = userTag;
    }

    public static final DownloadListener DEFAULT_DOWNLOAD_LISTENER = new DownloadListener() {
        @Override
        public void onProgress(DownloadInfo downloadInfo) {
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
        }
    };
}
