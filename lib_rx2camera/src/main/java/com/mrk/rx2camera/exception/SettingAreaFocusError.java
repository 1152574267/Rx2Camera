package com.mrk.rx2camera.exception;

/**
 * Created by mrk on 2017/12.
 */
public class SettingAreaFocusError extends Exception {

    public enum Reason {
        NOT_SUPPORT,
        SET_AREA_FOCUS_FAILED
    }

    private Reason reason;

    public SettingAreaFocusError(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }
}
