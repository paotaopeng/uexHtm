package com.wisemen.taskrunner.local;

import com.wisemen.taskrunner.task.ExecutorWithListener;
import com.wisemen.taskrunner.task.PriorityBlockingQueue;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 上传管理的线程池
 */
public class LocalThreadPool {
    private static final int MAXIMUM_POOL_SIZE = 5;     //最大线程池的数量
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit UNIT = TimeUnit.HOURS;
    private int corePoolSize = 3;
    private ExecutorWithListener executor;

    public ExecutorWithListener getExecutor() {
        if (executor == null) {
            synchronized (LocalThreadPool.class) {
                if (executor == null) {
                    executor = new ExecutorWithListener(corePoolSize, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, UNIT,
                            new PriorityBlockingQueue<Runnable>(),
                            Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }
        return executor;
    }

    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize <= 0) corePoolSize = 1;
        if (corePoolSize > MAXIMUM_POOL_SIZE) corePoolSize = MAXIMUM_POOL_SIZE;
        this.corePoolSize = corePoolSize;
    }

    /** 执行任务 */
    public void execute(Runnable runnable) {
        if (runnable != null) {
            getExecutor().execute(runnable);
        }
    }

    /** 移除线程 */
    public void remove(Runnable runnable) {
        if (runnable != null) {
            getExecutor().remove(runnable);
        }
    }
}