package com.android.nightvision;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/* compiled from: NightVisionActivity */
class e extends AnimatorListenerAdapter {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ NightVisionActivity f87a;

    e(NightVisionActivity nightVisionActivity) {
        this.f87a = nightVisionActivity;
    }

    public void onAnimationEnd(Animator animator) {
        super.onAnimationEnd(animator);
        this.f87a.w.setVisibility(View.GONE);
    }
}
