package com.android.nightvision;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraManager.TorchCallback;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;
import com.serenegiant.usb.LogUtil;

/* compiled from: FlashLight */
public class b {

    /* renamed from: a reason: collision with root package name */
    private static final String f83a = "b";

    /* renamed from: b reason: collision with root package name */
    private final CameraManager f84b;
    private final Context c;
    private Handler d;
    /* access modifiers changed from: private */
    public String e;
    /* access modifiers changed from: private */
    public boolean f;
    /* access modifiers changed from: private */
    public boolean g;
    private final TorchCallback h = new a(this);

    public b(Context context) {
        this.c = context;
        this.f84b = (CameraManager) this.c.getSystemService(Context.CAMERA_SERVICE);
        f();
    }

    private synchronized void d() {
        if (this.d == null) {
            HandlerThread handlerThread = new HandlerThread(f83a);
            handlerThread.start();
            this.d = new Handler(handlerThread.getLooper());
        }
    }

    private String e() {
        String[] cameraIdList;
        try {
            for (String str : this.f84b.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = this.f84b.getCameraCharacteristics(str);
                Boolean bool = (Boolean) cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer num = (Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (bool != null && bool.booleanValue() && num != null && num.intValue() == 1) {
                    return str;
                }
            }
        } catch (CameraAccessException cameraAccessException) {
            cameraAccessException.printStackTrace();
        }
        return null;
    }

    private void f() {
        try {
            this.e = e();
        } catch (Throwable th) {
            LogUtil.e(f83a, "Couldn't initialize.", th);
        }
        if (this.e != null) {
            d();
            this.f84b.registerTorchCallback(this.h, this.d);
        }
    }

    public synchronized boolean c() {
        return this.f;
    }

    public synchronized boolean b() {
        return this.g;
    }

    public void a(boolean z) {
        synchronized (this) {
            if (this.e != null) {
                if (this.f != z) {
                    this.f = z;
                    try {
                        this.f84b.setTorchMode(this.e, z);
                    } catch (CameraAccessException e2) {
                        LogUtil.e(f83a, "Couldn't set torch mode", e2);
                        this.f = false;
                        Toast.makeText(this.c,"open_flash_failed" , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public boolean a() {
        return this.c.getPackageManager().hasSystemFeature("android.hardware.camera.flash");
    }
}
