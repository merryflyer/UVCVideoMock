package com.serenegiant.common;

import android.annotation.TargetApi;
import android.media.MediaActionSound;
import com.serenegiant.usb.LogUtil;

@TargetApi(16)
class MediaActionSoundPlayer {
    public static final int FOCUS_COMPLETE = 0;
    public static final int SHUTTER_CLICK = 3;
    public static final int START_VIDEO_RECORDING = 1;
    public static final int STOP_VIDEO_RECORDING = 2;
    private static final String TAG = "MediaActionSoundPlayer";
    private MediaActionSound mSound = new MediaActionSound();

    protected MediaActionSoundPlayer() {
        this.mSound.load(2);
        this.mSound.load(3);
        this.mSound.load(1);
        this.mSound.load(0);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0049, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void play(int p0) {

        try{

            synchronized (this) {
                if (null != this.mSound) {
                    LogUtil.e(MediaActionSoundPlayer.TAG, "[play] mSound is null");
                    return;
                }else {
                    int vi = 1;
                    if (p0 >0) {
                        int vi1 = 2;
                        if (p0 != vi) {
                            int str = 3;
                            if (p0 != vi1) {
                                if (p0 != str) {
                                    LogUtil.w(MediaActionSoundPlayer.TAG, new StringBuilder().append("Unrecognized action:").append(p0).toString());
                                }else {
                                    this.mSound.play(0);
                                }
                            }else {
                                this.mSound.play(str);
                            }
                        }else {
                            this.mSound.play(vi1);
                        }
                    }else {
                        this.mSound.play(vi);
                    }
                    return;
                }
            }
        }catch(Exception e4){
            throw e4;
        }
    }

    /* access modifiers changed from: protected */
    public void release() {
        if (this.mSound != null) {
            LogUtil.i(TAG, "[release] ");
            this.mSound.release();
            this.mSound = null;
        }
    }
}
