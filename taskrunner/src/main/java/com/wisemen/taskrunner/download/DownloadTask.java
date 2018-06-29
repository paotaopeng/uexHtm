package com.wisemen.taskrunner.download;

import android.os.Message;
import android.text.TextUtils;

import com.wisemen.taskrunner.listener.DownloadListener;
import com.wisemen.taskrunner.task.PriorityAsyncTask;

import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.utils.HttpUtils;
import com.wisemen.taskrunner.download.db.DownloadDBManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * 文件的下载任务类
 */
public class DownloadTask extends PriorityAsyncTask<Void, DownloadInfo, DownloadInfo> {

    private static final int BUFFER_SIZE = 1024 * 8; //读写缓存大小

    private DownloadUIHandler mDownloadUIHandler;
    private DownloadInfo mDownloadInfo;              //当前任务的信息
    private long mPreviousTime;                      //上次更新的时间，用于计算下载速度
    private boolean isRestartTask;                   //是否重新下载的标识位
    private boolean isPause;                         //当前任务是暂停还是停止，true 暂停， false 停止

    public DownloadTask(DownloadInfo downloadInfo, boolean isRestart, DownloadListener downloadListener) {
        mDownloadInfo = downloadInfo;
        isRestartTask = isRestart;
        mDownloadInfo.setListener(downloadListener);
        mDownloadUIHandler = DownloadManager.getInstance().getHandler();
        //将当前任务在定义的线程池中执行
        executeOnExecutor(DownloadManager.getInstance().getThreadPool().getExecutor());
    }

    public void pause() {
        if (mDownloadInfo.getState() == DownloadManager.WAITING) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.PAUSE);
            postMessage(null, null);
        } else {
            isPause = true;
        }
        super.cancel(false);
    }

    public void stop() {
        if (mDownloadInfo.getState() == DownloadManager.PAUSE || mDownloadInfo.getState() == DownloadManager.ERROR || mDownloadInfo.getState() == DownloadManager.WAITING) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.NONE);
            postMessage(null, null);
        } else {
            isPause = false;
        }
        super.cancel(false);
    }

    /** 每个任务进队列的时候，都会执行该方法 */
    @Override
    protected void onPreExecute() {
        DownloadListener listener = mDownloadInfo.getListener();
        if (listener != null) listener.onAdd(mDownloadInfo);

        //如果是重新下载，删除临时文件
        if (isRestartTask) {
            HttpUtils.deleteFile(mDownloadInfo.getTargetPath());
            mDownloadInfo.setProgress(0);
            mDownloadInfo.setDownloadLength(0);
            mDownloadInfo.setTotalLength(0);
            isRestartTask = false;
        }

        mDownloadInfo.setNetworkSpeed(0);
        mDownloadInfo.setState(DownloadManager.WAITING);
        postMessage(null, null);
    }

    @Override
    protected void onPostExecute(DownloadInfo downloadInfo) {
        // 调用Cancel时不会执行该方法
    }

    @Override
    protected DownloadInfo doInBackground(Void... params) {
        if (isCancelled()) return mDownloadInfo;
        mPreviousTime = System.currentTimeMillis();
        mDownloadInfo.setNetworkSpeed(0);
        mDownloadInfo.setState(DownloadManager.DOWNLOADING);
        postMessage(null, null);

        long startPos = mDownloadInfo.getDownloadLength();
        Response response;
        try {
            response = mDownloadInfo.getRequest().headers("RANGE", "bytes=" + startPos + "-").execute();
        } catch (IOException e) {
            e.printStackTrace();
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("网络异常", e);
            return mDownloadInfo;
        }
        int code = response.code();
        if (code == 404 || code >= 500) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("服务器数据错误", null);
            return mDownloadInfo;
        }

        String url = mDownloadInfo.getUrl();
        String fileName = mDownloadInfo.getFileName();
        if (TextUtils.isEmpty(fileName)) {
            fileName = HttpUtils.getNetFileName(response, url);
            mDownloadInfo.setFileName(fileName);
        }
        if (TextUtils.isEmpty(mDownloadInfo.getTargetPath())) {
            File targetFolder = new File(mDownloadInfo.getTargetFolder());
            if (!targetFolder.exists()) targetFolder.mkdirs();
            File file = new File(targetFolder, fileName);
            mDownloadInfo.setTargetPath(file.getAbsolutePath());
        }
        //检查文件有效性
        if (startPos > mDownloadInfo.getTotalLength()) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("断点文件异常，需要删除后重新下载", null);
            return mDownloadInfo;
        }
        if (startPos == mDownloadInfo.getTotalLength() && startPos > 0) {
            mDownloadInfo.setProgress(1.0f);
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.FINISH);
            postMessage(null, null);
            return mDownloadInfo;
        }
        //设置断点写文件
        File file = new File(mDownloadInfo.getTargetPath());
        ProgressRandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new ProgressRandomAccessFile(file, "rw", startPos);
            randomAccessFile.seek(startPos);
        } catch (Exception e) {
            e.printStackTrace();
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("没有找到已存在的断点文件", e);
            return mDownloadInfo;
        }

        long totalLength = response.body().contentLength();
        if (mDownloadInfo.getTotalLength() == 0) {
            mDownloadInfo.setTotalLength(totalLength);
        }
        InputStream is = response.body().byteStream();
        //读写文件流
        try {
            download(is, randomAccessFile);
        } catch (IOException e) {
            e.printStackTrace();
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("文件读写异常", e);
            return mDownloadInfo;
        }

        if (isCancelled()) {
            mDownloadInfo.setNetworkSpeed(0);
            if (isPause) mDownloadInfo.setState(DownloadManager.PAUSE); //暂停
            else mDownloadInfo.setState(DownloadManager.NONE);          //停止
            postMessage(null, null);
        } else if (file.length() == mDownloadInfo.getTotalLength() && mDownloadInfo.getState() == DownloadManager.DOWNLOADING) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.FINISH); //下载完成
            postMessage(null, null);
        } else if (file.length() != mDownloadInfo.getDownloadLength()) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("未知原因", null);
        }
        return mDownloadInfo;
    }

    private void postMessage(String errorMsg, Exception e) {
        DownloadDBManager.INSTANCE.update(mDownloadInfo);
        DownloadUIHandler.MessageBean messageBean = new DownloadUIHandler.MessageBean();
        messageBean.downloadInfo = mDownloadInfo;
        messageBean.errorMsg = errorMsg;
        messageBean.e = e;
        Message msg = mDownloadUIHandler.obtainMessage();
        msg.obj = messageBean;
        mDownloadUIHandler.sendMessage(msg);
    }

    /** 执行文件下载 */
    private int download(InputStream input, RandomAccessFile out) throws IOException {
        if (input == null || out == null) return -1;

        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        int downloadSize = 0;
        int len;
        try {
            while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1 && !isCancelled()) {
                out.write(buffer, 0, len);
                downloadSize += len;
            }
        } finally {
            try {
                out.close();
                in.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return downloadSize;
    }

    /** 文件读写 */
    private final class ProgressRandomAccessFile extends RandomAccessFile {
        private long lastDownloadLength = 0; //总共已下载的大小
        private long curDownloadLength = 0;  //当前已下载的大小
        private long lastRefreshUiTime;

        public ProgressRandomAccessFile(File file, String mode, long lastDownloadLength) throws FileNotFoundException {
            super(file, mode);
            this.lastDownloadLength = lastDownloadLength;
            this.lastRefreshUiTime = System.currentTimeMillis();
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);

            //已下载大小
            long downloadLength = lastDownloadLength + count;
            curDownloadLength += count;
            lastDownloadLength = downloadLength;
            mDownloadInfo.setDownloadLength(downloadLength);

            //计算下载速度
            long totalTime = (System.currentTimeMillis() - mPreviousTime) / 1000;
            if (totalTime == 0) {
                totalTime += 1;
            }
            long networkSpeed = curDownloadLength / totalTime;
            mDownloadInfo.setNetworkSpeed(networkSpeed);

            //下载进度
            float progress = downloadLength * 1.0f / mDownloadInfo.getTotalLength();
            mDownloadInfo.setProgress(progress);
            long curTime = System.currentTimeMillis();

            //每200毫秒刷新一次数据
            if (curTime - lastRefreshUiTime >= TaskWalker.REFRESH_TIME || progress == 1.0f) {
                postMessage(null, null);
                lastRefreshUiTime = System.currentTimeMillis();
            }
        }
    }
}