package com.android.nightvision;

import android.hardware.camera2.CameraManager.TorchCallback;
import android.text.TextUtils;

/* compiled from: FlashLight */
class a extends TorchCallback {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ b f82a;

    a(b bVar) {
        this.f82a = bVar;
    }

    private void a(boolean z) {
        synchronized (this.f82a) {
            this.f82a.g = z;
        }
    }

    private void b(boolean z) {
        synchronized (this.f82a) {
            this.f82a.f = z;
        }
    }

    public void onTorchModeChanged(String str, boolean z) {
        if (TextUtils.equals(str, this.f82a.e)) {
            a(true);
            b(z);
        }
    }

    public void onTorchModeUnavailable(String str) {
        if (TextUtils.equals(str, this.f82a.e)) {
            a(false);
        }
    }
}
