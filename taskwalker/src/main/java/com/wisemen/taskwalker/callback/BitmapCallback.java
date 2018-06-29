package com.wisemen.taskwalker.callback;

import android.graphics.Bitmap;

import com.wisemen.taskwalker.convert.BitmapConvert;

import okhttp3.Response;

/**
 * 返回图片的Bitmap
 */
public abstract class BitmapCallback extends AbsCallback<Bitmap> {

    @Override
    public Bitmap convertSuccess(Response response) throws Exception {
        //这里没有进行图片的缩放，可能会发生OOM
        Bitmap bitmap = BitmapConvert.create().convertSuccess(response);
        response.close();
        return bitmap;
    }
}