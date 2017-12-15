package com.mrk.rx2camera.exception;

/**
 * Created by mrk on 2017/12.
 */
public class BindSurfaceFailedException extends Exception {
    public BindSurfaceFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cause: " + getCause();
    }
}
