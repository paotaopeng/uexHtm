package com.wisemen.taskrunner.download;

import android.os.Environment;
import android.text.TextUtils;

import com.wisemen.taskrunner.download.db.DownloadDBManager;
import com.wisemen.taskrunner.listener.DownloadListener;
import com.wisemen.taskrunner.task.ExecutorWithListener;
import com.wisemen.taskwalker.request.BaseRequest;
import com.wisemen.taskwalker.utils.NetStatusTool;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * 下载管理
 */
public class DownloadManager {
    //默认的下载目标文件夹
    public static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator;
    public static final int NONE = 0;
    public static final int WAITING = 1;
    public static final int DOWNLOADING = 2;
    public static final int PAUSE = 3;
    public static final int FINISH = 4;
    public static final int ERROR = 5;

    private List<DownloadInfo> mDownloadInfoList;   //所有下载任务的集合
    private DownloadUIHandler mDownloadUIHandler;   //主线程执行的handler
    private String mTargetFolder;                   //下载目录
    private static DownloadManager mInstance;
    private DownloadThreadPool threadPool;

    public static DownloadManager getInstance() {
        if (null == mInstance) {
            synchronized (DownloadManager.class) {
                if (null == mInstance) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    private DownloadManager() {
        mDownloadInfoList = Collections.synchronizedList(new ArrayList<DownloadInfo>());
        mDownloadUIHandler = new DownloadUIHandler();
        threadPool = new DownloadThreadPool();
        String folder = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
        if (!new File(folder).exists()) new File(folder).mkdirs();
        mTargetFolder = folder;

        mDownloadInfoList = DownloadDBManager.INSTANCE.getAll(); //获取所有任务
        if (mDownloadInfoList != null && !mDownloadInfoList.isEmpty()) {
            for (DownloadInfo info : mDownloadInfoList) {
                if (info.getState() == WAITING || info.getState() == DOWNLOADING || info.getState() == PAUSE) {
                    info.setState(NONE);
                    info.setNetworkSpeed(0);
                    DownloadDBManager.INSTANCE.replace(info);
                }
            }
        }
    }

    public boolean addTask(String taskTag, BaseRequest request, DownloadListener listener) {
        return addTask(null, taskTag, null, request, listener, false);
    }

    public boolean addTask(String taskTag, Serializable data, BaseRequest request, DownloadListener listener) {
        return addTask(null, taskTag, data, request, listener, false);
    }

    public boolean addTask(String fileName, String taskTag, BaseRequest request, DownloadListener listener) {
        return addTask(fileName, taskTag, null, request, listener, false);
    }

    public boolean addTask(String fileName, String taskTag,Serializable data, BaseRequest request, DownloadListener listener) {
        return addTask(fileName, taskTag, data, request, listener, false);
    }

    /**
     * 添加一个下载任务。返回true表示任务已经加入到任务队列，false表示未执行该任务
     *
     * @param request   下载的网络请求
     * @param listener  下载监听
     * @param isRestart 是否重新开始下载
     */
    private boolean addTask(String fileName, String taskTag, Serializable data, BaseRequest request, DownloadListener listener, boolean isRestart) {
        if(!NetStatusTool.getInstance().isWifi() && !request.isIndependentOfNetStatus()
                && (null == listener || !listener.onBefore(request))) {
            return false;
        }

        DownloadInfo downloadInfo = getDownloadInfo(taskTag);
        if (downloadInfo == null) {
            downloadInfo = new DownloadInfo();
            downloadInfo.setUrl(request.getBaseUrl());
            downloadInfo.setTaskKey(taskTag);
            downloadInfo.setFileName(fileName);
            downloadInfo.setRequest(request);
            downloadInfo.setState(DownloadManager.NONE);
            downloadInfo.setTargetFolder(mTargetFolder);
            downloadInfo.setData(data);
            DownloadDBManager.INSTANCE.replace(downloadInfo);
            mDownloadInfoList.add(downloadInfo);
        }

        if (downloadInfo.getState() == DownloadManager.NONE || downloadInfo.getState() == DownloadManager.PAUSE || downloadInfo.getState() == DownloadManager.ERROR) {
            DownloadTask downloadTask = new DownloadTask(downloadInfo, isRestart, listener);
            downloadInfo.setTask(downloadTask);
        } else if (downloadInfo.getState() == DownloadManager.FINISH) {
            //如果已经下载完成的文件不存在，则重新下载。2017-5-8
            if(fileName == null && taskTag != null) {
                fileName = taskTag.substring(taskTag.lastIndexOf("/")+1);
            }

            String downloadFileName = new StringBuilder().append(mTargetFolder).append(fileName).toString();
            File downloadFile = new File(downloadFileName);
            if (downloadFile.exists() && downloadFile.length() == downloadInfo.getTotalLength()) {
                if (listener != null) listener.onFinish(downloadInfo);
            } else {
                DownloadTask downloadTask = new DownloadTask(downloadInfo, true, listener);
                downloadInfo.setTask(downloadTask);
            }
        }

        return true;
    }

    /** 开始所有任务 */
    public void startAllTask() {
        for (DownloadInfo downloadInfo : mDownloadInfoList) {
            addTask(downloadInfo.getTaskKey(), downloadInfo.getRequest(), downloadInfo.getListener());
        }
    }

    /** 暂停任务 */
    public void pauseTask(String taskKey) {
        DownloadInfo downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;
        int state = downloadInfo.getState();

        if ((state == DOWNLOADING || state == WAITING) && downloadInfo.getTask() != null) {
            downloadInfo.getTask().pause();
        }
    }

    /** 暂停全部任务。先暂停没有下载的，再暂停下载中的 */
    public void pauseAllTask() {
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() != DOWNLOADING) pauseTask(info.getTaskKey());
        }
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() == DOWNLOADING) pauseTask(info.getTaskKey());
        }
    }

    /** 停止任务 */
    public void stopTask(final String taskKey) {
        DownloadInfo downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;

        if ((downloadInfo.getState() != NONE && downloadInfo.getState() != FINISH) && downloadInfo.getTask() != null) {
            downloadInfo.getTask().stop();
        }
    }

    /** 停止全部任务。先停止没有下载的，再停止下载中的 */
    public void stopAllTask() {
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() != DOWNLOADING) stopTask(info.getUrl());
        }
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() == DOWNLOADING) stopTask(info.getUrl());
        }
    }

    /** 删除一个任务,并且删除下载文件 */
    public void removeTask(String taskKey) {
        removeTask(taskKey, false);
    }

    /** 删除一个任务,并且删除下载文件 */
    public void removeTask(String taskKey, boolean isDeleteFile) {
        final DownloadInfo downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;
        pauseTask(taskKey);

        removeTaskByKey(taskKey);
        if (isDeleteFile) deleteFile(downloadInfo.getTargetPath());
        DownloadDBManager.INSTANCE.delete(taskKey);
    }

    /** 删除所有任务 */
    public void removeAllTask() {
        List<String> taskKeys = new ArrayList<>();
        for (DownloadInfo info : mDownloadInfoList) {
            taskKeys.add(info.getTaskKey());
        }
        for (String url : taskKeys) {
            removeTask(url);
        }
    }

    /** 重新下载 */
    public void restartTask(final String taskKey) {
        final DownloadInfo downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo != null && downloadInfo.getState() == DOWNLOADING) {
            //如果正在下载中，先暂停，等任务结束后再添加到队列开始下载
            pauseTask(taskKey);
            threadPool.getExecutor().addOnTaskEndListener(new ExecutorWithListener.OnTaskEndListener() {
                @Override
                public void onTaskEnd(Runnable r) {
                    if (r == downloadInfo.getTask().getRunnable()) {
                        threadPool.getExecutor().removeOnTaskEndListener(this);
                        addTask(downloadInfo.getFileName(), downloadInfo.getTaskKey(), downloadInfo.getData(), downloadInfo.getRequest(), downloadInfo.getListener(), true);
                    }
                }
            });
        } else {
            pauseTask(taskKey);
            restartTaskByKey(taskKey);
        }
    }

    /** 重新开始下载任务 */
    private void restartTaskByKey(String taskKey) {
        DownloadInfo downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;
        if (downloadInfo.getState() != DOWNLOADING) {
            DownloadTask downloadTask = new DownloadTask(downloadInfo, true, downloadInfo.getListener());
            downloadInfo.setTask(downloadTask);
        }
    }

    /** 获取一个任务 */
    public DownloadInfo getDownloadInfo(String taskKey) {
        for (DownloadInfo downloadInfo : mDownloadInfoList) {
            if (taskKey.equals(downloadInfo.getTaskKey())) {
                return downloadInfo;
            }
        }
        return null;
    }

    /** 移除一个任务 */
    private void removeTaskByKey(String taskKey) {
        ListIterator<DownloadInfo> iterator = mDownloadInfoList.listIterator();
        while (iterator.hasNext()) {
            DownloadInfo info = iterator.next();
            if (taskKey.equals(info.getTaskKey())) {
                DownloadListener listener = info.getListener();
                if (listener != null) listener.onRemove(info);
                info.removeListener();
                iterator.remove();
                break;
            }
        }
    }

    /** 根据路径删除文件 */
    private boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) return true;
        File file = new File(path);
        if (!file.exists()) return true;
        if (file.isFile()) return file.delete();
        return false;
    }

    /** 设置下载目标目录 */
    public String getTargetFolder() {
        return mTargetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.mTargetFolder = targetFolder;
    }

    public DownloadThreadPool getThreadPool() {
        return threadPool;
    }

    public DownloadUIHandler getHandler() {
        return mDownloadUIHandler;
    }

    public List<DownloadInfo> getAllTask() {
        return mDownloadInfoList;
    }
}