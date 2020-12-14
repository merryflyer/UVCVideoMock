package com.b.a.b;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/* compiled from: ThumbnailViewManager */
class MAnimationListener implements AnimationListener {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ MThumbnailViewManager f70a;

    MAnimationListener(MThumbnailViewManager MThumbnailViewManagerVar) {
        this.f70a = MThumbnailViewManagerVar;
    }

    public void onAnimationEnd(Animation animation) {
    }

    public void onAnimationRepeat(Animation animation) {
        this.f70a.d.setImageDrawable(this.f70a.f);
    }

    public void onAnimationStart(Animation animation) {
    }
}
