package com.wisemen.taskwalker.callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 所有回调的包装类
 */
public class AbsCallbackWrapper<T> extends AbsCallback<T> {
    @Override
    public T convertSuccess(Response response) throws Exception {
        response.close();
        return (T) response;
    }

    @Override
    public void onSuccess(T t, Call call, Response response) {
    }
}