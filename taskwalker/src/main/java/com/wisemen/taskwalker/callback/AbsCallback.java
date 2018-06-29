package com.wisemen.taskwalker.callback;

import com.wisemen.taskwalker.convert.Converter;
import com.wisemen.taskwalker.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

public abstract class AbsCallback<T> implements Converter<T> {

    /**
     * 请求网络开始前，UI线程。
     * @param request
     * 请求开启netStatusRelated属性后，返回true表示继续发起请求，false表示停止请求
     */
    public boolean onBefore(BaseRequest request) {
         return false;
    }

    /** 对返回数据进行操作的回调， UI线程 */
    public abstract void onSuccess(T t, Call call, Response response);

    /** 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程 */
    public void onError(Call call, Response response, Exception e) {
    }

    /** 网络失败结束之前的回调 */
    public void parseError(Call call, Exception e) {
    }

    /** 请求网络结束后，UI线程 */
    public void onAfter(T t, Exception e) {
        if (e != null) e.printStackTrace();
    }

    /**
     * Post执行上传过程中的进度回调，get请求不回调，UI线程
     *
     * @param currentSize  当前上传的字节数
     * @param totalSize    总共需要上传的字节数
     * @param progress     当前上传的进度
     * @param networkSpeed 当前上传的速度 字节/秒
     */
    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    }

    /**
     * 执行下载过程中的进度回调，UI线程
     *
     * @param currentSize  当前下载的字节数
     * @param totalSize    总共需要下载的字节数
     * @param progress     当前下载的进度
     * @param networkSpeed 当前下载的速度 字节/秒
     */
    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    }
}