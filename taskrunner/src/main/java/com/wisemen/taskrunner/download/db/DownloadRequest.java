package com.wisemen.taskrunner.download.db;

import com.wisemen.taskwalker.model.HttpHeaders;
import com.wisemen.taskwalker.model.HttpParams;
import com.wisemen.taskwalker.request.BaseRequest;
import com.wisemen.taskwalker.request.GetRequest;
import com.wisemen.taskwalker.request.PostRequest;

import java.io.Serializable;

/**
 * 下载请求对象
 */
public class DownloadRequest implements Serializable {

    private static final long serialVersionUID = -6883956320373276785L;

    public String method;
    public String url;
    public HttpParams params;
    public HttpHeaders headers;
    public boolean netStatusRelated;

    public static String getMethod(BaseRequest request) {
        if (request instanceof GetRequest) return "get";
        if (request instanceof PostRequest) return "post";
        return "";
    }

    public static BaseRequest createRequest(String url, String method) {
        if (method.equals("get")) return new GetRequest(url);
        if (method.equals("post")) return new PostRequest(url);
        return null;
    }
}
