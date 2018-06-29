package com.wisemen.taskrunner.local;

import com.wisemen.taskrunner.listener.LocalListener;
import com.wisemen.taskrunner.local.call.Callable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 全局的本地任务管理
 */
public class LocalManager {

    public static final int NONE = 0;
    public static final int WAITING = 1;
    public static final int RUNNING = 2;
    public static final int FINISH = 3;
    public static final int ERROR = 4;

    private List<LocalInfo> mLocalInfoList;
    private LocalUIHandler mLocalUIHandler;
    private static LocalManager mInstance;
    private LocalThreadPool threadPool;

    public static LocalManager getInstance() {
        if (null == mInstance) {
            synchronized (LocalManager.class) {
                if (null == mInstance) {
                    mInstance = new LocalManager();
                }
            }
        }

        return mInstance;
    }

    private LocalManager() {
        mLocalInfoList = Collections.synchronizedList(new ArrayList<LocalInfo>());
        mLocalUIHandler = new LocalUIHandler();
        threadPool = new LocalThreadPool();
    }

    /** 添加一个本地任务 */
    public <T> void addTask(String taskKey, LocalListener<T> listener, Callable<T> callable) {
        LocalInfo localInfo = new LocalInfo();
        localInfo.setTaskKey(taskKey);
        localInfo.setState(LocalManager.NONE);
        mLocalInfoList.add(localInfo);

        LocalTask localTask = new LocalTask<T>(localInfo, listener, callable);
        localInfo.setTask(localTask);
    }

    public LocalThreadPool getThreadPool() {
        return threadPool;
    }

    public LocalUIHandler getHandler() {
        return mLocalUIHandler;
    }

    public List<LocalInfo> getAllTask() {
        return mLocalInfoList;
    }
}