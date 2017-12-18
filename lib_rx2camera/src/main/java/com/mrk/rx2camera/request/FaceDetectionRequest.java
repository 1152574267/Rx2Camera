package com.mrk.rx2camera.request;

import android.hardware.Camera;

import com.mrk.rx2camera.base.RxCamera;
import com.mrk.rx2camera.data.RxCameraData;
import com.mrk.rx2camera.exception.FaceDetectionNotSupportError;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by mrk on 2017/12.
 */
public class FaceDetectionRequest extends BaseRxCameraRequest implements Camera.FaceDetectionListener {
    private ObservableEmitter<RxCameraData> emitter;

    public FaceDetectionRequest(RxCamera rxCamera) {
        super(rxCamera);
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new ObservableOnSubscribe<RxCameraData>() {

            @Override
            public void subscribe(ObservableEmitter<RxCameraData> emitter) throws Exception {
                if (rxCamera.getNativeCamera().getParameters().getMaxNumDetectedFaces() > 0) {
                    FaceDetectionRequest.this.emitter = emitter;
                } else {
                    emitter.onError(new FaceDetectionNotSupportError("Camera not support face detection"));
                }
            }
        }).doOnSubscribe(new Consumer<Disposable>() {

            @Override
            public void accept(Disposable disposable) throws Exception {
                rxCamera.getNativeCamera().setFaceDetectionListener(FaceDetectionRequest.this);
                rxCamera.getNativeCamera().startFaceDetection();
            }
        }).doOnDispose(new Action() {

            @Override
            public void run() throws Exception {
                rxCamera.getNativeCamera().setFaceDetectionListener(null);
                rxCamera.getNativeCamera().stopFaceDetection();
            }
        });
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (emitter != null && !emitter.isDisposed() && rxCamera.isOpenCamera()) {
            RxCameraData cameraData = new RxCameraData();
            cameraData.faceList = faces;
            emitter.onNext(cameraData);
        }
    }
}
