package com.android.nightvision;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.serenegiant.usb.Size;

/* compiled from: SettingsDialogFragment */
class k implements OnItemClickListener {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ Dialog f96a;

    /* renamed from: b reason: collision with root package name */
    final /* synthetic */ l f97b;

    k(l lVar, Dialog dialog) {
        this.f97b = lVar;
        this.f96a = dialog;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.f96a.isShowing()) {
            Size size = (Size) this.f97b.f99b.getSupportedPreviewSizes().get(i);
            String str = "camera_pixel";
            if (this.f97b.p.getString(str, "0").equals(size.getPreviewSize())) {
                this.f96a.dismiss();
                return;
            }
            if (this.f97b.q != null) {
                this.f97b.q.putString(str, size.getPreviewSize());
                this.f97b.q.apply();
            }
            this.f97b.n.setText(size.getPreviewSize());
            ((NightVisionActivity) this.f97b.d).a(size.getWidth(), size.getHeight());
            this.f96a.dismiss();
            this.f97b.dismiss();
        }
    }
}
