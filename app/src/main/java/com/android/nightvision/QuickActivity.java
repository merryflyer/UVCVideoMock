package com.android.nightvision;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.serenegiant.usb.LogUtil;

public abstract class QuickActivity extends AppCompatActivity {

    /* renamed from: a reason: collision with root package name */
    protected boolean f81a = false;

    /* access modifiers changed from: protected */
    public void a(Bundle bundle) {
    }

    /* access modifiers changed from: protected */
    public void b() {
    }

    /* access modifiers changed from: protected */
    public void c() {
    }

    /* access modifiers changed from: protected */
    public void d() {
    }

    /* access modifiers changed from: protected */
    public void e() {
    }

    /* access modifiers changed from: protected */
    public void f() {
    }

    /* access modifiers changed from: protected */
    public final void onCreate(Bundle bundle) {
        LogUtil.di("QuickActivity", "onCreate()");
        this.f81a = true;
        super.onCreate(bundle);
        a(bundle);
    }

    /* access modifiers changed from: protected */
    public final void onDestroy() {
        super.onDestroy();
        String str = "QuickActivity";
        LogUtil.di(str, "onDestroy() ++++++");
        b();
        LogUtil.di(str, "onDestroy() ------");
    }

    /* access modifiers changed from: protected */
    public final void onPause() {
        String str = "QuickActivity";
        LogUtil.i(str, "onPause() ++++++");
        super.onPause();
        c();
        this.f81a = false;
        LogUtil.i(str, "onPause() ------");
    }

    /* access modifiers changed from: protected */
    public final void onRestart() {
        LogUtil.di("QuickActivity", "onRestart()");
        super.onRestart();
    }

    /* access modifiers changed from: protected */
    public final void onResume() {
        String str = "QuickActivity";
        LogUtil.di(str, "onResume()");
        LogUtil.dd(str, "onResume --> onPermissionResumeTasks()");
        super.onResume();
        d();
    }

    /* access modifiers changed from: protected */
    public final void onStart() {
        LogUtil.di("QuickActivity", "onStart()");
        super.onStart();
        e();
    }

    /* access modifiers changed from: protected */
    public final void onStop() {
        super.onStop();
        String str = "QuickActivity";
        LogUtil.i(str, "onStop() ++++++");
        f();
        LogUtil.i(str, "onStop() ------");
    }
}
