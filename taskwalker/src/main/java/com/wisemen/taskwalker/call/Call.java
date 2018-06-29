package com.wisemen.taskwalker.call;

import com.wisemen.taskwalker.callback.AbsCallback;
import com.wisemen.taskwalker.model.Response;
import com.wisemen.taskwalker.request.BaseRequest;

/**
 * 请求的包装类
 */
public interface Call<T> {
    /** 同步执行 */
    Response<T> execute() throws Exception;

    /** 异步回调执行 */
    boolean execute(AbsCallback<T> callback);

    /** 是否已经执行 */
    boolean isExecuted();

    /** 取消 */
    void cancel();

    /** 是否取消 */
    boolean isCanceled();

    Call<T> clone();

    BaseRequest getBaseRequest();
}