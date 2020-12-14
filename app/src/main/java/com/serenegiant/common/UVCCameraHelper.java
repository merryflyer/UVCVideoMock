package com.serenegiant.common;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import com.android.nightvision.R;
import com.serenegiant.common.AbstractUVCCameraHandler.OnPreViewResultListener;
import com.serenegiant.common.AbstractUVCCameraHandler.OnScanCompletedListener;
import com.serenegiant.common.VideoFileSizeObserver.OnRerecordListener;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.LogUtil;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.widget.CameraViewInterface;
import com.serenegiant.widget.UVCCameraTextureView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UVCCameraHelper {
    private static final double ASPECT_TOLERANCE = 0.02d;
    public static final int FRAME_FORMAT_MJPEG = 1;
    public static final int FRAME_FORMAT_YUYV = 0;
    public static final String MANUFACTURER_NAME = "Sonix Technology Co., Ltd.";
    public static final int MODE_BRIGHTNESS = -2147483647;
    public static final int MODE_CONTRAST = -2147483646;
    private static final int MSG_PREVIEW_SIZE_CHANGED_NO_FRAME = 1;
    public static final double RATIO_16_9 = 1.7777777777777777d;
    public static final double RATIO_4_3 = 1.3333333333333333d;
    public static final String SUFFIX_JPEG = ".jpg";
    public static final String SUFFIX_MP4 = ".mp4";
    private static final String TAG = "UVCCameraHelper";
    public static final int VENDOR_ID = 3141;
    private static UVCCameraHelper mCameraHelper;
    private static DecimalFormat sFormat = new DecimalFormat("##0");
    private Activity mActivity;
    /* access modifiers changed from: private */
    public UVCCameraTextureView mCamView;
    private UVCCameraHandler mCameraHandler;
    /* access modifiers changed from: private */
    public UsbControlBlock mCtrlBlock;
    private int mFrameFormat = 1;
    private MediaActionSoundPlayer mMediaActionSoundPlayer;
    private SharedPreferences mPrefs;
    /* access modifiers changed from: private */
    public UsbDevice mUSBDevice;
    private USBMonitor mUSBMonitor;
    private int previewHeight = UVCCamera.DEFAULT_PREVIEW_HEIGHT;
    private int previewWidth = UVCCamera.DEFAULT_PREVIEW_WIDTH;

    public interface OnMyDevConnectListener {
        void onAttachDevice(UsbDevice usbDevice);

        void onConnectDevice(UsbDevice usbDevice, boolean z);

        void onDetachDevice(UsbDevice usbDevice);

        void onDisConnectDevice(UsbDevice usbDevice);
    }

    private UVCCameraHelper() {
    }

    public static UVCCameraHelper getInstance() {
        if (mCameraHelper == null) {
            mCameraHelper = new UVCCameraHelper();
        }
        return mCameraHelper;
    }

    /* access modifiers changed from: private */
    public void openCamera(UsbControlBlock usbControlBlock) {
        LogUtil.d(TAG, "openCamera");
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            uVCCameraHandler.open(usbControlBlock);
        }
    }

    private static int pixels(Size size) {
        if (size == null) {
            return 0;
        }
        DecimalFormat decimalFormat = sFormat;
        double d = (double) (size.width * size.height);
        Double.isNaN(d);
        return Integer.parseInt(decimalFormat.format(Math.round(d / 1000000.0d)));
    }

    public void capturePicture(int i) {
        LogUtil.d(TAG, "capturePicture");
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null && uVCCameraHandler.isOpened()) {
            if (isCameraSoundOpen()) {
                this.mMediaActionSoundPlayer.play(3);
            }
            this.mCameraHandler.captureStill(i);
        }
    }

    public boolean checkSupportFlag(int i) {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        return uVCCameraHandler != null && uVCCameraHandler.checkSupportFlag((long) i);
    }

    public void closeCamera() {
        LogUtil.d(TAG, "closeCamera");
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            uVCCameraHandler.close();
        }
    }

    public void createUVCCamera() {
        LogUtil.d(TAG, "createUVCCamera");
        if (this.mCamView != null) {
            UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
            if (uVCCameraHandler != null) {
                uVCCameraHandler.release();
                this.mCameraHandler = null;
            }
            MediaActionSoundPlayer mediaActionSoundPlayer = this.mMediaActionSoundPlayer;
            if (mediaActionSoundPlayer != null) {
                mediaActionSoundPlayer.release();
                this.mMediaActionSoundPlayer = null;
            }
            this.mMediaActionSoundPlayer = new MediaActionSoundPlayer();
            this.mPrefs = this.mActivity.getSharedPreferences("UVCCamera", 0);
            String string = this.mPrefs.getString("camera_pixel", "1280 x 720");
            if (string != null) {
                String str = "x";
                this.previewWidth = Integer.parseInt(string.split(str)[0].trim());
                this.previewHeight = Integer.parseInt(string.split(str)[1].trim());
            }
            this.mCamView.setPictureSize(this.previewWidth, this.previewHeight);
            this.mCamView.setAspectRatio((double) (((float) this.previewWidth) / ((float) this.previewHeight)));
            this.mCameraHandler = UVCCameraHandler.createHandler(this.mActivity, this.mCamView, 2, this.previewWidth, this.previewHeight, this.mFrameFormat);
            return;
        }
        throw new NullPointerException("CameraViewInterface cannot be null!");
    }

    public UVCCameraHandler getCameraHandler() {
        return this.mCameraHandler;
    }

    public int getKey() {
        return UVCCamera.PU_BACKLIGHT;
    }

    public int getModelValue(int i) {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            return uVCCameraHandler.getValue(i);
        }
        return 0;
    }

    public int getPreviewHeight() {
        return this.previewHeight;
    }

    public int getPreviewWidth() {
        return this.previewWidth;
    }

    public List<Size> getSupportedPreviewSizes() {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler == null) {
            return null;
        }
        ArrayList<Size> arrayList = new ArrayList<>(uVCCameraHandler.getSupportedPreviewSizes());
        LinkedList linkedList = new LinkedList();
        for (Size size : arrayList) {
            double d = (double) size.width;
            double d2 = (double) size.height;
            Double.isNaN(d);
            Double.isNaN(d2);
            double d3 = d / d2;
            int i = 0;
            while (true) {
                if (i < linkedList.size()) {
                    if (size.width == ((Size) linkedList.get(i)).width && size.height == ((Size) linkedList.get(i)).height) {
                        i = -2;
                        break;
                    } else if (pixels(size) > pixels((Size) linkedList.get(i))) {
                        break;
                    } else {
                        i++;
                    }
                } else {
                    i = -1;
                    break;
                }
            }
            if (i == -1) {
                i = linkedList.size();
            }
            if (d3 == 1.7777777777777777d && i != -2) {
                linkedList.add(i, size);
            }
        }
        return linkedList;
    }

    public USBMonitor getUSBMonitor() {
        return this.mUSBMonitor;
    }

    public int getUsbDeviceCount() {
        List usbDeviceList = getUsbDeviceList();
        if (usbDeviceList == null || usbDeviceList.size() == 0) {
            return 0;
        }
        return usbDeviceList.size();
    }

    public List<UsbDevice> getUsbDeviceList() {
        List deviceFilters = DeviceFilter.getDeviceFilters(this.mActivity.getApplicationContext(), R.xml.device_filter);
        USBMonitor uSBMonitor = this.mUSBMonitor;
        if (uSBMonitor == null || deviceFilters == null) {
            return null;
        }
        return uSBMonitor.getDeviceList((DeviceFilter) deviceFilters.get(0));
    }

    public void initUSBMonitor(Activity activity, UVCCameraTextureView uVCCameraTextureView, final OnMyDevConnectListener pOnMyDevConnectListener) {
        this.mActivity = activity;
        this.mCamView = uVCCameraTextureView;
        this.mUSBMonitor = new USBMonitor(activity.getApplicationContext(), new OnDeviceConnectListener() {
            public void onAttach(UsbDevice usbDevice) {
                LogUtil.d(UVCCameraHelper.TAG, "onAttach");
                UVCCameraHelper.this.mUSBDevice = usbDevice;
                OnMyDevConnectListener onMyDevConnectListener = pOnMyDevConnectListener;
                if (onMyDevConnectListener != null) {
                    onMyDevConnectListener.onAttachDevice(usbDevice);
                }
            }

            @Override
            public void onDettach(UsbDevice device) {

            }

            public void onCancel(UsbDevice usbDevice) {
            }

            public void onConnect(UsbDevice usbDevice, UsbControlBlock usbControlBlock, boolean z) {
                LogUtil.d(UVCCameraHelper.TAG, "onConnect");
                UVCCameraHelper.this.mCtrlBlock = usbControlBlock;
                UVCCameraHelper.this.openCamera(usbControlBlock);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        UVCCameraHelper uVCCameraHelper = UVCCameraHelper.this;
                        uVCCameraHelper.startPreview(uVCCameraHelper.mCamView);
                    }
                }).start();
                OnMyDevConnectListener onMyDevConnectListener = pOnMyDevConnectListener;
                if (onMyDevConnectListener != null) {
                    onMyDevConnectListener.onConnectDevice(usbDevice, true);
                }
            }

            public void onDetach(UsbDevice usbDevice) {
                LogUtil.d(UVCCameraHelper.TAG, "onDetach");
                OnMyDevConnectListener onMyDevConnectListener = pOnMyDevConnectListener;
                if (onMyDevConnectListener != null) {
                    onMyDevConnectListener.onDetachDevice(usbDevice);
                }
            }

            public void onDisconnect(UsbDevice usbDevice, UsbControlBlock usbControlBlock) {
                LogUtil.d(UVCCameraHelper.TAG, "onDisconnect");
                OnMyDevConnectListener onMyDevConnectListener = pOnMyDevConnectListener;
                if (onMyDevConnectListener != null) {
                    onMyDevConnectListener.onDisConnectDevice(usbDevice);
                }
            }
        });
        createUVCCamera();
    }

    public boolean isCameraOpened() {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            return uVCCameraHandler.isOpened();
        }
        return false;
    }

    public boolean isCameraSoundOpen() {
        return this.mPrefs.getBoolean("camera_sound", true);
    }

    public boolean isPreviewing() {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            return uVCCameraHandler.isPreviewing();
        }
        return false;
    }

    public boolean isRecording() {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            return uVCCameraHandler.isRecording();
        }
        return false;
    }

    public void registerUSB() {
        USBMonitor uSBMonitor = this.mUSBMonitor;
        if (uSBMonitor != null) {
            uSBMonitor.register();
        }
    }

    public void release() {
        LogUtil.d(TAG, "release");
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            uVCCameraHandler.release();
            this.mCameraHandler = null;
        }
        MediaActionSoundPlayer mediaActionSoundPlayer = this.mMediaActionSoundPlayer;
        if (mediaActionSoundPlayer != null) {
            mediaActionSoundPlayer.release();
            this.mMediaActionSoundPlayer = null;
        }
        USBMonitor uSBMonitor = this.mUSBMonitor;
        if (uSBMonitor != null) {
            uSBMonitor.destroy();
            this.mUSBMonitor = null;
        }
    }

    public void requestPermission() {
        String str = TAG;
        LogUtil.d(str, "requestPermission");
        List usbDeviceList = getUsbDeviceList();
        if (usbDeviceList != null && usbDeviceList.size() != 0) {
            UsbDevice usbDevice = null;
            Iterator it = usbDeviceList.iterator();
            if (it.hasNext()) {
                usbDevice = (UsbDevice) it.next();
            }
            if (this.mUSBMonitor != null && usbDevice != null) {
                LogUtil.d(str, "requestPermission NV Device");
                this.mUSBMonitor.requestPermission(usbDevice);
            }
        }
    }

    public int resetModelValue(int i) {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            return uVCCameraHandler.resetValue(i);
        }
        return 0;
    }

    public void setDefaultFrameFormat(int i) {
        if (this.mUSBMonitor == null) {
            this.mFrameFormat = i;
            return;
        }
        throw new IllegalStateException("setDefaultFrameFormat should be call before initMonitor");
    }

    public void setDefaultPreviewSize(int i, int i2) {
        if (this.mUSBMonitor == null) {
            this.previewWidth = i;
            this.previewHeight = i2;
            return;
        }
        throw new IllegalStateException("setDefaultPreviewSize should be call before initMonitor");
    }

    public int setModelValue(int i, int i2) {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            return uVCCameraHandler.setValue(i, i2);
        }
        return 0;
    }

    public void setOnPreviewFrameListener(OnPreViewResultListener onPreViewResultListener) {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            uVCCameraHandler.setOnPreViewResultListener(onPreViewResultListener);
        }
    }

    public void setOnRerecordListener(OnRerecordListener onRerecordListener) {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            uVCCameraHandler.setOnRerecordListener(onRerecordListener);
        }
    }

    public void setOnScanCompletedListener(OnScanCompletedListener onScanCompletedListener) {
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            uVCCameraHandler.setOnScanCompletedListener(onScanCompletedListener);
        }
    }

    public void startPreview(CameraViewInterface cameraViewInterface) {
        LogUtil.d(TAG, "startPreview");
        SurfaceTexture surfaceTexture = cameraViewInterface.getSurfaceTexture();
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            uVCCameraHandler.startPreview(surfaceTexture);
        }
    }

    public void startRecording(int i) {
        LogUtil.d(TAG, "startRecording");
        if (this.mCameraHandler != null && !isRecording()) {
            if (isCameraSoundOpen()) {
                this.mMediaActionSoundPlayer.play(1);
            }
            this.mCameraHandler.startRecording(i);
        }
    }

    public void stopPreview() {
        LogUtil.d(TAG, "stopPreview");
        UVCCameraHandler uVCCameraHandler = this.mCameraHandler;
        if (uVCCameraHandler != null) {
            uVCCameraHandler.stopPreview();
        }
    }

    public void stopRecording() {
        LogUtil.d(TAG, "stopRecording");
        if (this.mCameraHandler != null && isRecording()) {
            if (isCameraSoundOpen()) {
                this.mMediaActionSoundPlayer.play(2);
            }
            this.mCameraHandler.stopRecording();
        }
    }

    public void unregisterUSB() {
        USBMonitor uSBMonitor = this.mUSBMonitor;
        if (uSBMonitor != null) {
            uSBMonitor.unregister();
        }
    }

    public void updateResolution(int i, int i2) {
        String str = TAG;
        LogUtil.d(str, "updateResolution +++");
        if (!(i == 0 || i2 == 0 || this.previewWidth == i || this.previewHeight == i2)) {
            stopPreview();
            this.previewWidth = i;
            this.previewHeight = i2;
            this.mCamView.setPictureSize(this.previewWidth, this.previewHeight);
        }
        this.mCameraHandler.setPreviewSize(this.mCamView.getSurfaceTexture(), this.previewWidth, this.previewHeight);
        LogUtil.d(str, "updateResolution ---");
    }

    public void requestPermission(UsbDevice usbDevice) {
        String str = TAG;
        LogUtil.d(str, "requestPermission with usbDevice");
        List usbDeviceList = getUsbDeviceList();
        if (usbDeviceList != null && usbDeviceList.size() != 0) {
            Iterator it = usbDeviceList.iterator();
            UsbDevice usbDevice2 = it.hasNext() ? (UsbDevice) it.next() : null;
            if (usbDevice2 != null) {
                usbDevice = usbDevice2;
            }
            if (this.mUSBMonitor != null && usbDevice != null) {
                LogUtil.d(str, "requestPermission NV Device");
                this.mUSBMonitor.requestPermission(usbDevice);
            }
        }
    }
}
