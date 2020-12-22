package com.android.nightvision;

import android.hardware.usb.UsbDevice;

import com.serenegiant.common.UVCCameraHelper;
import com.serenegiant.common.UVCCameraHelper.OnMyDevConnectListener;
import com.serenegiant.usb.LogUtil;

/* compiled from: NightVisionActivity */
class MOnMyDevConnectListener implements OnMyDevConnectListener {

    /* renamed from: a reason: collision with root package name */
    final  NightVisionActivity mNightVisionActivity;

    public static String TAG = "MOnMyDevConnectListener";

    MOnMyDevConnectListener(NightVisionActivity nightVisionActivity) {
        this.mNightVisionActivity = nightVisionActivity;
    }

    public void onAttachDevice(UsbDevice usbDevice) {
        if (this.mNightVisionActivity.mUVCCameraHelper != null && this.mNightVisionActivity.mUVCCameraHelper.getUsbDeviceCount() != 0 && !this.mNightVisionActivity.isAttachDevice) {
            this.mNightVisionActivity.isAttachDevice = true;
            this.mNightVisionActivity.mUVCCameraHelper.requestPermission();
        }
    }

    public void onConnectDevice(UsbDevice usbDevice, boolean z) {
        LogUtil.d(TAG, "onConnectDevice z =  "+ z);
        if (!z) {
            this.mNightVisionActivity.C = false;
        } else if (!this.mNightVisionActivity.g) {
            this.mNightVisionActivity.C = true;
            if (this.mNightVisionActivity.z) {
                this.mNightVisionActivity.z = false;
                this.mNightVisionActivity.mHandler.sendEmptyMessageDelayed(1, 200);
                return;
            }
            this.mNightVisionActivity.mHandler.sendEmptyMessage(1);
        }
    }

    public void onDetachDevice(UsbDevice usbDevice) {
        this.mNightVisionActivity.isAttachDevice = false;
        if (this.mNightVisionActivity.mSharedPreferences != null) {
            this.mNightVisionActivity.mSharedPreferences.edit().putBoolean(Constant.c, true).apply();
        }
        if (this.mNightVisionActivity.A) {
            this.mNightVisionActivity.mUVCCameraHelper.stopRecording();
            this.mNightVisionActivity.mHandler.sendEmptyMessageDelayed(5, 200);
            return;
        }
        this.mNightVisionActivity.mHandler.sendEmptyMessage(5);
    }

    public void onDisConnectDevice(UsbDevice usbDevice) {
        if (!this.mNightVisionActivity.g) {
            this.mNightVisionActivity.mHandler.sendEmptyMessage(2);
        }
    }
}
