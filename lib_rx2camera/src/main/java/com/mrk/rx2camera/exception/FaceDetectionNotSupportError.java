package com.mrk.rx2camera.exception;

/**
 * Created by mrk on 2017/12.
 */
public class FaceDetectionNotSupportError extends Exception {

    public FaceDetectionNotSupportError(String detailMessage) {
        super(detailMessage);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
