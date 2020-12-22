package com.android.nightvision;

/* compiled from: NightVisionActivity */
class g implements Runnable {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ NightVisionActivity f89a;

    g(NightVisionActivity nightVisionActivity) {
        this.f89a = nightVisionActivity;
    }

    public void run() {
        if (this.f89a.mUVCCameraHelper.isPreviewing() && this.f89a.mUVCCameraHelper.checkSupportFlag(this.f89a.mUVCCameraHelper.getKey())) {
            int modelValue = this.f89a.mUVCCameraHelper.getModelValue(this.f89a.mUVCCameraHelper.getKey());
            if (modelValue != 0) {
                this.f89a.mUVCCameraHelper.setModelValue(this.f89a.mUVCCameraHelper.getKey(), 0);
                this.f89a.a(modelValue);
            }
        }
        this.f89a.mHandler.postDelayed(this.f89a.E, 100);
    }
}
