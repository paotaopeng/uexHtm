package org.zywx.wbpalmstar.plugin.uexhtm.model;

import com.google.gson.stream.JsonReader;
import com.wisemen.taskwalker.callback.AbsCallback;
import com.wisemen.taskwalker.request.BaseRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

public abstract class JsonCallback<T> extends AbsCallback<T> {

    @Override
    public boolean onBefore(BaseRequest request) {
        return super.onBefore(request);
    }

    /**
     * 解析网络返回的 response 对象，在子线程中运行
     */
    @Override
    public T convertSuccess(Response response) throws Exception {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        //目前只有一个泛型
        Type type = params[0];

        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        Type rawType = ((ParameterizedType) type).getRawType();

        //根据泛型解析数据，返回的对象自动以参数的形式传递到 onSuccess 中
        JsonReader jsonReader = new JsonReader(response.body().charStream());
        if (rawType == Void.class) {
            //无数据类型,表示没有data数据的情况
            BaseResponse baseResponse = Convert.fromJson(jsonReader, BaseResponse.class);
            response.close();
            //noinspection unchecked
            return (T) baseResponse;
        } else if (rawType == BaseResponse.class) {
            //有数据类型，表示有data的情况
            BaseResponse baseResponse = Convert.fromJson(jsonReader, type);
            response.close();
            String code = baseResponse.code;

            if (IMessageCode.MSG_SUCCESS.equals(code)) {
                //noinspection unchecked
                return (T) baseResponse;
            } else if (IMessageCode.MSG_UPLOAD_FILE_EMPTY.equals(code)) {
                throw new IllegalStateException("上传的文件内容为空");
            } else if (IMessageCode.MSG_UPLOAD_FILE_FAILED.equals(code)) {
                throw new IllegalStateException("文件上传失败");
            } else {
                throw new IllegalStateException("操作失败。错误代码：" + code);
            }
        } else {
            response.close();
            throw new IllegalStateException("基类错误无法解析!");
        }
    }
}