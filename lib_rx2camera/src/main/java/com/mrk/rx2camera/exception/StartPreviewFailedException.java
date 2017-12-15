package com.mrk.rx2camera.exception;

/**
 * Created by mrk on 2017/12.
 */
public class StartPreviewFailedException extends Exception {

    public StartPreviewFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cause: " + getCause();
    }
}
