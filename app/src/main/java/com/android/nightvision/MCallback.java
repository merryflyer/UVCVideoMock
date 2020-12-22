package com.android.nightvision;

import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;

import com.serenegiant.usb.LogUtil;

/* compiled from: NightVisionActivity */
class MCallback implements Callback {

    /* renamed from: a reason: collision with root package name */
    final /* synthetic */ NightVisionActivity nightVisionActivity;

    MCallback(NightVisionActivity nightVisionActivity) {
        this.nightVisionActivity = nightVisionActivity;
    }

    public boolean handleMessage(Message message) {
        boolean z = false;
        switch (message.what) {
            case 1:
                this.nightVisionActivity.x.setVisibility(View.GONE);
                this.nightVisionActivity.y.setVisibility(View.VISIBLE);
                break;
            case 2:
                this.nightVisionActivity.x.setVisibility(View.VISIBLE);
                this.nightVisionActivity.y.setVisibility(View.INVISIBLE);
                break;
            case 3:
                if (this.nightVisionActivity.isFindingDevices) {
                    Constant.showToast(this.nightVisionActivity, R.string.usb_update_resolution_message);
                    break;
                } else {
                    this.nightVisionActivity.isFindingDevices = true;
//                    PreviewRetry.getInstance().startTimeOut(3000); todo 临时屏蔽 zhaolei
                    this.nightVisionActivity.mUVCCameraHelper.setOnPreviewFrameListener(this.nightVisionActivity);
                    this.nightVisionActivity.mUVCCameraTextureView.resetFps();
                    this.nightVisionActivity.mUVCCameraTextureView.resetFrame();
                    this.nightVisionActivity.mUVCCameraHelper.updateResolution(message.arg1, message.arg2);
                    break;
                }
            case 4:
                LogUtil.i(NightVisionActivity.TAG, "View no frame ?");
                if (this.nightVisionActivity.mUVCCameraHelper != null && this.nightVisionActivity.mUVCCameraTextureView.getFrameNum() < 3) {
                    LogUtil.i(NightVisionActivity.TAG, "View no frame, Re-update");
                    this.nightVisionActivity.mUVCCameraHelper.updateResolution(message.arg1, message.arg2);
                    this.nightVisionActivity.mHandler.sendEmptyMessageDelayed(4, 3000);
                    break;
                }
            case 5:
            case 6:
                String l = NightVisionActivity.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("finish msg.what = ");
                sb.append(message.what);
                LogUtil.i(l, sb.toString());
                Constant.showToast(this.nightVisionActivity, R.string.usb_device_disconnect);
                this.nightVisionActivity.finish();
                break;
            case 7:
                LogUtil.i(NightVisionActivity.TAG, "Re-recording");
                this.nightVisionActivity.s();
                break;
            case 8:
                Constant.showToast(this.nightVisionActivity, R.string.usb_device_loading_failed);
                this.nightVisionActivity.finish();
                break;
            case 9:
                this.nightVisionActivity.getWindow().clearFlags(128);
                break;
            case 10:
                if (message.arg1 == 1) {
                    z = true;
                }
                if (!z) {
                    this.nightVisionActivity.release();
                    break;
                } else {
                    this.nightVisionActivity.p();
                    break;
                }
            default:
                LogUtil.e(NightVisionActivity.TAG, "msg what is null");
                break;
        }
        return true;
    }
}
