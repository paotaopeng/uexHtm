package com.wisemen.demo.taskwalker;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseDetailActivity;
import com.wisemen.demo.ui.NumberProgressBar;
import com.wisemen.demo.utils.Urls;
import com.wisemen.taskwalker.TaskWalker;
import com.wisemen.taskwalker.callback.FileCallback;
import com.wisemen.taskwalker.request.BaseRequest;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class FileDownloadActivity extends BaseDetailActivity {

    @Bind(R.id.fileDownload) Button btnFileDownload;
    @Bind(R.id.downloadSize) TextView tvDownloadSize;
    @Bind(R.id.tvProgress) TextView tvProgress;
    @Bind(R.id.netSpeed) TextView tvNetSpeed;
    @Bind(R.id.pbProgress) NumberProgressBar pbProgress;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_file_download);
        ButterKnife.bind(this);
        setTitle("文件下载");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        TaskWalker.getInstance().cancelTag(this);
    }

    @OnClick(R.id.fileDownload)
    public void fileDownload(View view) {
        String destFullFileName = new StringBuilder().append(Environment.getExternalStorageDirectory())
                .append(File.separator).append("download").append(File.separator).append("test.zip")
                .toString();

        TaskWalker.get(Urls.URL_DOWNLOAD)
                .tag(this)
                .headers("header1", "headerValue1")
                .params("param1", "paramValue1")
                .execute(new FileCallback(destFullFileName) {
                    @Override
                    public boolean onBefore(BaseRequest request) {
                        btnFileDownload.setText("正在下载中");
                        return super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        handleResponse(file, call, response);
                        btnFileDownload.setText("下载完成");
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        System.out.println("downloadProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);

                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), totalSize);
                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String netSpeed = Formatter.formatFileSize(getApplicationContext(), networkSpeed);
                        tvNetSpeed.setText(netSpeed + "/S");
                        tvProgress.setText((Math.round(progress * 10000) * 1.0f / 100) + "%");
                        pbProgress.setMax(100);
                        pbProgress.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onError(Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                        btnFileDownload.setText("下载出错");
                    }
                });
    }

    @OnClick(R.id.cancel)
    public void cancel(View view) {
        TaskWalker.getInstance().cancelTag(this);
    }
}