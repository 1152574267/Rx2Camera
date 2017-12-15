package com.mrk.rx2camera.request;

import android.hardware.Camera;

import com.mrk.rx2camera.base.RxCamera;
import com.mrk.rx2camera.data.RxCameraData;
import com.mrk.rx2camera.exception.TakePictureFailedException;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

/**
 * Created by mrk on 2017/12.
 */
public class TakePictureRequest extends BaseRxCameraRequest {
    private Func shutterAction;

    private boolean isContinuePreview;
    private boolean openFlash = false;
    private int pictureFormat = -1;
    private int pictureWidth = -1;
    private int pictureHeight = -1;

    public TakePictureRequest(RxCamera rxCamera, Func shutterAction, boolean isContinuePreview) {
        this(rxCamera, shutterAction, isContinuePreview, false);
    }

    public TakePictureRequest(RxCamera rxCamera, Func shutterAction, boolean isContinuePreview, boolean openFlash) {
        this(rxCamera, shutterAction, isContinuePreview, -1, -1, -1, openFlash);
    }

    public TakePictureRequest(RxCamera rxCamera, Func shutterAction, boolean isContinuePreview, int width, int height, int format, boolean openFlash) {
        super(rxCamera);

        this.shutterAction = shutterAction;
        this.isContinuePreview = isContinuePreview;
        this.pictureWidth = width;
        this.pictureHeight = height;
        this.pictureFormat = format;
        this.openFlash = openFlash;
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new ObservableOnSubscribe<RxCameraData>() {

            @Override
            public void subscribe(final ObservableEmitter<RxCameraData> emitter) throws Exception {
                try {
                    Camera.Parameters param = rxCamera.getNativeCamera().getParameters();

                    // set the picture format
                    if (pictureFormat != -1) {
                        param.setPictureFormat(pictureFormat);
                    }

                    // set the picture size
                    if (pictureWidth != -1 && pictureHeight != -1) {
                        Camera.Size size = findClosetPictureSize(param.getSupportedPictureSizes(), pictureWidth, pictureHeight);
                        if (size != null) {
                            param.setPictureSize(size.width, size.height);
                        }
                    }

                    if (openFlash) {
                        if (param.getSupportedFlashModes() != null &&
                                param.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_ON)) {
                            param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        }
                    }

                    rxCamera.getNativeCamera().setParameters(param);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                rxCamera.getNativeCamera().takePicture(new Camera.ShutterCallback() {

                    @Override
                    public void onShutter() {
                        if (shutterAction != null) {
                            shutterAction.call();
                        }
                    }
                }, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {

                    }
                }, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (isContinuePreview) {
                            rxCamera.startPreview().doOnError(new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    emitter.onError(throwable);
                                }
                            }).subscribe();
                        }

                        if (data != null) {
                            RxCameraData rxCameraData = new RxCameraData();
                            rxCameraData.cameraData = data;
                            rxCameraData.rotateMatrix = rxCamera.getRotateMatrix();
                            emitter.onNext(rxCameraData);

                            // should close flash
                            if (openFlash) {
                                Camera.Parameters param = rxCamera.getNativeCamera().getParameters();
                                if (param.getSupportedFlashModes() != null &&
                                        param.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_OFF)) {
                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                }
                                rxCamera.getNativeCamera().setParameters(param);
                            }
                        } else {
                            emitter.onError(new TakePictureFailedException("cannot get take picture data"));
                        }
                    }
                });
            }
        });
    }

    private Camera.Size findClosetPictureSize(List<Camera.Size> sizeList, int width, int height) {
        if (sizeList == null || sizeList.size() <= 0) {
            return null;
        }

        int minDiff = Integer.MAX_VALUE;
        Camera.Size bestSize = null;
        for (Camera.Size size : sizeList) {
            int diff = Math.abs(size.width - width) + Math.abs(size.height - height);
            if (diff < minDiff) {
                minDiff = diff;
                bestSize = size;
            }
        }

        return bestSize;
    }
}
