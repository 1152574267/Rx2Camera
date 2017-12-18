package com.mrk.rx2camera.action;

import android.hardware.Camera;

import com.mrk.rx2camera.base.RxCamera;
import com.mrk.rx2camera.exception.SettingAreaFocusError;
import com.mrk.rx2camera.exception.SettingFlashException;
import com.mrk.rx2camera.exception.SettingMeterAreaError;
import com.mrk.rx2camera.exception.ZoomFailedException;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by ragnarok on 16/1/9.
 */
public class RxCameraActionBuilder {
    private RxCamera rxCamera;

    public RxCameraActionBuilder(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    /**
     * set the zoom level of the camera
     *
     * @param level
     * @return
     */
    public Observable<RxCamera> zoom(final int level) {
        return Observable.create(new ObservableOnSubscribe<RxCamera>() {

            @Override
            public void subscribe(ObservableEmitter<RxCamera> emitter) throws Exception {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (!parameters.isZoomSupported()) {
                    emitter.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_NOT_SUPPORT));
                    return;
                }

                int maxZoomLevel = parameters.getMaxZoom();
                if (level < 0 || level > maxZoomLevel) {
                    emitter.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_RANGE_ERROR));
                    return;
                }

                parameters.setZoom(level);
                rxCamera.getNativeCamera().setParameters(parameters);
                emitter.onNext(rxCamera);
            }
        });
    }

    /**
     * smooth zoom the camera, which will gradually change the preview content
     *
     * @param level
     * @return
     */
    public Observable<RxCamera> smoothZoom(final int level) {
        return Observable.create(new ObservableOnSubscribe<RxCamera>() {

            @Override
            public void subscribe(ObservableEmitter<RxCamera> emitter) throws Exception {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (!parameters.isZoomSupported() || !parameters.isSmoothZoomSupported()) {
                    emitter.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_NOT_SUPPORT));
                    return;
                }

                int maxZoomLevel = parameters.getMaxZoom();
                if (level < 0 || level > maxZoomLevel) {
                    emitter.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_RANGE_ERROR));
                    return;
                }

                rxCamera.getNativeCamera().startSmoothZoom(level);
                emitter.onNext(rxCamera);
            }
        });
    }

    public Observable<RxCamera> flashAction(final boolean isOn) {
        return Observable.create(new ObservableOnSubscribe<RxCamera>() {

            @Override
            public void subscribe(ObservableEmitter<RxCamera> emitter) throws Exception {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (parameters.getSupportedFlashModes() == null || parameters.getSupportedFlashModes().size() <= 0) {
                    emitter.onError(new SettingFlashException(SettingFlashException.Reason.NOT_SUPPORT));
                    return;
                }

                if (isOn) {
                    if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        rxCamera.getNativeCamera().setParameters(parameters);
                        emitter.onNext(rxCamera);
                        return;
                    } else {
                        emitter.onError(new SettingFlashException(SettingFlashException.Reason.NOT_SUPPORT));
                    }
                } else {
                    if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_OFF)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        rxCamera.getNativeCamera().setParameters(parameters);
                        emitter.onNext(rxCamera);
                    } else {
                        emitter.onError(new SettingFlashException(SettingFlashException.Reason.NOT_SUPPORT));
                    }
                }
            }
        });
    }

    public Observable<RxCamera> areaFocusAction(final List<Camera.Area> focusAreaList) {
        if (focusAreaList == null || focusAreaList.size() == 0) {
            return null;
        }

        return Observable.create(new ObservableOnSubscribe<RxCamera>() {

            @Override
            public void subscribe(final ObservableEmitter<RxCamera> emitter) throws Exception {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (parameters.getMaxNumFocusAreas() < focusAreaList.size()) {
                    emitter.onError(new SettingAreaFocusError(SettingAreaFocusError.Reason.NOT_SUPPORT));
                } else {
                    if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
                        List<String> focusModes = parameters.getSupportedFocusModes();
                        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        }
                    }
                    parameters.setFocusAreas(focusAreaList);
                    rxCamera.getNativeCamera().setParameters(parameters);
                    rxCamera.getNativeCamera().autoFocus(new Camera.AutoFocusCallback() {

                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                emitter.onNext(rxCamera);
                            } else {
                                emitter.onError(new SettingAreaFocusError(SettingAreaFocusError.Reason.SET_AREA_FOCUS_FAILED));
                            }
                        }
                    });
                }
            }
        });
    }

    public Observable<RxCamera> areaMeterAction(final List<Camera.Area> meterAreaList) {
        if (meterAreaList == null || meterAreaList.size() == 0) {
            return null;
        }

        return Observable.create(new ObservableOnSubscribe<RxCamera>() {

            @Override
            public void subscribe(final ObservableEmitter<RxCamera> emitter) throws Exception {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (parameters.getMaxNumMeteringAreas() < meterAreaList.size()) {
                    emitter.onError(new SettingMeterAreaError(SettingMeterAreaError.Reason.NOT_SUPPORT));
                } else {
                    parameters.setFocusAreas(meterAreaList);
                    rxCamera.getNativeCamera().setParameters(parameters);
                    emitter.onNext(rxCamera);
                }
            }
        });
    }
}
