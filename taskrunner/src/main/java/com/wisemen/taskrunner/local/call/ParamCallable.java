package com.wisemen.taskrunner.local.call;


import java.util.HashMap;
import java.util.Map;

public abstract class ParamCallable<T,P> implements Callable<T> {
    protected Map<String, P> params = new HashMap<>();

    public Map<String, P> getParams() {
        return params;
    }

    public void setParams(Map<String, P> params) {
        this.params = params;
    }
}
