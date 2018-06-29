package com.wisemen.taskwalker.request;

import com.wisemen.taskwalker.model.HttpHeaders;
import com.wisemen.taskwalker.utils.HttpUtils;
import com.wisemen.taskwalker.utils.Logger;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Post请求的实现类
 */
public class PostRequest extends BaseBodyRequest<PostRequest> {

    public PostRequest(String url) {
        super(url);
        method = "POST";
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        try {
            headers.put(HttpHeaders.HEAD_KEY_CONTENT_LENGTH, String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            Logger.e(e);
        }
        Request.Builder requestBuilder = HttpUtils.appendHeaders(headers);
        return requestBuilder.post(requestBody).url(url).tag(tag).build();
    }
}