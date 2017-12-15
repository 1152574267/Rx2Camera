package com.mrk.rx2camera.request;

import com.mrk.rx2camera.base.RxCamera;
import com.mrk.rx2camera.callback.OnRxCameraPreviewFrameCallback;
import com.mrk.rx2camera.data.RxCameraData;
import com.mrk.rx2camera.exception.CameraDataNullException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by mrk on 2017/12.
 */
public class TakeOneShotRequest extends BaseRxCameraRequest implements OnRxCameraPreviewFrameCallback {
    private ObservableEmitter<RxCameraData> emitter;

    public TakeOneShotRequest(RxCamera rxCamera) {
        super(rxCamera);
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new ObservableOnSubscribe<RxCameraData>() {

            @Override
            public void subscribe(ObservableEmitter<RxCameraData> emitter) throws Exception {
                TakeOneShotRequest.this.emitter = emitter;
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                rxCamera.installOneShotPreviewCallback(TakeOneShotRequest.this);
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (emitter != null && !emitter.isDisposed() && rxCamera.isOpenCamera()) {
            if (data == null || data.length == 0) {
                emitter.onError(new CameraDataNullException());
            }
            RxCameraData rxCameraData = new RxCameraData();
            rxCameraData.cameraData = data;
            rxCameraData.rotateMatrix = rxCamera.getRotateMatrix();
            emitter.onNext(rxCameraData);
        }
    }
}
