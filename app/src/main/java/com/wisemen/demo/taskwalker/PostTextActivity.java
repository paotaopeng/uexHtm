package com.wisemen.demo.taskwalker;

import android.os.Bundle;
import android.view.View;

import com.wisemen.demo.R;
import com.wisemen.demo.base.BaseDetailActivity;
import com.wisemen.demo.callback.DialogCallback;
import com.wisemen.demo.model.BaseResponse;
import com.wisemen.demo.utils.Urls;
import com.wisemen.taskwalker.TaskWalker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class PostTextActivity extends BaseDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_post_text);
        ButterKnife.bind(this);
        setTitle("上传大文本Json数据");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        TaskWalker.getInstance().cancelTag(this);
    }

    @OnClick(R.id.postJson)
    public void postJson(View view) {

        HashMap<String, String> params = new HashMap<>();
        params.put("key1", "数据1");
        params.put("key2", "数据2");
        params.put("key3", "数据3");
        params.put("key4", "数据4");
        JSONObject jsonObject = new JSONObject(params);

        TaskWalker.post(Urls.URL_TEXT_UPLOAD)
                .tag(this)
                .headers("header1", "headerValue1")
                .upJson(jsonObject)
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

    @OnClick(R.id.postString)
    public void postString(View view) {
        TaskWalker.post(Urls.URL_TEXT_UPLOAD)
                .tag(this)
                .headers("header1", "headerValue1")
                .upString("长文本测试数据")//
                //.upString("", MediaType.parse("application/xml"))
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

    @OnClick(R.id.postBytes)
    public void postBytes(View view) {
        TaskWalker.post(Urls.URL_TEXT_UPLOAD)
                .tag(this)
                .headers("header1", "headerValue1")
                .upBytes("字节数据".getBytes())
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