package com.android.nightvision;

import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;

import com.serenegiant.common.PreviewRetry;
import com.serenegiant.usb.LogUtil;

/* compiled from: NightVisionActivity */
class MCallback implements Callback {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ NightVisionActivity f88a;

    MCallback(NightVisionActivity nightVisionActivity) {
        this.f88a = nightVisionActivity;
    }

    public boolean handleMessage(Message message) {
        boolean z = false;
        switch (message.what) {
            case 1:
                this.f88a.x.setVisibility(View.GONE);
                this.f88a.y.setVisibility(View.VISIBLE);
                break;
            case 2:
                this.f88a.x.setVisibility(View.VISIBLE);
                this.f88a.y.setVisibility(View.INVISIBLE);
                break;
            case 3:
                if (this.f88a.h) {
                    Constant.a(this.f88a, R.string.usb_update_resolution_message);
                    break;
                } else {
                    this.f88a.h = true;
                    PreviewRetry.getInstance().startTimeOut(3000);
                    this.f88a.k.setOnPreviewFrameListener(this.f88a);
                    this.f88a.l.resetFps();
                    this.f88a.l.resetFrame();
                    this.f88a.k.updateResolution(message.arg1, message.arg2);
                    break;
                }
            case 4:
                LogUtil.i(NightVisionActivity.TAG, "View no frame ?");
                if (this.f88a.k != null && this.f88a.l.getFrameNum() < 3) {
                    LogUtil.i(NightVisionActivity.TAG, "View no frame, Re-update");
                    this.f88a.k.updateResolution(message.arg1, message.arg2);
                    this.f88a.mHandler.sendEmptyMessageDelayed(4, 3000);
                    break;
                }
            case 5:
            case 6:
                String l = NightVisionActivity.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("finish msg.what = ");
                sb.append(message.what);
                LogUtil.i(l, sb.toString());
                Constant.a(this.f88a, R.string.usb_device_disconnect);
                this.f88a.finish();
                break;
            case 7:
                LogUtil.i(NightVisionActivity.TAG, "Re-recording");
                this.f88a.s();
                break;
            case 8:
                Constant.a(this.f88a, R.string.usb_device_loading_failed);
                this.f88a.finish();
                break;
            case 9:
                this.f88a.getWindow().clearFlags(128);
                break;
            case 10:
                if (message.arg1 == 1) {
                    z = true;
                }
                if (!z) {
                    this.f88a.q();
                    break;
                } else {
                    this.f88a.p();
                    break;
                }
            default:
                LogUtil.e(NightVisionActivity.TAG, "msg what is null");
                break;
        }
        return true;
    }
}
