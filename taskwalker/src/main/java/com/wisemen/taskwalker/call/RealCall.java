package com.wisemen.taskwalker.call;

import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.callback.AbsCallback;
import com.wisemen.taskwalker.callback.AbsCallbackWrapper;
import com.wisemen.taskwalker.model.Response;
import com.wisemen.taskwalker.request.BaseRequest;
import com.wisemen.taskwalker.utils.NetStatusTool;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Request;
import okhttp3.RequestBody;

public class RealCall<T> implements Call<T> {

    private volatile boolean canceled;
    private boolean executed;
    private BaseRequest baseRequest;
    private okhttp3.Call rawCall;
    private AbsCallback<T> mCallback;

    private int currentRetryCount;

    public RealCall(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
    }

    @Override
    public boolean execute(AbsCallback<T> callback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;
        }
        mCallback = callback;
        if (mCallback == null) mCallback = new AbsCallbackWrapper<>();

        if(!NetStatusTool.getInstance().isWifi() && !baseRequest.isIndependentOfNetStatus() &&
                (null == mCallback || !mCallback.onBefore(baseRequest))) {
            return false;
        }

        //构建请求
        RequestBody requestBody = baseRequest.generateRequestBody();
        final Request request = baseRequest.generateRequest(baseRequest.wrapRequestBody(requestBody));
        rawCall = baseRequest.generateCall(request);
        if (canceled) {
            rawCall.cancel();
        }
        currentRetryCount = 0;
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (e instanceof SocketTimeoutException && currentRetryCount < baseRequest.getRetryCount()) {
                    //超时重试
                    currentRetryCount++;
                    okhttp3.Call newCall = baseRequest.generateCall(call.request());
                    newCall.enqueue(this);
                } else {
                    mCallback.parseError(call, e);

                    if (!call.isCanceled()) {
                        sendFailResultCallback(call, null, e);
                    }
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                int responseCode = response.code();

                if (responseCode == 404 || responseCode >= 500) {
                    sendFailResultCallback(call, response, new Exception("服务器数据异常!"));
                    return;
                }

                try {
                    Response<T> parseResponse = parseResponse(response);
                    T data = parseResponse.body();
                    //请求成功回调
                    sendSuccessResultCallback(data, call, response);
                } catch (Exception e) {
                    sendFailResultCallback(call, response, e);
                }
            }
        });

        return true;
    }

    /** 失败回调，发送到主线程 */
    @SuppressWarnings("unchecked")
    private void sendFailResultCallback(final okhttp3.Call call, final okhttp3.Response response, final Exception e) {
        TaskWalker.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                mCallback.onError(call, response, e);        //请求失败回调
                mCallback.onAfter(null, e);              //请求结束回调
            }
        });
    }

    /** 成功回调，发送到主线程 */
    private void sendSuccessResultCallback(final T t, final okhttp3.Call call, final okhttp3.Response response) {
        TaskWalker.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(t, call, response);      //请求成功回调
                mCallback.onAfter(t, null);                  //请求结束回调
            }
        });
    }

    @Override
    public Response<T> execute() throws Exception {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;
        }
        okhttp3.Call call = baseRequest.getCall();
        if (canceled) {
            call.cancel();
        }
        return parseResponse(call.execute());
    }

    private Response<T> parseResponse(okhttp3.Response rawResponse) throws Exception {
        T body = (T) baseRequest.getConverter().convertSuccess(rawResponse);
        return Response.success(body, rawResponse);
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        canceled = true;
        if (rawCall != null) {
            rawCall.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public Call<T> clone() {
        return new RealCall<>(baseRequest);
    }

    @Override
    public BaseRequest getBaseRequest() {
        return baseRequest;
    }
}