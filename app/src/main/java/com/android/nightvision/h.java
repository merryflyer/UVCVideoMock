package com.android.nightvision;

/* compiled from: NightVisionActivity */
class h implements Runnable {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ int f90a;

    /* renamed from: b reason: collision with root package name */
    final /* synthetic */ NightVisionActivity f91b;

    h(NightVisionActivity nightVisionActivity, int i) {
        this.f91b = nightVisionActivity;
        this.f90a = i;
    }

    public void run() {
        int i = this.f90a;
        if (i == 1) {
            if (this.f91b.k.isCameraOpened() && this.f91b.k.isPreviewing() && !this.f91b.o()) {
                this.f91b.r();
                this.f91b.k.capturePicture(this.f91b.r_UnKnow);
            }
        } else if (i == 2 && !this.f91b.o()) {
            this.f91b.s();
        }
    }
}
