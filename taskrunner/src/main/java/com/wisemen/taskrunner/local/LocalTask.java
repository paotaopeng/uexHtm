package com.wisemen.taskrunner.local;

import android.os.Message;

import com.wisemen.taskrunner.listener.LocalListener;
import com.wisemen.taskrunner.local.call.Callable;
import com.wisemen.taskrunner.task.PriorityAsyncTask;

public class LocalTask<T> extends PriorityAsyncTask<Void, LocalInfo, LocalInfo> {

    private LocalUIHandler mLocalUIHandler;
    private LocalInfo mLocalInfo;              //当前任务的信息
    private Callable<T> mCallable;

    public LocalTask(LocalInfo localInfo, LocalListener<T> localListener, Callable<T> callable) {
        mLocalInfo = localInfo;
        mLocalInfo.setListener(localListener);
        mLocalUIHandler = LocalManager.getInstance().getHandler();
        mCallable = callable;
        //将当前任务在定义的线程池中执行
        executeOnExecutor(LocalManager.getInstance().getThreadPool().getExecutor());
    }

    /** 每个任务进队列的时候，都会执行该方法 */
    @Override
    protected void onPreExecute() {
        LocalListener listener = mLocalInfo.getListener();
        if (listener != null) listener.onAdd(mLocalInfo);

        mLocalInfo.setState(LocalManager.WAITING);
        //postMessage(null, null, null);
    }

    @Override
    protected LocalInfo doInBackground(Void... params) {
        if (isCancelled()) return mLocalInfo;
        mLocalInfo.setState(LocalManager.RUNNING);

        try {
            T results = mCallable.call();
            mLocalInfo.setState(LocalManager.FINISH);
            postMessage(results, null, null);
            return mLocalInfo;
        } catch (Exception e) {
            e.printStackTrace();
            mLocalInfo.setState(LocalManager.ERROR);
            postMessage(null, "任务执行过程中发生异常", e);
            return mLocalInfo;
        }
    }

    private void postMessage(T data, String errorMsg, Exception e) {
        LocalUIHandler.MessageBean<T> messageBean = new LocalUIHandler.MessageBean<>();
        messageBean.localInfo = mLocalInfo;
        messageBean.errorMsg = errorMsg;
        messageBean.e = e;
        messageBean.data = data;
        Message msg = mLocalUIHandler.obtainMessage();
        msg.obj = messageBean;
        mLocalUIHandler.sendMessage(msg);
    }
}