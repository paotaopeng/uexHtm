package com.wisemen.taskwalker.callback;

import com.wisemen.taskwalker.convert.StringConvert;

import okhttp3.Response;

/**
 * 返回文本数据
 */
public abstract class StringCallback extends AbsCallback<String> {

    @Override
    public String convertSuccess(Response response) throws Exception {
        String s = StringConvert.create().convertSuccess(response);
        response.close();
        return s;
    }
}