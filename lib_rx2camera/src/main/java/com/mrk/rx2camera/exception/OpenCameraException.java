package com.mrk.rx2camera.exception;

/**
 * Created by mrk on 2017/12.
 * throw this exception if open camera failed
 */
public class OpenCameraException extends Exception {
    private OpenCameraFailedReason reason;
    private Throwable cause;

    public OpenCameraException(OpenCameraFailedReason reason, Throwable cause) {
        this.reason = reason;
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    public OpenCameraFailedReason getReason() {
        return reason;
    }

    @Override
    public String getMessage() {
        return String.format("Open camera failed: %s, cause: %s", reason, cause);
    }
}
