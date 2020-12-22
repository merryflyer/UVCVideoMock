package com.android.nightvision;

import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.serenegiant.usb.LogUtil;

public abstract class PermissionActivity extends QuickActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    /* renamed from: b reason: collision with root package name */
    private PermissionUtils permissionUtils;
    private Bundle c;
    private int d = 4;

    /* access modifiers changed from: protected */
    public void a(Bundle bundle) {
        this.permissionUtils = new PermissionUtils(this);
        this.c = bundle;
        if (this.permissionUtils.a()) {
            customOnCreate(bundle);
            this.d = 1;
        }
    }

    /* access modifiers changed from: protected */
    public void b() {
        if (this.d != 4) {
            g();
            this.d = 4;
        }
    }

    /* access modifiers changed from: protected */
    public void customOnCreate(Bundle bundle) {
    }

    /* access modifiers changed from: protected */
    public void c() {
        if (this.d == 2) {
            customOnPause();
            this.d = 3;
        }
    }

    /* access modifiers changed from: protected */
    public void customResume() {
        if (this.permissionUtils.a() || this.permissionUtils.c()) {
            if (this.d == 4) {
                customOnCreate(this.c);
            }
            this.c = null;
            startFindDevices();
            this.d = 2;
        }
    }

    /* access modifiers changed from: protected */
    public void e() {
        j();
    }

    /* access modifiers changed from: protected */
    public void f() {
        customOnStop();
    }

    /* access modifiers changed from: protected */
    public void g() {
    }

    /* access modifiers changed from: protected */
    public void customOnPause() {
    }

    /* access modifiers changed from: protected */
    public void startFindDevices() {
    }

    /* access modifiers changed from: protected */
    public void j() {
    }

    /* access modifiers changed from: protected */
    public void customOnStop() {
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("onRequestPermissionsResult(), grantResults = ");
        sb.append(iArr.length);
        LogUtil.dd("PermissionActivity", sb.toString());
        if (iArr.length > 0 && this.permissionUtils.b() == i && !this.permissionUtils.a(strArr, iArr)) {
            finish();
        }
    }
}
