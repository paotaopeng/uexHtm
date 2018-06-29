package com.wisemen.taskrunner.listener;

import com.wisemen.taskrunner.upload.UploadInfo;
import com.wisemen.taskwalker.request.BaseRequest;

import okhttp3.Response;

/**
 * 全局的上传监听
 */
public abstract class UploadListener<T> {

    private Object userTag;

    /** 下载进行时回调 */
    public abstract void onProgress(UploadInfo uploadInfo);

    /** 下载完成时回调 */
    public abstract void onFinish(UploadInfo uploadInfo, T t);

    /** 下载出错时回调 */
    public abstract void onError(UploadInfo uploadInfo, String errorMsg, Exception e);

    /** 成功添加任务的回调 */
    public void onAdd(UploadInfo uploadInfo) {
    }

    /** 成功移除任务回调 */
    public void onRemove(UploadInfo uploadInfo) {
    }

    /**
     * 上传前的回调
     * @param request
     * 返回true表示在非wifi的环境中继续上传，false表示停止上传
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

    public abstract T parseNetworkResponse(Response response) throws Exception;

    public static final UploadListener DEFAULT_UPLOAD_LISTENER = new UploadListener() {
        @Override
        public void onProgress(UploadInfo uploadInfo) {
        }

        @Override
        public void onFinish(UploadInfo uploadInfo, Object response) {
        }

        @Override
        public void onError(UploadInfo uploadInfo, String errorMsg, Exception e) {
        }

        @Override
        public Response parseNetworkResponse(Response response) throws Exception {
            return response;
        }
    };
}
