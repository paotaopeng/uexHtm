package com.wisemen.demo.taskwalker;

import android.os.Bundle;
import android.view.View;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseDetailActivity;
import com.wisemen.demo.callback.DialogCallback;
import com.wisemen.demo.model.BaseResponse;
import com.wisemen.demo.utils.Urls;
import com.wisemen.taskwalker.TaskWalker;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class JsonRequestActivity extends BaseDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_custom_request);
        ButterKnife.bind(this);
        actionBar.setTitle("自动解析Json对象");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        TaskWalker.getInstance().cancelTag(this);
    }

    /**
     * 解析javabean对象
     */
    @OnClick(R.id.requestJson)
    public void requestJson(View view) {
        TaskWalker.get(Urls.URL_JSONOBJECT)
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

    /**
     * 解析集合对象
     */
    @OnClick(R.id.requestJsonArray)
    public void requestJsonArray(View view) {
        TaskWalker.get(Urls.URL_JSONARRAY)
                .tag(this)
                .headers("header1", "headerValue1")
                .params("param1", "paramValue1")
                .execute(new DialogCallback<BaseResponse<List<Map>>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<List<Map>> responseData, Call call, Response response) {
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
