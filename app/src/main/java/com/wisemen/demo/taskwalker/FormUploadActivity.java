package com.wisemen.demo.taskwalker;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseDetailActivity;
import com.wisemen.demo.callback.JsonCallback;
import com.wisemen.demo.model.BaseResponse;
import com.wisemen.demo.ui.NumberProgressBar;
import com.wisemen.demo.utils.Urls;
import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.request.BaseRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class FormUploadActivity extends BaseDetailActivity {

    @Bind(R.id.formUpload) Button btnFormUpload;
    @Bind(R.id.downloadSize) TextView tvDownloadSize;
    @Bind(R.id.tvProgress) TextView tvProgress;
    @Bind(R.id.netSpeed) TextView tvNetSpeed;
    @Bind(R.id.pbProgress) NumberProgressBar pbProgress;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_form_upload);
        ButterKnife.bind(this);
        setTitle("文件上传");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        TaskWalker.getInstance().cancelTag(this);
    }

    @OnClick(R.id.formUpload)
    public void formUpload(View view) {
        //测试数据
        ArrayList<File> files = new ArrayList<>();
        File file1 = new File("/storage/emulated/0/Jeffrey/pic/史努比.jpg");
        File file2 = new File("/storage/emulated/0/Jeffrey/pic/WALL.E.jpg");
        files.add(file1);
        files.add(file2);

        TaskWalker.post(Urls.URL_FORM_UPLOAD)
                .tag(this)
                .headers("header1", "headerValue1")
                //.headers("header2", "headerValue2")
                .params("file", new File("/storage/emulated/0/Jeffrey/pic/史努比.jpg"))
                //.params("file", new File("/storage/emulated/0/Jeffrey/pic/WALL.E.jpg"))
                //.addFileParams("file", files)
                .execute(new JsonCallback<BaseResponse<Map>>() {
                    @Override
                    public boolean onBefore(BaseRequest request) {
                        Snackbar.make(rootContent, "正在上传中...", Snackbar.LENGTH_INDEFINITE) .show();
                        return super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(BaseResponse<Map> responseData, Call call, Response response) {
                        handleResponse(responseData.data, call, response);
                        Snackbar.make(rootContent, "上传完成", Snackbar.LENGTH_SHORT) .show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                        Snackbar.make(rootContent, "上传出错", Snackbar.LENGTH_SHORT) .show();
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        System.out.println("upProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);

                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), totalSize);
                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String netSpeed = Formatter.formatFileSize(getApplicationContext(), networkSpeed);
                        tvNetSpeed.setText(netSpeed + "/S");
                        tvProgress.setText((Math.round(progress * 10000) * 1.0f / 100) + "%");
                        pbProgress.setMax(100);
                        pbProgress.setProgress((int) (progress * 100));
                    }
                });
    }
}