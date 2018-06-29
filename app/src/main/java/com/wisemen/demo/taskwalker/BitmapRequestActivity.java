package com.wisemen.demo.taskwalker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseDetailActivity;
import com.wisemen.demo.callback.BitmapDialogCallback;
import com.wisemen.demo.utils.Urls;
import com.wisemen.taskwalker.TaskWalker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class BitmapRequestActivity extends BaseDetailActivity {

    @Bind(R.id.imageView) ImageView imageView;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bitmap_request);
        ButterKnife.bind(this);
        setTitle("请求图片");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        TaskWalker.getInstance().cancelTag(this);
    }

    @OnClick(R.id.requestImage)
    public void requestJson(View view) {
        TaskWalker.get(Urls.URL_IMAGE)
                .tag(this)
                .headers("header1", "headerValue1")
                .params("param1", "paramValue1")
                .execute(new BitmapDialogCallback(this) {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        handleResponse(bitmap, call, response);
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                    }
                });
    }
}
