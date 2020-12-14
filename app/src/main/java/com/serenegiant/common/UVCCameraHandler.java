package com.serenegiant.common;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.os.Message;
import com.serenegiant.common.AbstractUVCCameraHandler.OnPreViewResultListener;
import com.serenegiant.common.AbstractUVCCameraHandler.OnScanCompletedListener;
import com.serenegiant.common.VideoFileSizeObserver.OnRerecordListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.widget.CameraViewInterface;
import java.util.List;

public class UVCCameraHandler extends AbstractUVCCameraHandler {
    protected UVCCameraHandler(CameraThread cameraThread) {
        super(cameraThread);
    }

    public static final UVCCameraHandler createHandler(Activity activity, CameraViewInterface cameraViewInterface, int i, int i2) {
        return createHandler(activity, cameraViewInterface, 1, i, i2, 1, 1.0f);
    }

    public void captureStill() {
        super.captureStill();
    }

    public /* bridge */ /* synthetic */ boolean checkSupportFlag(long j) {
        return super.checkSupportFlag(j);
    }

    public /* bridge */ /* synthetic */ void close() {
        super.close();
    }

    public /* bridge */ /* synthetic */ int getHeight() {
        return super.getHeight();
    }

    public /* bridge */ /* synthetic */ List getSupportedPreviewSizes() {
        return super.getSupportedPreviewSizes();
    }

    public /* bridge */ /* synthetic */ UVCCamera getUVCCamera() {
        return super.getUVCCamera();
    }

    public /* bridge */ /* synthetic */ int getValue(int i) {
        return super.getValue(i);
    }

    public /* bridge */ /* synthetic */ int getWidth() {
        return super.getWidth();
    }

    public /* bridge */ /* synthetic */ void handleMessage(Message message) {
        super.handleMessage(message);
    }

    public /* bridge */ /* synthetic */ boolean isEqual(UsbDevice usbDevice) {
        return super.isEqual(usbDevice);
    }

    public /* bridge */ /* synthetic */ boolean isOpened() {
        return super.isOpened();
    }

    public /* bridge */ /* synthetic */ boolean isPreviewing() {
        return super.isPreviewing();
    }

    public /* bridge */ /* synthetic */ boolean isRecording() {
        return super.isRecording();
    }

    public /* bridge */ /* synthetic */ void open(UsbControlBlock usbControlBlock) {
        super.open(usbControlBlock);
    }

    public /* bridge */ /* synthetic */ void release() {
        super.release();
    }

    public /* bridge */ /* synthetic */ int resetValue(int i) {
        return super.resetValue(i);
    }

    public /* bridge */ /* synthetic */ void resize(int i, int i2) {
        super.resize(i, i2);
        throw null;
    }

    public /* bridge */ /* synthetic */ void setOnPreViewResultListener(OnPreViewResultListener onPreViewResultListener) {
        super.setOnPreViewResultListener(onPreViewResultListener);
    }

    public /* bridge */ /* synthetic */ void setOnRerecordListener(OnRerecordListener onRerecordListener) {
        super.setOnRerecordListener(onRerecordListener);
    }

    public /* bridge */ /* synthetic */ void setOnScanCompletedListener(OnScanCompletedListener onScanCompletedListener) {
        super.setOnScanCompletedListener(onScanCompletedListener);
    }

    public void setPreviewSize(Object obj, int i, int i2) {
        super.setPreviewSize(obj, i, i2);
    }

    public /* bridge */ /* synthetic */ int setValue(int i, int i2) {
        return super.setValue(i, i2);
    }

    public void startPreview(Object obj) {
        super.startPreview(obj);
    }

    public /* bridge */ /* synthetic */ void startRecording(int i) {
        super.startRecording(i);
    }

    public /* bridge */ /* synthetic */ void stopPreview() {
        super.stopPreview();
    }

    public /* bridge */ /* synthetic */ void stopRecording() {
        super.stopRecording();
    }

    public static final UVCCameraHandler createHandler(Activity activity, CameraViewInterface cameraViewInterface, int i, int i2, float f) {
        return createHandler(activity, cameraViewInterface, 1, i, i2, 1, f);
    }

    public void captureStill(int i) {
        super.captureStill(i);
    }

    public static final UVCCameraHandler createHandler(Activity activity, CameraViewInterface cameraViewInterface, int i, int i2, int i3) {
        return createHandler(activity, cameraViewInterface, i, i2, i3, 1, 1.0f);
    }

    public static final UVCCameraHandler createHandler(Activity activity, CameraViewInterface cameraViewInterface, int i, int i2, int i3, int i4) {
        return createHandler(activity, cameraViewInterface, i, i2, i3, i4, 1.0f);
    }

    public static final UVCCameraHandler createHandler(Activity activity, CameraViewInterface cameraViewInterface, int i, int i2, int i3, int i4, float f) {
        CameraThread cameraThread = new CameraThread(UVCCameraHandler.class, activity, cameraViewInterface, i, i2, i3, i4, f);
        cameraThread.start();
        return (UVCCameraHandler) cameraThread.getHandler();
    }
}
