package com.wisemen.taskrunner.upload;

import com.wisemen.taskrunner.listener.UploadListener;
import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.request.BaseBodyRequest;
import com.wisemen.taskwalker.request.PostRequest;
import com.wisemen.taskwalker.utils.NetStatusTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * 全局的上传管理
 */
public class UploadManager {

    public static final int NONE = 0;
    public static final int WAITING = 1;
    public static final int UPLOADING = 2;
    public static final int FINISH = 3;
    public static final int ERROR = 4;

    private List<UploadInfo> mUploadInfoList;       //所有上传任务的集合
    private UploadUIHandler mUploadUIHandler;
    private static UploadManager mInstance;
    private UploadThreadPool threadPool;

    public static UploadManager getInstance() {
        if (null == mInstance) {
            synchronized (UploadManager.class) {
                if (null == mInstance) {
                    mInstance = new UploadManager();
                }
            }
        }
        return mInstance;
    }

    private UploadManager() {
        mUploadInfoList = Collections.synchronizedList(new ArrayList<UploadInfo>());
        mUploadUIHandler = new UploadUIHandler();
        threadPool = new UploadThreadPool();
    }

    @Deprecated
    public <T> boolean addTask(String url, File resource, String key, UploadListener<T> listener) {
        PostRequest request = TaskWalker.post(url).params(key, resource);
        return addTask(url, request, listener);
    }

    /** 添加一个上传任务。返回true表示任务已经加入到任务队列，false表示未执行该任务 */
    public <T> boolean addTask(String taskKey, BaseBodyRequest request, UploadListener<T> listener) {
        if(!NetStatusTool.getInstance().isWifi() && !request.isIndependentOfNetStatus()
                && (null == listener || !listener.onBefore(request))) {
            return false;
        }

        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.setTaskKey(taskKey);
        uploadInfo.setState(UploadManager.NONE);
        uploadInfo.setRequest(request);
        mUploadInfoList.add(uploadInfo);

        UploadTask uploadTask = new UploadTask<T>(uploadInfo, listener);
        uploadInfo.setTask(uploadTask);

        return true;
    }

    public void removeTask(String taskKey) {
        final UploadInfo uploadInfo = getUploadInfo(taskKey);
        if (uploadInfo == null) return;
        stopTask(taskKey);
        removeTaskByKey(taskKey);
    }

    /** 移除一个任务 */
    private void removeTaskByKey(String taskKey) {
        ListIterator<UploadInfo> iterator = mUploadInfoList.listIterator();
        while (iterator.hasNext()) {
            UploadInfo info = iterator.next();
            if (taskKey.equals(info.getTaskKey())) {
                UploadListener listener = info.getListener();
                if (listener != null) listener.onRemove(info);
                info.removeListener();
                iterator.remove();
                break;
            }
        }
    }

    /** 停止任务 */
    public void stopTask(String taskKey) {
        UploadInfo uploadInfo = getUploadInfo(taskKey);
        if (uploadInfo == null) return;
        int state = uploadInfo.getState();

        if ((state == UPLOADING || state == WAITING) && uploadInfo.getTask() != null) {
            uploadInfo.getTask().stop();
        }
    }

    /** 获取一个任务 */
    public UploadInfo getUploadInfo(String taskKey) {
        for (UploadInfo uploadInfo : mUploadInfoList) {
            if (taskKey.equals(uploadInfo.getTaskKey())) {
                return uploadInfo;
            }
        }
        return null;
    }

    public UploadThreadPool getThreadPool() {
        return threadPool;
    }

    public UploadUIHandler getHandler() {
        return mUploadUIHandler;
    }

    public List<UploadInfo> getAllTask() {
        return mUploadInfoList;
    }
}