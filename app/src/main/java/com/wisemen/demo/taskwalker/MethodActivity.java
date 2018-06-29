package com.wisemen.demo.taskwalker;

import android.os.Bundle;
import android.view.View;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseDetailActivity;
import com.wisemen.demo.callback.DialogCallback;
import com.wisemen.demo.model.BaseResponse;
import com.wisemen.demo.utils.Urls;
import com.wisemen.taskwalker.TaskWalker;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class MethodActivity extends BaseDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_method);
        ButterKnife.bind(this);
        setTitle("请求方法演示");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        TaskWalker.getInstance().cancelTag(this);
    }

    @OnClick(R.id.get)
    public void get(View view) {
        TaskWalker.get(Urls.URL_METHOD)
                .tag(this)
                .headers("header1", "headerValue1")
                .params("param1", "paramValue1")
                .execute(new DialogCallback<BaseResponse<Map>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<Map> responseData, Call call, Response response) {
                        handleResponse(responseData.data, call, response);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                    }
                });
    }

    @OnClick(R.id.post)
    public void post(View view) {
        TaskWalker.post(Urls.URL_METHOD)
                .tag(this)
                .headers("header1", "headerValue1")
                .params("param1", "paramValue1")
                .params("param2", "paramValue2")
                .params("param3", "paramValue3")
                //.isMultipart(true)         //强制使用 multipart/form-data 表单上传
                .execute(new DialogCallback<BaseResponse<Map>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<Map> responseData, Call call, Response response) {
                        handleResponse(responseData.data, call, response);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                    }
                });
    }

}