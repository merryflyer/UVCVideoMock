package com.android.nightvision;

import android.hardware.usb.UsbDevice;
import com.serenegiant.common.UVCCameraHelper.OnMyDevConnectListener;

/* compiled from: NightVisionActivity */
class c implements OnMyDevConnectListener {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ NightVisionActivity f85a;

    c(NightVisionActivity nightVisionActivity) {
        this.f85a = nightVisionActivity;
    }

    public void onAttachDevice(UsbDevice usbDevice) {
        if (this.f85a.k != null && this.f85a.k.getUsbDeviceCount() != 0 && !this.f85a.B) {
            this.f85a.B = true;
            this.f85a.k.requestPermission();
        }
    }

    public void onConnectDevice(UsbDevice usbDevice, boolean z) {
        if (!z) {
            this.f85a.C = false;
        } else if (!this.f85a.g) {
            this.f85a.C = true;
            if (this.f85a.z) {
                this.f85a.z = false;
                this.f85a.mHandler.sendEmptyMessageDelayed(1, 200);
                return;
            }
            this.f85a.mHandler.sendEmptyMessage(1);
        }
    }

    public void onDetachDevice(UsbDevice usbDevice) {
        this.f85a.B = false;
        if (this.f85a.q != null) {
            this.f85a.q.edit().putBoolean(Constant.c, true).apply();
        }
        if (this.f85a.A) {
            this.f85a.k.stopRecording();
            this.f85a.mHandler.sendEmptyMessageDelayed(5, 200);
            return;
        }
        this.f85a.mHandler.sendEmptyMessage(5);
    }

    public void onDisConnectDevice(UsbDevice usbDevice) {
        if (!this.f85a.g) {
            this.f85a.mHandler.sendEmptyMessage(2);
        }
    }
}
