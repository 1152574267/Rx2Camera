package com.mrk.rx2camera.request;

import com.mrk.rx2camera.base.RxCamera;
import com.mrk.rx2camera.data.RxCameraData;

import io.reactivex.Observable;

/**
 * Created by mrk on 2017/12.
 */
public abstract class BaseRxCameraRequest {
    protected RxCamera rxCamera;

    public BaseRxCameraRequest(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    public abstract Observable<RxCameraData> get();
}
