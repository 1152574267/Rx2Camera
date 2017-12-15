package com.mrk.rx2camera.request;

import com.mrk.rx2camera.base.RxCamera;
import com.mrk.rx2camera.callback.OnRxCameraPreviewFrameCallback;
import com.mrk.rx2camera.data.RxCameraData;
import com.mrk.rx2camera.exception.CameraDataNullException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by mrk on 2017/12.
 */
public class PeriodicDataRequest extends BaseRxCameraRequest implements OnRxCameraPreviewFrameCallback {
    private static final String TAG = PeriodicDataRequest.class.getSimpleName();

    private long intervalMills;
    private long lastSendDataTimestamp = 0;
    private boolean isInstallCallback = false;

    private ObservableEmitter<RxCameraData> emitter;

    private RxCameraData currentData = new RxCameraData();

    public PeriodicDataRequest(RxCamera rxCamera, long intervalMills) {
        super(rxCamera);
        this.intervalMills = intervalMills;
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new ObservableOnSubscribe<RxCameraData>() {

            @Override
            public void subscribe(ObservableEmitter<RxCameraData> emitter) throws Exception {
                PeriodicDataRequest.this.emitter = emitter;

//                subscriber.add(Schedulers.newThread().createWorker().schedulePeriodically(new Action0() {
//                    @Override
//                    public void call() {
//                        if (currentData.cameraData != null && !subscriber.isUnsubscribed() && rxCamera.isOpenCamera()) {
//                            subscriber.onNext(currentData);
//                        }
//                    }
//                }, 0, intervalMills, TimeUnit.MILLISECONDS));
            }
        }).doOnDispose(new Action() {

            @Override
            public void run() throws Exception {
                rxCamera.uninstallPreviewCallback(PeriodicDataRequest.this);
                isInstallCallback = false;
            }
        }).doOnSubscribe(new Consumer<Disposable>() {

            @Override
            public void accept(Disposable disposable) throws Exception {
                if (!isInstallCallback) {
                    rxCamera.installPreviewCallback(PeriodicDataRequest.this);
                    isInstallCallback = true;
                }
            }
        }).doOnTerminate(new Action() {
            @Override
            public void run() throws Exception {
                rxCamera.uninstallPreviewCallback(PeriodicDataRequest.this);
                isInstallCallback = false;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (emitter != null && !emitter.isDisposed() && rxCamera.isOpenCamera()) {
            if (data == null || data.length == 0) {
                emitter.onError(new CameraDataNullException());
            }
            currentData.cameraData = data;
            currentData.rotateMatrix = rxCamera.getRotateMatrix();
        }
    }
}
