package com.mrk.rx2camera.request;

import com.mrk.rx2camera.base.RxCamera;
import com.mrk.rx2camera.callback.OnRxCameraPreviewFrameCallback;
import com.mrk.rx2camera.data.RxCameraData;
import com.mrk.rx2camera.exception.CameraDataNullException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by mrk on 2017/12.
 */
public class SuccessiveDataRequest extends BaseRxCameraRequest implements OnRxCameraPreviewFrameCallback {
    private boolean isInstallSuccessivePreviewCallback = false;

    private ObservableEmitter<RxCameraData> emitter;

    public SuccessiveDataRequest(RxCamera rxCamera) {
        super(rxCamera);
    }

    public Observable<RxCameraData> get() {
        return Observable.create(new ObservableOnSubscribe<RxCameraData>() {

            @Override
            public void subscribe(ObservableEmitter<RxCameraData> emitter) throws Exception {
                SuccessiveDataRequest.this.emitter = emitter;
            }
        }).doOnDispose(new Action() {

            @Override
            public void run() throws Exception {
                rxCamera.uninstallPreviewCallback(SuccessiveDataRequest.this);
                isInstallSuccessivePreviewCallback = false;
            }
        }).doOnSubscribe(new Consumer<Disposable>() {

            @Override
            public void accept(Disposable disposable) throws Exception {
                if (!isInstallSuccessivePreviewCallback) {
                    rxCamera.installPreviewCallback(SuccessiveDataRequest.this);
                    isInstallSuccessivePreviewCallback = true;
                }
            }
        }).doOnTerminate(new Action() {

            @Override
            public void run() throws Exception {
                rxCamera.uninstallPreviewCallback(SuccessiveDataRequest.this);
                isInstallSuccessivePreviewCallback = false;
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (emitter != null && !emitter.isDisposed() && rxCamera.isOpenCamera()) {
            if (data == null || data.length == 0) {
                emitter.onError(new CameraDataNullException());
            }
            RxCameraData cameraData = new RxCameraData();
            cameraData.cameraData = data;
            cameraData.rotateMatrix = rxCamera.getRotateMatrix();
            emitter.onNext(cameraData);
        }
    }
}
