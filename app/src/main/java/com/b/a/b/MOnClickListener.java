package com.b.a.b;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: ThumbnailViewManager */
class MOnClickListener implements OnClickListener {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ MThumbnailViewManager f69a;

    MOnClickListener(MThumbnailViewManager MThumbnailViewManagerVar) {
        this.f69a = MThumbnailViewManagerVar;
    }

    public void onClick(View view) {
        if (this.f69a.k != null) {
            this.f69a.k.a();
        }
    }
}
