package com.mrk.rx2camera.exception;

/**
 * Created by mrk on 2017/12.
 */
public class ZoomFailedException extends Exception {

    public enum Reason {
        ZOOM_NOT_SUPPORT,
        ZOOM_RANGE_ERROR
    }

    private Reason reason;

    public ZoomFailedException(Reason reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("Zoom failed: %s", reason.toString());
    }
}
