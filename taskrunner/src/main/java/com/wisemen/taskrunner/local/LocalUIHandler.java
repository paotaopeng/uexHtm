package com.wisemen.taskrunner.local;

import android.os.Handler;
import android.os.Message;

import com.wisemen.taskrunner.listener.LocalListener;

/**
 * 用于在主线程回调更新UI
 */
public class LocalUIHandler extends Handler {

    private LocalListener mGlobalLocalListener;

    @Override
    public void handleMessage(Message msg) {
        MessageBean messageBean = (MessageBean) msg.obj;
        if (messageBean != null) {
            LocalInfo info = messageBean.localInfo;
            String errorMsg = messageBean.errorMsg;
            Exception e = messageBean.e;
            Object t = messageBean.data;
            if (mGlobalLocalListener != null) {
                executeListener(mGlobalLocalListener, info, t, errorMsg, e);
            }
            LocalListener listener = info.getListener();
            if (listener != null) executeListener(listener, info, t, errorMsg, e);
        }
    }

    private void executeListener(LocalListener listener, LocalInfo info, Object t, String errorMsg, Exception e) {
        int state = info.getState();
        switch (state) {
            case LocalManager.FINISH:
                listener.onFinish(t);
                break;
            case LocalManager.ERROR:
                listener.onError(info, errorMsg, e);
                break;
        }
    }

    public void setGlobalLocalListener(LocalListener localListener) {
        this.mGlobalLocalListener = localListener;
    }

    public static class MessageBean<T> {
        public LocalInfo localInfo;
        public T data;
        public String errorMsg;
        public Exception e;
    }
}