package org.zywx.wbpalmstar.plugin.uexhtm.model;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable, IMessageCode {

    private static final long serialVersionUID = 5213230387175987834L;

    public String code;

    public T data;
}