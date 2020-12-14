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
    public synchronized void play(int r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            android.media.MediaActionSound r0 = r3.mSound     // Catch:{ all -> 0x004a }
            if (r0 != 0) goto L_0x000e
            java.lang.String r4 = TAG     // Catch:{ all -> 0x004a }
            java.lang.String r0 = "[play] mSound is null"
            com.serenegiant.usb.LogUtil.e(r4, r0)     // Catch:{ all -> 0x004a }
            monitor-exit(r3)
            return
        L_0x000e:
            r0 = 1
            if (r4 == 0) goto L_0x0043
            r1 = 2
            if (r4 == r0) goto L_0x003d
            r0 = 3
            if (r4 == r1) goto L_0x0037
            if (r4 == r0) goto L_0x0030
            java.lang.String r0 = TAG     // Catch:{ all -> 0x004a }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x004a }
            r1.<init>()     // Catch:{ all -> 0x004a }
            java.lang.String r2 = "Unrecognized action:"
            r1.append(r2)     // Catch:{ all -> 0x004a }
            r1.append(r4)     // Catch:{ all -> 0x004a }
            java.lang.String r4 = r1.toString()     // Catch:{ all -> 0x004a }
            com.serenegiant.usb.LogUtil.w(r0, r4)     // Catch:{ all -> 0x004a }
            goto L_0x0048
        L_0x0030:
            android.media.MediaActionSound r4 = r3.mSound     // Catch:{ all -> 0x004a }
            r0 = 0
            r4.play(r0)     // Catch:{ all -> 0x004a }
            goto L_0x0048
        L_0x0037:
            android.media.MediaActionSound r4 = r3.mSound     // Catch:{ all -> 0x004a }
            r4.play(r0)     // Catch:{ all -> 0x004a }
            goto L_0x0048
        L_0x003d:
            android.media.MediaActionSound r4 = r3.mSound     // Catch:{ all -> 0x004a }
            r4.play(r1)     // Catch:{ all -> 0x004a }
            goto L_0x0048
        L_0x0043:
            android.media.MediaActionSound r4 = r3.mSound     // Catch:{ all -> 0x004a }
            r4.play(r0)     // Catch:{ all -> 0x004a }
        L_0x0048:
            monitor-exit(r3)
            return
        L_0x004a:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.common.MediaActionSoundPlayer.play(int):void");
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
