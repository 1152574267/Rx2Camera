package com.mrk.rx2camera.callback;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.TextureView;

/**
 * Created by mrk on 2017/12.
 */
public class SurfaceCallback implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {
    private SurfaceListener listener;

    public interface SurfaceListener {
        void onAvailable();

        void onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        onSurfaceAvailable();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onSurfaceDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        onSurfaceAvailable();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        onSurfaceDestroy();

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void onSurfaceAvailable() {
        if (listener != null) {
            listener.onAvailable();
        }
    }


    public void onSurfaceDestroy() {
        if (listener != null) {
            listener.onDestroy();
        }
    }

    public void setSurfaceListener(SurfaceListener l) {
        this.listener = l;
    }
}
