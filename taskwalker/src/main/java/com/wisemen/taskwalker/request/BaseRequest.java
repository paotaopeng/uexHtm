package com.wisemen.taskwalker.request;

import android.text.TextUtils;

import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.call.RealCall;
import com.wisemen.taskwalker.callback.AbsCallback;
import com.wisemen.taskwalker.convert.Converter;
import com.wisemen.taskwalker.model.HttpHeaders;
import com.wisemen.taskwalker.model.HttpParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseRequest<R extends BaseRequest> {

    protected String url;
    protected String method;
    protected String baseUrl;
    protected Object tag;
    protected long readTimeOut;
    protected long writeTimeOut;
    protected long connectTimeout;
    protected int retryCount;
    protected HttpParams params = new HttpParams();
    protected HttpHeaders headers = new HttpHeaders();
    protected List<Interceptor> interceptors = new ArrayList<>();

    private AbsCallback mCallback;
    private Converter mConverter;
    private Request mRequest;

    private boolean independentOfNetStatus = false;

    public BaseRequest(String url) {
        this.url = url;
        baseUrl = url;
        TaskWalker go = TaskWalker.getInstance();
        //默认添加 Accept-Language
        String acceptLanguage = HttpHeaders.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage)) headers(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
        //默认添加 User-Agent
        String userAgent = HttpHeaders.getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) headers(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent);
        //添加公共请求参数
        if (go.getCommonParams() != null) params.put(go.getCommonParams());
        if (go.getCommonHeaders() != null) headers.put(go.getCommonHeaders());
        //超时重试次数
        retryCount = go.getRetryCount();
    }

    @SuppressWarnings("unchecked")
    public R url(String url) {
        this.url = url;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R connTimeOut(long connTimeOut) {
        this.connectTimeout = connTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(HttpHeaders headers) {
        this.headers.put(headers);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeHeader(String key) {
        headers.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllHeaders() {
        headers.clear();
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(HttpParams params) {
        this.params.put(params);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(Map<String, String> params, boolean... isReplace) {
        this.params.put(params, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, String value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, int value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, float value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, double value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, long value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, char value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, boolean value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R addUrlParams(String key, List<String> values) {
        params.putUrlParams(key, values);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeParam(String key) {
        params.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllParams() {
        params.clear();
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R setCallback(AbsCallback callback) {
        this.mCallback = callback;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R setIndependentOfNetStatus(boolean independency) {
        this.independentOfNetStatus = independency;
        return (R) this;
    }

    /** 默认返回第一个参数 */
    public String getUrlParam(String key) {
        List<String> values = params.urlParamsMap.get(key);
        if (values != null && values.size() > 0) return values.get(0);
        return null;
    }

    /** 默认返回第一个参数 */
    public HttpParams.FileWrapper getFileParam(String key) {
        List<HttpParams.FileWrapper> values = params.fileParamsMap.get(key);
        if (values != null && values.size() > 0) return values.get(0);
        return null;
    }

    public HttpParams getParams() {
        return params;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getUrl() {
        return url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Object getTag() {
        return tag;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Request getRequest() {
        return mRequest;
    }

    public AbsCallback getCallback() {
        return mCallback;
    }

    public Converter getConverter() {
        return mConverter;
    }

    public boolean isIndependentOfNetStatus() {
        return independentOfNetStatus;
    }

    /**
     * 返回当前的请求方法
     */
    public String getMethod() {
        return method;
    }

    /** 根据不同的请求方式和参数，生成不同的RequestBody */
    public abstract RequestBody generateRequestBody();

    /** 对请求body进行包装，用于回调上传进度 */
    public RequestBody wrapRequestBody(RequestBody requestBody) {
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody);
        progressRequestBody.setListener(new ProgressRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength, final long networkSpeed) {
                TaskWalker.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) mCallback.upProgress(bytesWritten, contentLength, bytesWritten * 1.0f / contentLength, networkSpeed);
                    }
                });
            }
        });
        return progressRequestBody;
    }

    /** 根据不同的请求方式，将RequestBody转换成Request对象 */
    public abstract Request generateRequest(RequestBody requestBody);

    /** 根据当前的请求参数，生成对应的 Call 任务 */
    public okhttp3.Call generateCall(Request request) {
        mRequest = request;
        if (readTimeOut <= 0 && writeTimeOut <= 0 && connectTimeout <= 0 && interceptors.size() == 0) {
            return TaskWalker.getInstance().getOkHttpClient().newCall(request);
        } else {
            OkHttpClient.Builder newClientBuilder = TaskWalker.getInstance().getOkHttpClient().newBuilder();
            if (readTimeOut > 0) newClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
            if (writeTimeOut > 0) newClientBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
            if (connectTimeout > 0) newClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            if (interceptors.size() > 0) {
                for (Interceptor interceptor : interceptors) {
                    newClientBuilder.addInterceptor(interceptor);
                }
            }
            return newClientBuilder.build().newCall(request);
        }
    }

    /** 获取同步call对象 */
    public okhttp3.Call getCall() {
        //构建请求体，返回call对象
        RequestBody requestBody = generateRequestBody();
        mRequest = generateRequest(wrapRequestBody(requestBody));
        return generateCall(mRequest);
    }

    /** 同步请求 */
    public Response execute() throws IOException {
        return getCall().execute();
    }

    /** 异步请求。回调在子线程中执行，返回true表示任务已经加入到任务队列，false表示未执行该任务 */
    @SuppressWarnings("unchecked")
    public <T> boolean execute(AbsCallback<T> callback) {
        mCallback = callback;
        mConverter = callback;
        return new RealCall<T>(this).execute(callback);
    }
}