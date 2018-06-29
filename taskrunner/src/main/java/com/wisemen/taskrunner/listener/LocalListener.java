package com.wisemen.taskrunner.listener;

import com.wisemen.taskrunner.local.LocalInfo;

/**
 * 全局的本地任务监听
 */
public abstract class LocalListener<T> {

    /** 任务执行完成时回调 */
    public abstract void onFinish(T t);

    /** 任务执行出错时回调 */
    public abstract void onError(LocalInfo localInfo, String errorMsg, Exception e);

    /** 成功添加任务的回调 */
    public void onAdd(LocalInfo localInfo) {
    }

    /** 成功移除任务回调 */
    public void onRemove(LocalInfo localInfo) {
    }

    public static final LocalListener DEFAULT_LOCAL_LISTENER = new LocalListener() {
        @Override
        public void onFinish(Object localInfo) {
        }

        @Override
        public void onError(LocalInfo localInfo, String errorMsg, Exception e) {
        }
    };
}
