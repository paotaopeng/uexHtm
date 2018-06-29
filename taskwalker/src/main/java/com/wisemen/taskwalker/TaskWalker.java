package com.wisemen.taskwalker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.wisemen.taskwalker.https.HttpsUtils;
import com.wisemen.taskwalker.interceptor.HttpLoggingInterceptor;
import com.wisemen.taskwalker.model.HttpHeaders;
import com.wisemen.taskwalker.model.HttpParams;
import com.wisemen.taskwalker.request.GetRequest;
import com.wisemen.taskwalker.request.PostRequest;
import com.wisemen.taskwalker.utils.Logger;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * 网络请求的入口类
 */
public class TaskWalker {
    public static final int DEFAULT_MILLISECONDS = 60000;       //默认的超时时间
    public static int REFRESH_TIME = 100;                       //回调刷新时间（单位ms）

    private Handler mDelivery;                                  //用于在主线程执行的调度器
    private OkHttpClient.Builder okHttpClientBuilder;
    private OkHttpClient okHttpClient;
    private HttpParams mCommonParams;                           //全局公共请求参数
    private HttpHeaders mCommonHeaders;                         //全局公共请求头
    private int mRetryCount = 3;                                //全局超时重试次数
    private static Context context;                              //全局上下文

    private TaskWalker() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static TaskWalker getInstance() {
        return TaskWalkerHolder.holder;
    }

    private static class TaskWalkerHolder {
        private static TaskWalker holder = new TaskWalker();
    }

    /** 缓存context上下文，必须在全局Application先调用 */
    public static void init(Context app) {
        context = app;
    }

    /** 获取全局上下文 */
    public static Context getContext() {
        if (context == null) throw new IllegalStateException("加载全局上下文失败");
        return context;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) okHttpClient = okHttpClientBuilder.build();
        return okHttpClient;
    }

    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return okHttpClientBuilder;
    }

    /** get请求 */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    /** post请求 */
    public static PostRequest post(String url) {
        return new PostRequest(url);
    }

    /** 调试模式 */
    public TaskWalker debug(String tag) {
        debug(tag, Level.INFO, true);
        return this;
    }

    /**
     * 调试模式。isPrintException表示所有catch住的log是否需要打印
     */
    public TaskWalker debug(String tag, Level level, boolean isPrintException) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(tag);
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(level);
        okHttpClientBuilder.addInterceptor(loggingInterceptor);
        Logger.debug(isPrintException);
        return this;
    }

    /** https的自定义域名访问规则 */
    public TaskWalker setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /**
     * https单向认证
     * 用含有服务端公钥的证书校验服务端证书
     */
    public TaskWalker setCertificates(InputStream... certificates) {
        setCertificates(null, null, certificates);
        return this;
    }

    /**
     * https单向认证
     * 可以额外配置信任服务端的证书策略，否则默认是按CA证书去验证的，若不是CA可信任的证书，则无法通过验证
     */
    public TaskWalker setCertificates(X509TrustManager trustManager) {
        setCertificates(null, null, trustManager);
        return this;
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * certificates -> 用含有服务端公钥的证书校验服务端证书
     */
    public TaskWalker setCertificates(InputStream bksFile, String password, InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, bksFile, password, certificates);
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * X509TrustManager -> 如果需要自己校验，那么可以自己实现相关校验，如果不需要自己校验，那么传null即可
     */
    public TaskWalker setCertificates(InputStream bksFile, String password, X509TrustManager trustManager) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(trustManager, bksFile, password, null);
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    /** 全局读取超时时间 */
    public TaskWalker setReadTimeOut(long readTimeOut) {
        okHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 全局写入超时时间 */
    public TaskWalker setWriteTimeOut(long writeTimeout) {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 全局连接超时时间 */
    public TaskWalker setConnectTimeout(long connectTimeout) {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 超时重试次数 */
    public TaskWalker setRetryCount(int retryCount) {
        if (retryCount < 0) throw new IllegalArgumentException("retryCount must > 0");
        mRetryCount = retryCount;
        return this;
    }

    /** 超时重试次数 */
    public int getRetryCount() {
        return mRetryCount;
    }

    /** 获取全局公共请求参数 */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /** 添加全局公共请求参数 */
    public TaskWalker addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /** 获取全局公共请求头 */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /** 添加全局公共请求参数 */
    public TaskWalker addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /** 添加全局拦截器 */
    public TaskWalker addInterceptor(Interceptor interceptor) {
        okHttpClientBuilder.addInterceptor(interceptor);
        return this;
    }

    /** 根据Tag取消请求 */
    public void cancelTag(Object tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /** 取消所有请求请求 */
    public void cancelAll() {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }
}