package com.android.nightvision;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.b.a.b.BitmapCreator;
import com.b.a.b.IOnThumbnailUpdateListener;
import com.b.a.b.MThumbnailViewManager;
import com.serenegiant.common.PreviewRetry;
import com.serenegiant.common.PreviewRetry.OnPreviewRetryListener;
import com.serenegiant.common.UVCCameraHelper;
import com.serenegiant.common.UVCCameraHelper.OnMyDevConnectListener;
import com.serenegiant.widget.VideoFileSizeObserver.OnRerecordListener;
import com.serenegiant.encoder.MediaMuxerWrapper;
import com.serenegiant.usb.LogUtil;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usbcameracommon.AbstractUVCCameraHandler;
import com.serenegiant.widget.CameraViewInterface;
import com.serenegiant.widget.CameraViewInterface.Callback;
import com.serenegiant.widget.UVCCameraTextureView;

import java.io.File;

public class NightVisionActivity extends PermissionActivity implements OnClickListener, Callback,
        AbstractUVCCameraHandler.CameraThread.OnPreViewResultListener,
        AbstractUVCCameraHandler.CameraThread.OnScanCompletedListener,
        OnPreviewRetryListener,
        IOnThumbnailUpdateListener,
        OnRerecordListener {
    /* access modifiers changed from: private */
    public static final String TAG = "NightVisionActivity";
    /* access modifiers changed from: private */
    public boolean A;
    /* access modifiers changed from: private */
    public boolean B;
    /* access modifiers changed from: private */
    public boolean C;
    private OnMyDevConnectListener D = new c(this);
    /* access modifiers changed from: private */
    public Runnable E = new g(this);
    private long e;
    private int f = 0;
    /* access modifiers changed from: private */
    public boolean g = true;
    /* access modifiers changed from: private */
    public boolean h = false;
    private boolean i;
    private final Object j = new Object();
    /* access modifiers changed from: private */
    public UVCCameraHelper k;
    /* access modifiers changed from: private */
    public UVCCameraTextureView l;
    private l m;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler(new MCallback(this));
    private ProgressDialog n;
    private MThumbnailViewManager mThumbnailViewManager;
    private Uri p;
    /* access modifiers changed from: private */
    public SharedPreferences q;
    /* access modifiers changed from: private */
    public String r_UnKnow = "";
    private ImageButton s;
    private ImageButton t;
    private Chronometer u;
    private LinearLayout v;
    /* access modifiers changed from: private */
    public View w;
    /* access modifiers changed from: private */
    public LinearLayout x;
    /* access modifiers changed from: private */
    public LinearLayout y;
    /* access modifiers changed from: private */
    public boolean z;

    private void t() {
        this.mHandler.removeMessages(10);
        this.mHandler.removeMessages(9);
        getWindow().clearFlags(128);
    }

    private void u() {
        Window window = getWindow();
        boolean z2 = true;
        if (getResources().getConfiguration().orientation != 1) {
            z2 = false;
        }
        if (VERSION.SDK_INT >= 28) {
            if (z2) {
                window.clearFlags(1024);
                window.getAttributes().layoutInDisplayCutoutMode = 2;
                return;
            }
            window.addFlags(1024);
        } else if (z2) {
            window.clearFlags(1024);
        } else {
            window.addFlags(1024);
        }
    }

    private void v() {
        if (!this.n.isShowing()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getResources().getString(R.string.usb_device_loading_message));
            String str = "\n";
            sb.append(str);
            sb.append(" ");
            sb.append(str);
            sb.append(getResources().getString(R.string.usb_device_loading_message_note));
            sb.append(str);
            sb.append(getResources().getString(R.string.usb_device_loading_message_note_1));
            sb.append(str);
            sb.append(getResources().getString(R.string.usb_device_loading_message_note_2));
            this.n.setTitle(getResources().getString(R.string.app_name));
            this.n.setMessage(sb);
            this.n.setCancelable(false);
            this.n.show();
            this.mHandler.sendEmptyMessageDelayed(8, 16000);
        }
    }

    public void handleTimeOut() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("handleTimeOut.count = ");
        sb.append(this.f);
        LogUtil.i(str, sb.toString());
        this.f++;
        if (!this.g) {
            this.g = true;
        }
        if (this.f > 5) {
            Constant.a(this, this.h ? R.string.usb_update_resolution_failed :
                    R.string.usb_device_loading_failed);
            finish();
            return;
        }
        synchronized (this.j) {
            if (this.k == null) {
                this.k = UVCCameraHelper.getInstance();
            }
            this.k.unregisterUSB();
            this.k.release();
            this.k.initUSBMonitor(this, this.l, this.D);
            this.k.setOnPreviewFrameListener(this);
            this.k.setOnScanCompletedListener(this);
            this.k.registerUSB();
            if (this.B && !this.C) {
                this.k.requestPermission();
            }
        }
    }

    public void onClick(View view) {
        if (!o()) {
            int id = view.getId();
            if (id != R.id.capture_button) {
                if (id == R.id.recording_button) {
                    s();
                } else if (id == R.id.settings_button) {
                    this.m.c();
                }
            } else if (this.k.isCameraOpened() && this.k.isPreviewing()) {
                r();
                this.k.capturePicture(this.r_UnKnow);
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        u();
    }

    public boolean onKeyDown(int i2, KeyEvent keyEvent) {
        if (i2 == 24) {
            if (!o()) {
                s();
            }
            return true;
        } else if (i2 == 25 || i2 == 27 || i2 == 133) {
            if (this.k.isCameraOpened() && this.k.isPreviewing() && !o()) {
                r();
                this.k.capturePicture(this.r_UnKnow);
            }
            return true;
        } else if (i2 != 136) {
            return super.onKeyDown(i2, keyEvent);
        } else {
            LogUtil.d(TAG, "night vision device removed");
            SharedPreferences sharedPreferences = this.q;
            if (sharedPreferences != null) {
                sharedPreferences.edit().putBoolean(Constant.c, true).apply();
            }
            this.mHandler.sendEmptyMessageDelayed(6, 500);
            return true;
        }
    }

    public void onOpen(UVCCamera uVCCamera) {
        if (uVCCamera != null) {
            PreviewRetry.getInstance().startTimeOut(3000);
        }
    }

    public void onPreviewResult(byte[] bArr) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onPreviewResult IsUpdateResolution: ");
        sb.append(this.h);
        LogUtil.i(str, sb.toString());
        this.f = 0;
        this.g = false;
        PreviewRetry.getInstance().cancelTime();
        this.k.setOnPreviewFrameListener(null);
        if (!this.h) {
            this.mHandler.removeMessages(8);
            this.mHandler.sendEmptyMessage(1);
            this.n.dismiss();
            if (getResources().getBoolean(R.bool.config_take_video_key_support)) {
                this.mHandler.post(this.E);
            }
        }
        this.h = false;
    }

    public void onRerecord() {
        LogUtil.e(TAG, "onRerecord");
        runOnUiThread(new d(this));
        this.mHandler.sendEmptyMessageDelayed(7, 500);
    }

    public void onScanCompleted(Bitmap bitmap, Uri uri, String str, boolean z2) {
        Bitmap bitmap2;
        this.p = uri;
        int c = this.mThumbnailViewManager.c();
        if (z2) {
            bitmap2 = BitmapCreator.a(str, c);
        } else if (bitmap != null) {
            Bitmap a2 = BitmapCreator.a(bitmap, c);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap2 = a2;
        } else {
            bitmap2 = null;
        }
        if (bitmap2 != null) {
            this.mThumbnailViewManager.a(bitmap2);
        }
    }

    public void onSurfaceChanged(CameraViewInterface cameraViewInterface, Surface surface, int i2, int i3) {
        if (this.k.isCameraOpened()) {
            this.mHandler.sendEmptyMessage(3);
        }
    }

    public void onSurfaceCreated(CameraViewInterface cameraViewInterface, Surface surface) {
        if (!this.C && this.k.isCameraOpened()) {
            synchronized (this.j) {
                this.k.startPreview(this.l);
                this.C = true;
            }
        }
    }

    public void onSurfaceDestroy(CameraViewInterface cameraViewInterface, Surface surface) {
        synchronized (this.j) {
            this.C = false;
            if (this.k != null) {
                this.k.stopPreview();
            }
        }
    }

    public void onUserInteraction() {
        super.onUserInteraction();
        UVCCameraHelper uVCCameraHelper = this.k;
        if (uVCCameraHelper != null && !uVCCameraHelper.isRecording() && !this.A) {
            a(false);
        }
    }

    private void m() {
        this.l.setCallback(this);
        this.l.initOpenGL();
        this.k = UVCCameraHelper.getInstance();
        PreviewRetry.getInstance().setPreviewRetryListener(this);
        this.mThumbnailViewManager = new MThumbnailViewManager(this, this);
        this.m = new l();
        this.m.a(this, this, this.k);
    }

    private void n() {
        ((ImageButton) findViewById(R.id.capture_button)).setOnClickListener(this);
        this.s = (ImageButton) findViewById(R.id.recording_button);
        this.s.setOnClickListener(this);
        this.t = (ImageButton) findViewById(R.id.settings_button);
        this.t.setOnClickListener(this);
        this.v = (LinearLayout) findViewById(R.id.recording_time_container);
        this.u = (Chronometer) findViewById(R.id.recording_time);
        this.l = (UVCCameraTextureView) findViewById(R.id.camera_view);
        this.w = findViewById(R.id.capture_animator_view);
        this.x = (LinearLayout) findViewById(R.id.camera_empty);
        this.y = (LinearLayout) findViewById(R.id.camera_button_container);
    }

    /* access modifiers changed from: private */
    public boolean o() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.e < 1000) {
            return true;
        }
        this.e = currentTimeMillis;
        return false;
    }

    /* access modifiers changed from: private */
    public void p() {
        this.mHandler.removeMessages(9);
        getWindow().addFlags(128);
    }

    /* access modifiers changed from: private */
    public void q() {
        this.mHandler.removeMessages(9);
        getWindow().addFlags(128);
        this.mHandler.sendEmptyMessageDelayed(9, 120000);
    }

    /* access modifiers changed from: private */
    public void r() {
        LogUtil.d(TAG, "playCaptureAnimation +");
        this.w.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.cature_anim);
        animatorSet.setTarget(this.w);
        animatorSet.addListener(new e(this));
        animatorSet.start();
        LogUtil.d(TAG, "playCaptureAnimation -");
    }

    /* access modifiers changed from: private */
    public void s() {
        if (this.k.isCameraOpened() && this.k.isPreviewing()) {
            if (this.k.isRecording() || this.A) {
                LogUtil.d(TAG, "stopRecording");
                this.A = false;
                this.s.setImageResource(R.drawable.ic_action_record_pause);
                this.k.setOnRerecordListener(null);
                this.k.stopRecording();
                a(false);
                this.v.setVisibility(View.GONE);
                this.u.stop();
                this.t.setVisibility(View.VISIBLE);
                this.mThumbnailViewManager.a(false);
                return;
            }
            LogUtil.d(TAG, "startRecording");
            String i2 = this.r_UnKnow;
            this.A = true;
            this.s.setImageResource(R.drawable.ic_action_record_start);
            this.k.setOnRerecordListener(this);
            this.k.startRecording(i2);
            a(true);
            this.v.setVisibility(View.VISIBLE);
            this.u.setBase(SystemClock.elapsedRealtime());
            this.t.setVisibility(View.INVISIBLE);
            this.mThumbnailViewManager.a(true);
            this.u.start();
        }
    }

    /* access modifiers changed from: protected */
    public void g() {
        super.g();
        this.k = null;
    }

    /* access modifiers changed from: protected */
    public void h() {
        this.i = false;
        super.h();
        PreviewRetry.getInstance().cancelTime();
        t();
        this.mHandler.removeCallbacksAndMessages(null);
        this.k.setOnRerecordListener(null);
        synchronized (this.j) {
            if (this.A) {
                this.k.stopRecording();
            }
            this.k.setOnScanCompletedListener(null);
            this.k.setOnPreviewFrameListener(null);
            this.k.unregisterUSB();
        }
        this.n.dismiss();
        this.m.a();
        this.l.onPause();
    }

    /* access modifiers changed from: protected */
    public void i() {
        super.i();
        SharedPreferences sharedPreferences = this.q;
        if (sharedPreferences == null || !sharedPreferences.getBoolean(Constant.c, false)) {
            UsbDevice usbDevice = (UsbDevice) getIntent().getParcelableExtra("device");
            this.i = true;
            u();
            if (this.A) {
                this.A = false;
                this.s.setImageResource(R.drawable.ic_action_record_pause);
                if (this.v.getVisibility() == View.VISIBLE) {
                    this.v.setVisibility(View.GONE);
                    this.u.stop();
                }
                if (this.t.getVisibility() != View.VISIBLE) {
                    this.t.setVisibility(View.VISIBLE);
                }
            }
            q();
            v();
            this.g = true;
            this.h = false;
            this.x.setVisibility(View.VISIBLE);
            this.l.onResume();
            this.mThumbnailViewManager.d();
            synchronized (this.j) {
                if (this.k != null) {
                    this.k.initUSBMonitor(this, this.l, this.D);
                    this.k.setOnPreviewFrameListener(this);
                    this.k.setOnScanCompletedListener(this);
                    this.k.registerUSB();
                    if (this.B) {
                        this.k.requestPermission(usbDevice);
                    }
                }
            }
            this.m.b();
            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void k() {
        super.k();
        synchronized (this.j) {
            if (this.k != null) {
                this.k.release();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void b(@Nullable Bundle bundle) {
        super.b(bundle);
        setContentView((int) R.layout.activity_main_common);
        this.z = true;
        final File outputFile = MediaMuxerWrapper.getCaptureFile(Environment.DIRECTORY_DCIM, ".png");
        r_UnKnow = outputFile.toString();
        n();
        m();
        this.n = new ProgressDialog(this);
        this.q = getSharedPreferences(Constant.f93b, 0);
    }

    public void a(int i2, int i3) {
        if (this.k.isCameraOpened()) {
            Message message = new Message();
            message.what = 3;
            message.arg1 = i2;
            message.arg2 = i3;
            this.mHandler.sendMessage(message);
        }
    }

    public void a(Uri uri) {
        this.p = uri;
    }

    public void a() {
        if (this.p == null) {
            LogUtil.d(TAG, "uri is null, can not go to gallery");
            return;
        }
        String type = getContentResolver().getType(this.p);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("[goToGallery] uri: ");
        sb.append(this.p);
        sb.append(", mimeType = ");
        sb.append(type);
        LogUtil.d(str, sb.toString());
        Intent intent = new Intent("com.android.camera.action.REVIEW");
        intent.setDataAndType(this.p, type);
        intent.putExtra("isUVCCamera", true);
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (VERSION.SDK_INT >= 23 && 2 == activityManager.getLockTaskModeState()) {
//            intent.addFlags(134742016); todo 不知道是什么值
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e2) {
            LogUtil.e(TAG, "[startGalleryActivity] Couldn't view ", e2);
        }
    }

    public void a(boolean z2) {
        if (this.i) {
            this.mHandler.removeMessages(10);
            Message obtain = Message.obtain();
            obtain.arg1 = z2 ? 1 : 0;
            obtain.what = 10;
            this.mHandler.sendMessage(obtain);
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2) {
        runOnUiThread(new h(this, i2));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        // TODO: 2020/12/14
    }   
}
