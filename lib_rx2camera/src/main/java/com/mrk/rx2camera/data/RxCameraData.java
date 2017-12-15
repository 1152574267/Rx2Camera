package com.mrk.rx2camera.data;

import android.graphics.Matrix;
import android.hardware.Camera;

/**
 * Created by mrk on 2017/12.
 * the preview frame data
 */
public class RxCameraData {
    /**
     * the raw preview frame, the format is in YUV if you not set the
     * preview format in the config, it will null on face detect request
     */
    public byte[] cameraData;

    /**
     * a matrix help you rotate the camera data in portrait mode,
     * it will null on face detect request
     */
    public Matrix rotateMatrix;

    /**
     * the face detector return's face list, only has values if you
     * request face detection
     */
    public Camera.Face[] faceList;
}
