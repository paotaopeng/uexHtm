package org.zywx.wbpalmstar.plugin.uexhtm;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.wisemen.taskrunner.download.DownloadInfo;
import com.wisemen.taskrunner.download.DownloadManager;
import com.wisemen.taskrunner.download.DownloadService;
import com.wisemen.taskrunner.listener.DownloadListener;
import com.wisemen.taskrunner.listener.LocalListener;
import com.wisemen.taskrunner.listener.UploadListener;
import com.wisemen.taskrunner.local.LocalInfo;
import com.wisemen.taskrunner.local.LocalManager;
import com.wisemen.taskrunner.local.call.ParamCallable;
import com.wisemen.taskrunner.local.db.DataBaseExecutor;
import com.wisemen.taskrunner.upload.UploadInfo;
import com.wisemen.taskrunner.upload.UploadManager;
import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.callback.FileCallback;
import com.wisemen.taskwalker.model.HttpParams;
import com.wisemen.taskwalker.request.BaseRequest;
import com.wisemen.taskwalker.request.GetRequest;
import com.wisemen.taskwalker.request.PostRequest;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexhtm.model.BaseResponse;
import org.zywx.wbpalmstar.plugin.uexhtm.model.JsonCallback;
import org.zywx.wbpalmstar.plugin.uexhtm.model.ResourceInfo;
import org.zywx.wbpalmstar.plugin.uexhtm.util.DbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class EUExHtm extends EUExBase {
    private static final String TAG = "uexHtm";
    private static final String CALLBACK_CB_DOWNLOAD_SUCCESS = "uexHtm.cbDownloadSuccess";
    private static final String CALLBACK_CB_DOWNLOAD_PROGRESS = "uexHtm.cbDownloadProgress";
    private static final String CALLBACK_CB_DOWNLOAD_ERROR = "uexHtm.cbDownloadError";

    private static final String CALLBACK_CB_DOWNLOADBACKGROUND_SUCCESS = "uexHtm.cbDownloadBackgroundSuccess";
    private static final String CALLBACK_CB_DOWNLOADBACKGROUND_PROGRESS = "uexHtm.cbDownloadBackgroundProgress";
    private static final String CALLBACK_CB_DOWNLOADBACKGROUND_ERROR = "uexHtm.cbDownloadBackgroundError";

    private static final String CALLBACK_CB_UPLOAD_SUCCESS = "uexHtm.cbUploadSuccess";
    private static final String CALLBACK_CB_UPLOAD_ERROR = "uexHtm.cbUploadError";
    private static final String CALLBACK_CB_UPLOAD_PROGRESS = "uexHtm.cbUploadProgress";

    private static final String CALLBACK_CB_UPLOADBACKGROUND_SUCCESS = "uexHtm.cbUploadBackgroundSuccess";
    private static final String CALLBACK_CB_UPLOADBACKGROUND_ERROR = "uexHtm.cbUploadBackgroundError";
    private static final String CALLBACK_CB_UPLOADBACKGROUND_PROGRESS = "uexHtm.cbUploadBackgroundProgress";
    private DownloadManager downloadManager;
    private UploadManager uploadManager;
    private LocalManager localManager;
    private static int sCurrentId;

    public EUExHtm(Context context, EBrowserView view) {
        super(context, view);
        TaskWalker.init(context);
        downloadManager = DownloadService.getDownloadManager();
        uploadManager = UploadManager.getInstance();
        localManager = LocalManager.getInstance();
    }

    private int generateId(){
        sCurrentId++;
        return sCurrentId;
    }

    private String getTargetPath(String fileName){
        return String.format("/data/data/%s/hhb/%s",mContext.getPackageName(),fileName);
    }

    public String createDownloadTag(String[] params){
        return String.valueOf(generateId());
    }

    public void cancelDownload(String[] params){
        if(isEmpty(params[0]) || params.length > 1 ){
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        TaskWalker.getInstance().cancelTag(params[0]);
    }

    public void download(String[] params) {
        if (params == null || params.length < 2 || isEmpty(params[0]) || isEmpty(params[1])) {
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        String requestUrl = params[1];
        String savePath = getTargetPath(params[2]);
        TaskWalker.get(requestUrl)
                .tag(params[0])
                .headers("header1", "headerValue1")
                .params("param1", "paramValue1")
                .setIndependentOfNetStatus(true)
                .execute(new FileCallback(savePath) {
                    @Override
                    public boolean onBefore(BaseRequest request) {
                        Log.i(TAG, "正在下载中");
                        return super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        Log.i(TAG, "下载完成");
                        callBackPluginJs(CALLBACK_CB_DOWNLOAD_SUCCESS, "下载完成");
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        Log.i(TAG, "downloadProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);
                        callBackPluginJs(CALLBACK_CB_DOWNLOAD_PROGRESS,totalSize,currentSize,progress,networkSpeed);
                    }

                    @Override
                    public void onError(Call call, @Nullable Response response, @Nullable Exception e) {
                        if(!call.isCanceled()){
                            super.onError(call, response, e);
                            Log.i(TAG, "下载出错");
                            callBackPluginJs(CALLBACK_CB_DOWNLOAD_ERROR, "下载任务出错");
                        }

                    }
                });
    }


    private boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }


    public void downloadBatchBackground(String[] params) {
        if (params == null || params.length > 1) {
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONArray tasks=null;
        try {
            tasks = new JSONArray(params[0]);
        } catch (JSONException e) {
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        JSONObject jsonobj;
        ResourceInfo resourceInfo;
        MyDownloadListener downloadListener;
        String savePath,fileName,fileDir;
        try {
            for (int i = 0, len = tasks.length(); i < len; i++) {
                downloadListener=new MyDownloadListener();
                jsonobj = tasks.getJSONObject(i);
                resourceInfo = new ResourceInfo();
                resourceInfo.setUrl(jsonobj.getString("requestUrl"));
                savePath=jsonobj.getString("savePath");
                fileName=savePath.substring(savePath.lastIndexOf("/")+1);
                fileDir=savePath.substring(0,savePath.lastIndexOf("/")+1);
                resourceInfo.setName(fileName);
                downloadManager.setTargetFolder(fileDir);
                DownloadInfo downloadInfo = downloadManager.getDownloadInfo(resourceInfo.getUrl());
                if ( downloadInfo!= null && (downloadInfo.getState()==DownloadManager.DOWNLOADING || downloadInfo.getState()==DownloadManager.WAITING)) {
                    Toast.makeText(mContext, "任务已经在下载列表中", Toast.LENGTH_SHORT).show();
                } else {
                    GetRequest request = TaskWalker.get(resourceInfo.getUrl()).setIndependentOfNetStatus(true)
                            .headers("headerKey1", "headerValue1")
                            .headers("headerKey2", "headerValue2")
                            .params("paramKey1", "paramValue1")
                            .params("paramKey2", "paramValue2");
                    downloadManager.addTask(resourceInfo.getName(),resourceInfo.getUrl(), resourceInfo, request, downloadListener);
                }
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "下载任务失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void cancelDownloadTask(String[] params){
        if(isEmpty(params[0]) || params.length > 1 ){
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        downloadManager.pauseTask(params[0]);
    }


    private class  MyDownloadListener extends DownloadListener  {
        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            Log.i(TAG, "总大小：" + downloadInfo.getTotalLength() + " 当前大小：" + downloadInfo.getDownloadLength() + " 下载进度：" + downloadInfo.getProgress() + " 下载速度：" + downloadInfo.getNetworkSpeed());
            callBackPluginJs(CALLBACK_CB_DOWNLOADBACKGROUND_PROGRESS,downloadInfo.getTotalLength(),downloadInfo.getDownloadLength(),downloadInfo.getProgress(),downloadInfo.getNetworkSpeed());
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
            Log.i(TAG, downloadInfo.getUrl()+"下载成功");
            callBackPluginJs(CALLBACK_CB_DOWNLOADBACKGROUND_SUCCESS, "使用后台服务下载" + ((ResourceInfo) downloadInfo.getData()).getName() + "完成");
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
            if(!downloadInfo.getTask().isCancelled()){
                Log.i(TAG, downloadInfo.getUrl()+"使用后台服务下载任务出错");
                callBackPluginJs(CALLBACK_CB_DOWNLOADBACKGROUND_ERROR, "使用后台服务下载任务出错");
            }
        }
    };

    public String createUploadTag(String[] params){
        return String.valueOf(generateId());
    }

    public void cancelFormUpload(String[] params){
        if(isEmpty(params[0]) || params.length > 1 ){
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        TaskWalker.getInstance().cancelTag(params[0]);
    }

    public void formUpload(String[] params) {
        if (params == null || params.length >4  || isEmpty(params[0]) || isEmpty(params[1])) {
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        String requestUrl = params[1];
        JSONObject formParams=null;
        JSONArray fileParams=null;
        JSONObject jsonobj=null;
        List<HttpParams.FileWrapper> fileLists=null;
        Map<String,String> formMap=null;
        if(!isEmpty(params[2])){
            try {
                fileLists=new ArrayList<HttpParams.FileWrapper>();
                fileParams = new JSONArray(params[2]);
                for (int i = 0, len = fileParams.length(); i < len; i++) {
                    jsonobj = fileParams.getJSONObject(i);
                    String filePath=jsonobj.getString("filePath");
                    String fileName = jsonobj.getString("fileName");
                    HttpParams.FileWrapper fileWrapper=new HttpParams.FileWrapper(new File(filePath),fileName,HttpParams.guessMimeType(filePath));
                    fileLists.add(fileWrapper);
                }
            } catch (JSONException e) {
                Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
        }
        if(!isEmpty(params[3])){
            try {
                formMap=new HashMap<String,String>();
                formParams = new JSONObject(params[3]);
                Iterator iterator = formParams.keys();
                while(iterator.hasNext()){
                    String key = (String) iterator.next();
                    String value = formParams.getString(key);
                    formMap.put(key,value);
                }
            } catch (JSONException e) {
                Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
        }

        TaskWalker.post(requestUrl)
                .tag(params[0])
                .headers("header1", "headerValue1")
                .addMapParams(formMap)
                .addFileWrapperParams("upload",fileLists)
                .setIndependentOfNetStatus(true)
                .execute(new JsonCallback<BaseResponse<Map>>() {
                    @Override
                    public boolean onBefore(BaseRequest request) {
                        Log.i(TAG, "正在上传中...");
                        return super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(BaseResponse<Map> responseData, Call call, Response response) {
                        Log.i(TAG, "上传完成");
                        callBackPluginJs(CALLBACK_CB_UPLOAD_SUCCESS, "上传完成");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if(!call.isCanceled()){
                            super.onError(call, response, e);
                            Log.i(TAG, "上传出错");
                            callBackPluginJs(CALLBACK_CB_UPLOAD_ERROR, "上传出错");
                        }
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        Log.i(TAG, "upProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);
                        callBackPluginJs(CALLBACK_CB_UPLOAD_PROGRESS, totalSize, currentSize, progress, networkSpeed);
                    }
                });

    }

    public void uploadBatchBackground(String[] params) {
        if (params == null || params.length < 3 || isEmpty(params[0]) || isEmpty(params[1]) ||isEmpty(params[2])) {
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONArray tasks=null;
        try {
            tasks = new JSONArray(params[1]);
        } catch (JSONException e) {
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
        String requestUrl = params[0];
        File file;
        String filePath;
        MyUploadListener listener;
        try {
            for (int i = 0, len = tasks.length(); i < len; i++) {
                listener= new MyUploadListener();
                filePath = tasks.get(i).toString();
                file = new File(filePath);
                PostRequest postRequest = TaskWalker.post(requestUrl)
                        .headers("headerKey1", "headerValue1")
                        .headers("headerKey2", "headerValue2")
                        .params("paramKey1", "paramValue1")
                        .params("paramKey2", "paramValue2")
                        .params(params[2], file);
                uploadManager.addTask(file.getPath(), postRequest, listener);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "上传任务失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void cancelUploadTask(String[] params){
        if(isEmpty(params[0]) || params.length > 1 ){
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadManager.removeTask(params[0]);
    }

    private class MyUploadListener extends UploadListener<String> {
        @Override
        public void onProgress(UploadInfo uploadInfo) {
            Log.i("MyUploadListener", "onProgress:" + uploadInfo.getTotalLength() + " " + uploadInfo.getUploadLength() + " " + uploadInfo.getProgress()+" "+uploadInfo.getNetworkSpeed());
            //TODO
            callBackPluginJs(CALLBACK_CB_UPLOADBACKGROUND_PROGRESS,uploadInfo.getTotalLength(),uploadInfo.getUploadLength(),uploadInfo.getProgress(),uploadInfo.getNetworkSpeed());
        }

        @Override
        public void onFinish(UploadInfo uploadInfo,String s) {
            Log.i("MyUploadListener", "finish: url: " + uploadInfo.getTaskKey() + "response: " + s);
            //uploadManager.removeTask(uploadInfo.getUrl());
            callBackPluginJs(CALLBACK_CB_UPLOADBACKGROUND_SUCCESS, "上传完成");

        }

        @Override
        public void onError(UploadInfo uploadInfo, String errorMsg, Exception e) {
            if(!uploadInfo.getTask().isCancelled()){
                Log.i("MyUploadListener", "onError:" + errorMsg);
                uploadManager.removeTask(uploadInfo.getUrl());
                callBackPluginJs(CALLBACK_CB_UPLOADBACKGROUND_ERROR, "上传失败");
            }
        }

        @Override
        public String parseNetworkResponse(Response response) throws Exception {
            Log.i("MyUploadListener", "convertSuccess");
            return response.body().string();
        }
    }


    private void callBackPluginJs(String methodName, String jsonData) {
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }

    private void callBackPluginJs(String methodName, long totalSize, long currentSize, float progress, long speed) {
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "(" + totalSize + "," + currentSize + "," + progress + "," + speed + ");}";
        onCallback(js);
    }


    // clean something
    @Override
    protected boolean clean() {
        return true;
    }

    public void insertBatch(String[] params){
        if(isEmpty(params[0]) || params.length > 1 ){
            Toast.makeText(mContext, "参数设置错误", Toast.LENGTH_SHORT).show();
            return;
        }
        String jsonFilePath=params[0];
        String taskKey = "task_" + 0;
        MyLocalListener listener = new MyLocalListener();
        MyCallable callable = new MyCallable();
        Map<String, String> hashMapParams = new HashMap<String, String>();
        hashMapParams.put("taskKey", taskKey);
        hashMapParams.put("jsonFilePath",jsonFilePath);
        callable.setParams(hashMapParams);
        localManager.addTask(taskKey, listener, callable);
    }

    private class MyLocalListener extends LocalListener<String> {
        @Override
        public void onFinish(String s) {
            Log.e("MyLocalListener", "finish:" + s);
        }

        @Override
        public void onError(LocalInfo localInfo, String errorMsg, Exception e) {
            Log.e("MyLocalListener", "onError:" + errorMsg);
        }
    }

    private class MyCallable extends ParamCallable<String, String> {
        public String call() throws Exception {
            System.out.println(params.get("taskKey") + "... has been called");
            long startTime = System.currentTimeMillis();
            String jsonFilePath=params.get("jsonFilePath");
            File jsonFile = new File(jsonFilePath);
            if(!jsonFile.exists()){
                Log.e(TAG,jsonFilePath+"文件不存在");
                return "task can not run";
            }
            String jsonStr=FileUtils.readFileToString(jsonFile);
            List<String> sqlList = new ArrayList<String>();
            JSONObject jsonObj=new JSONObject(jsonStr);
            JSONObject data = (JSONObject)jsonObj.get("data");
            JSONArray sqlJsonArray = data.getJSONArray("sqlList");
            if(sqlJsonArray!=null && sqlJsonArray.length()>0){
                for(int i=0,len=sqlJsonArray.length();i<len;i++){
                    sqlList.add(sqlJsonArray.get(i).toString());
                }
                DbHelper helper = new DbHelper();
                DataBaseExecutor.execBatch(helper, sqlList);
            }else{
                return "there not have sql to exec";
            }
            Log.i("insertTest", String.valueOf(System.currentTimeMillis()- startTime));
            return "the results of " + params.get("taskKey");
        }
    }
}
