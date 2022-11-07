package com.xxdai.starter.cache.model;

/**
 * 配合 IdempotenceStatusResolver 使用
 * Created by fangdajiang on 2017/6/15.
 */
public enum RequestStatus {
    INITIATED, FINISHED;
    public boolean isFinished() {
        switch (this) {
            case INITIATED: return false;
            case FINISHED: return true;
        }
        throw new AssertionError("Unknown op:" + this);
    }
}
