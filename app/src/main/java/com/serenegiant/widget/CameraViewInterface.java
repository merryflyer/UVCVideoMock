package com.serenegiant.widget;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import com.serenegiant.encoder.IVideoEncoder;

public interface CameraViewInterface extends IAspectRatioView {

    public interface Callback {
        void onSurfaceChanged(CameraViewInterface cameraViewInterface, Surface surface, int i, int i2);

        void onSurfaceCreated(CameraViewInterface cameraViewInterface, Surface surface);

        void onSurfaceDestroy(CameraViewInterface cameraViewInterface, Surface surface);
    }

    Bitmap captureStillImage();

    Surface getSurface();

    SurfaceTexture getSurfaceTexture();

    boolean hasSurface();

    void onPause();

    void onResume();

    void setCallback(Callback callback);

    void setPictureSize(int i, int i2);

    void setVideoEncoder(IVideoEncoder iVideoEncoder);
}
