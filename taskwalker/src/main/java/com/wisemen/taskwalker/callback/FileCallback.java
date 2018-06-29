package com.wisemen.taskwalker.callback;

import com.wisemen.taskwalker.convert.FileConvert;

import java.io.File;

import okhttp3.Response;

public abstract class FileCallback extends AbsCallback<File> {

    private FileConvert convert;

    public FileCallback() {
        this(null);
    }

    public FileCallback(String destFullFileName) {
//        this(null, destFileName);

        int index = destFullFileName.lastIndexOf(File.separator);
        if (index > 0) {
            convert = new FileConvert(destFullFileName.substring(0, index + 1),
                    destFullFileName.substring(index + 1));
            convert.setCallback(this);
        }
    }

    public FileCallback(String destFileDir, String destFileName) {
        convert = new FileConvert(destFileDir, destFileName);
        convert.setCallback(this);
    }

    @Override
    public File convertSuccess(Response response) throws Exception {
        File file = convert.convertSuccess(response);
        response.close();
        return file;
    }
}