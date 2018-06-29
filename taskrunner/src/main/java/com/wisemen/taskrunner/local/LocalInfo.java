package com.wisemen.taskrunner.local;

import com.wisemen.taskrunner.listener.LocalListener;

/**
 * 本地任务的信息
 */
public class LocalInfo {

    private String taskKey;                     //本地任务的标识
    private int state;                          //当前状态
    private LocalTask task;                    //执行当前的本地任务
    private LocalListener listener;            //当前本地任务的监听

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public LocalTask getTask() {
        return task;
    }

    public void setTask(LocalTask task) {
        this.task = task;
    }

    public LocalListener getListener() {
        return listener;
    }

    public void setListener(LocalListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    /** taskKey 相同就认为是同一个任务 */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof LocalInfo) {
            LocalInfo info = (LocalInfo) o;
            return getTaskKey().equals(info.getTaskKey());
        }
        return false;
    }
}