package com.mrk.rx2camera.exception;

/**
 * Created by mrk on 2017/12.
 */
public class SettingMeterAreaError extends Exception {

    public enum Reason {
        NOT_SUPPORT,
    }

    private Reason reason;

    public SettingMeterAreaError(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }
}
