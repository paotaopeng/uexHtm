package com.wisemen.taskrunner.local.call;


public interface Callable<T> {
    T call() throws Exception;
}