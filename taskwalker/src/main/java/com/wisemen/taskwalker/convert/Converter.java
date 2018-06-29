package com.wisemen.taskwalker.convert;

import okhttp3.Response;

/**
 * 网络数据的转换接口
 */
public interface Converter<T> {

    /**
     * 收到响应后，将数据转换成需要的格式，子线程中执行，可以是耗时操作
     */
    T convertSuccess(Response response) throws Exception;
}