package com.wisemen.taskrunner.upload;

import android.os.Message;
import android.util.Log;

import com.wisemen.taskrunner.listener.UploadListener;
import com.wisemen.taskrunner.task.PriorityAsyncTask;
import com.wisemen.taskwalker.callback.AbsCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 上传任务
 */
public class UploadTask<T> extends PriorityAsyncTask<Void, UploadInfo, UploadInfo> {

    private UploadUIHandler mUploadUIHandler;
    private UploadInfo mUploadInfo;              //当前任务的信息

    public UploadTask(UploadInfo downloadInfo, UploadListener<T> uploadListener) {
        mUploadInfo = downloadInfo;
        mUploadInfo.setListener(uploadListener);
        mUploadUIHandler = UploadManager.getInstance().getHandler();
        //将当前任务在定义的线程池中执行
        executeOnExecutor(UploadManager.getInstance().getThreadPool().getExecutor());
    }

    /** 每个任务进队列的时候，都会执行该方法 */
    @Override
    protected void onPreExecute() {
        UploadListener listener = mUploadInfo.getListener();
        if (listener != null) listener.onAdd(mUploadInfo);

        mUploadInfo.setNetworkSpeed(0);
        mUploadInfo.setState(UploadManager.WAITING);
        postMessage(null, null, null);
    }

    @Override
    protected UploadInfo doInBackground(Void... params) {
        if (isCancelled()) return mUploadInfo;
        mUploadInfo.setNetworkSpeed(0);
        mUploadInfo.setState(UploadManager.UPLOADING);
        postMessage(null, null, null);

        //构建请求体，默认使用post请求上传
        Response response;
        try {
            Log.i("UploadTask", String.valueOf(Thread.currentThread().getId()));
            response = mUploadInfo.getRequest().setCallback(new MergeListener()).execute();
        } catch (IOException e) {
            e.printStackTrace();
            mUploadInfo.setNetworkSpeed(0);
            mUploadInfo.setState(UploadManager.ERROR);
            postMessage(null, "网络异常", e);
            return mUploadInfo;
        }

        if (response.isSuccessful()) {
            try {
                T t = (T) mUploadInfo.getListener().parseNetworkResponse(response);
                mUploadInfo.setNetworkSpeed(0);
                mUploadInfo.setState(UploadManager.FINISH); //上传成功
                postMessage(t, null, null);
                return mUploadInfo;
            } catch (Exception e) {
                e.printStackTrace();
                mUploadInfo.setNetworkSpeed(0);
                mUploadInfo.setState(UploadManager.ERROR);
                postMessage(null, "解析数据对象出错", e);
                return mUploadInfo;
            }
        } else {
            mUploadInfo.setNetworkSpeed(0);
            mUploadInfo.setState(UploadManager.ERROR);
            postMessage(null, "数据返回失败", null);
            return mUploadInfo;
        }
    }

    private class MergeListener extends AbsCallback<T> {

        private long lastRefreshUiTime;

        public MergeListener() {
            lastRefreshUiTime = System.currentTimeMillis();
        }

        @Override
        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            long curTime = System.currentTimeMillis();
            //每200毫秒刷新一次数据
            if (curTime - lastRefreshUiTime >= 200 || progress == 1.0f) {
                mUploadInfo.setState(UploadManager.UPLOADING);
                mUploadInfo.setUploadLength(currentSize);
                mUploadInfo.setTotalLength(totalSize);
                mUploadInfo.setProgress(progress);
                mUploadInfo.setNetworkSpeed(networkSpeed);
                postMessage(null, null, null);
                lastRefreshUiTime = System.currentTimeMillis();
            }
        }

        @Override
        public T convertSuccess(Response response) throws Exception {
            return null;
        }

        @Override
        public void onSuccess(T t, Call call, Response response) {
        }
    }

    private void postMessage(T data, String errorMsg, Exception e) {
        UploadUIHandler.MessageBean<T> messageBean = new UploadUIHandler.MessageBean<>();
        messageBean.uploadInfo = mUploadInfo;
        messageBean.errorMsg = errorMsg;
        messageBean.e = e;
        messageBean.data = data;
        Message msg = mUploadUIHandler.obtainMessage();
        msg.obj = messageBean;
        mUploadUIHandler.sendMessage(msg);
    }

    public void stop() {
        super.cancel(false);
    }
}