package com.serenegiant.encoder;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import com.serenegiant.encoder.MediaEncoder.MediaEncoderListener;
import com.serenegiant.usb.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MediaAudioEncoder extends MediaEncoder implements IAudioEncoder {
    /* access modifiers changed from: private */
    public static final int[] AUDIO_SOURCES = {0, 1, 5};
    private static final int BIT_RATE = 64000;
    public static final int FRAMES_PER_BUFFER = 25;
    private static final String MIME_TYPE = "audio/mp4a-latm";
    public static final int SAMPLES_PER_FRAME = 1024;
    private static final int SAMPLE_RATE = 44100;
    private static final String TAG = "MediaAudioEncoder";
    private AudioThread mAudioThread = null;

    private class AudioThread extends Thread {
        private AudioThread() {
        }

        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* JADX WARNING: Missing exception handler attribute for start block: B:69:0x00fb */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x00cf A[LOOP:2: B:57:0x00cf->B:71:0x00fc, LOOP_START, PHI: r14 
          PHI: (r14v1 int) = (r14v0 int), (r14v2 int) binds: [B:56:0x00cd, B:71:0x00fc] A[DONT_GENERATE, DONT_INLINE]] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r17 = this;
                r1 = r17
                r0 = -16
                android.os.Process.setThreadPriority(r0)
                r0 = 2
                r2 = 44100(0xac44, float:6.1797E-41)
                r3 = 16
                int r2 = android.media.AudioRecord.getMinBufferSize(r2, r3, r0)
                r3 = 1
                r4 = 1024(0x400, float:1.435E-42)
                r5 = 25600(0x6400, float:3.5873E-41)
                if (r5 >= r2) goto L_0x001e
                int r2 = r2 / r4
                int r2 = r2 + r3
                int r2 = r2 * 1024
                int r5 = r2 * 2
            L_0x001e:
                java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r4)
                java.nio.ByteOrder r2 = java.nio.ByteOrder.nativeOrder()
                java.nio.ByteBuffer r2 = r0.order(r2)
                int[] r0 = com.serenegiant.encoder.MediaAudioEncoder.AUDIO_SOURCES
                int r12 = r0.length
                r13 = 0
                r14 = 0
                r6 = r13
                r15 = 0
            L_0x0033:
                if (r15 >= r12) goto L_0x005a
                r7 = r0[r15]
                android.media.AudioRecord r16 = new android.media.AudioRecord     // Catch:{ Exception -> 0x0053 }
                r8 = 44100(0xac44, float:6.1797E-41)
                r9 = 16
                r10 = 2
                r6 = r16
                r11 = r5
                r6.<init>(r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0053 }
                int r6 = r16.getState()     // Catch:{ Exception -> 0x0053 }
                if (r6 == r3) goto L_0x0050
                r16.release()     // Catch:{ Exception -> 0x0053 }
                r16 = r13
            L_0x0050:
                r6 = r16
                goto L_0x0054
            L_0x0053:
                r6 = r13
            L_0x0054:
                if (r6 == 0) goto L_0x0057
                goto L_0x005a
            L_0x0057:
                int r15 = r15 + 1
                goto L_0x0033
            L_0x005a:
                r3 = r6
                if (r3 == 0) goto L_0x00cc
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ Exception -> 0x00be }
                boolean r0 = r0.mIsCapturing     // Catch:{ Exception -> 0x00be }
                if (r0 == 0) goto L_0x00b7
                java.lang.String r0 = "MediaAudioEncoder"
                java.lang.String r5 = "AudioThread:start audio recording"
                com.serenegiant.usb.LogUtil.dv(r0, r5)     // Catch:{ Exception -> 0x00be }
                r3.startRecording()     // Catch:{ Exception -> 0x00be }
                r5 = 0
            L_0x006e:
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ all -> 0x00b0 }
                boolean r0 = r0.mIsCapturing     // Catch:{ all -> 0x00b0 }
                if (r0 == 0) goto L_0x00a5
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ all -> 0x00b0 }
                boolean r0 = r0.mRequestStop     // Catch:{ all -> 0x00b0 }
                if (r0 != 0) goto L_0x00a5
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ all -> 0x00b0 }
                boolean r0 = r0.mIsEOS     // Catch:{ all -> 0x00b0 }
                if (r0 != 0) goto L_0x00a5
                r2.clear()     // Catch:{ all -> 0x00b0 }
                int r8 = r3.read(r2, r4)     // Catch:{ Exception -> 0x00a4 }
                if (r8 <= 0) goto L_0x006e
                r2.position(r8)     // Catch:{ all -> 0x00b0 }
                r2.flip()     // Catch:{ all -> 0x00b0 }
                com.serenegiant.encoder.MediaAudioEncoder r6 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ all -> 0x00b0 }
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ all -> 0x00b0 }
                long r9 = r0.getPTSUs()     // Catch:{ all -> 0x00b0 }
                r11 = 0
                r7 = r2
                r6.encode(r7, r8, r9, r11)     // Catch:{ all -> 0x00b0 }
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ all -> 0x00b0 }
                r0.frameAvailableSoon()     // Catch:{ all -> 0x00b0 }
                int r5 = r5 + 1
                goto L_0x006e
            L_0x00a4:
            L_0x00a5:
                if (r5 <= 0) goto L_0x00ac
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ all -> 0x00b0 }
                r0.frameAvailableSoon()     // Catch:{ all -> 0x00b0 }
            L_0x00ac:
                r3.stop()     // Catch:{ Exception -> 0x00b5 }
                goto L_0x00b8
            L_0x00b0:
                r0 = move-exception
                r3.stop()     // Catch:{ Exception -> 0x00b5 }
                throw r0     // Catch:{ Exception -> 0x00b5 }
            L_0x00b5:
                r0 = move-exception
                goto L_0x00c0
            L_0x00b7:
                r5 = 0
            L_0x00b8:
                r3.release()
                goto L_0x00cd
            L_0x00bc:
                r0 = move-exception
                goto L_0x00c8
            L_0x00be:
                r0 = move-exception
                r5 = 0
            L_0x00c0:
                java.lang.String r6 = "MediaAudioEncoder"
                java.lang.String r7 = "AudioThread#run"
                com.serenegiant.usb.LogUtil.e(r6, r7, r0)     // Catch:{ all -> 0x00bc }
                goto L_0x00b8
            L_0x00c8:
                r3.release()
                throw r0
            L_0x00cc:
                r5 = 0
            L_0x00cd:
                if (r5 != 0) goto L_0x0101
            L_0x00cf:
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this
                boolean r0 = r0.mIsCapturing
                if (r0 == 0) goto L_0x0101
                r0 = 5
                if (r14 >= r0) goto L_0x0101
                r2.position(r4)
                r2.flip()
                com.serenegiant.encoder.MediaAudioEncoder r6 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ Exception -> 0x0101 }
                r8 = 1024(0x400, float:1.435E-42)
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ Exception -> 0x0101 }
                long r9 = r0.getPTSUs()     // Catch:{ Exception -> 0x0101 }
                r11 = 0
                r7 = r2
                r6.encode(r7, r8, r9, r11)     // Catch:{ Exception -> 0x0101 }
                com.serenegiant.encoder.MediaAudioEncoder r0 = com.serenegiant.encoder.MediaAudioEncoder.this     // Catch:{ Exception -> 0x0101 }
                r0.frameAvailableSoon()     // Catch:{ Exception -> 0x0101 }
                monitor-enter(r17)
                r5 = 50
                r1.wait(r5)     // Catch:{ InterruptedException -> 0x00fb }
                goto L_0x00fb
            L_0x00f9:
                r0 = move-exception
                goto L_0x00ff
            L_0x00fb:
                monitor-exit(r17)     // Catch:{ all -> 0x00f9 }
                int r14 = r14 + 1
                goto L_0x00cf
            L_0x00ff:
                monitor-exit(r17)     // Catch:{ all -> 0x00f9 }
                throw r0
            L_0x0101:
                java.lang.String r0 = "MediaAudioEncoder"
                java.lang.String r2 = "AudioThread:finished"
                com.serenegiant.usb.LogUtil.dv(r0, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.encoder.MediaAudioEncoder.AudioThread.run():void");
        }
    }


    public MediaAudioEncoder(MediaMuxerWrapper mediaMuxerWrapper, MediaEncoderListener mediaEncoderListener) {
        super(mediaMuxerWrapper, mediaEncoderListener);
    }

    private static final MediaCodecInfo selectAudioCodec(String str) {
        String str2 = TAG;
        LogUtil.dv(str2, "selectAudioCodec:");
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
            if (codecInfoAt.isEncoder()) {
                String[] supportedTypes = codecInfoAt.getSupportedTypes();
                for (int i2 = 0; i2 < supportedTypes.length; i2++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("supportedType:");
                    sb.append(codecInfoAt.getName());
                    sb.append(",MIME=");
                    sb.append(supportedTypes[i2]);
                    LogUtil.di(str2, sb.toString());
                    if (supportedTypes[i2].equalsIgnoreCase(str)) {
                        return codecInfoAt;
                    }
                }
                continue;
            }
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void calc1(short[] sArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = i3 + i;
            sArr[i4] = (short) (sArr[i4] >> 1);
        }
    }

    /* access modifiers changed from: protected */
    public void prepare() {
        String str = "prepare:";
        String str2 = TAG;
        LogUtil.dv(str2, str);
        this.mTrackIndex = -1;
        this.mIsEOS = false;
        this.mMuxerStarted = false;
        String str3 = MIME_TYPE;
        MediaCodecInfo selectAudioCodec = selectAudioCodec(str3);
        if (selectAudioCodec == null) {
            LogUtil.e(str2, "Unable to find an appropriate codec for audio/mp4a-latm");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("selected codec: ");
        sb.append(selectAudioCodec.getName());
        LogUtil.di(str2, sb.toString());
        MediaFormat createAudioFormat = MediaFormat.createAudioFormat(str3, SAMPLE_RATE, 1);
        createAudioFormat.setInteger("aac-profile", 2);
        createAudioFormat.setInteger("channel-mask", 16);
        createAudioFormat.setInteger("bitrate", BIT_RATE);
        createAudioFormat.setInteger("channel-count", 1);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("format: ");
        sb2.append(createAudioFormat);
        LogUtil.di(str2, sb2.toString());
        try {
            this.mMediaCodec = MediaCodec.createEncoderByType(str3);
            this.mMediaCodec.configure(createAudioFormat, null, null, 1);
            this.mMediaCodec.start();
            LogUtil.di(str2, "prepare finishing");
            MediaEncoderListener mediaEncoderListener = this.mListener;
            if (mediaEncoderListener != null) {
                try {
                    mediaEncoderListener.onPrepared(this);
                } catch (Exception e) {
                    LogUtil.e(str2, str, e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void release() {
        this.mAudioThread = null;
        super.release();
    }

    /* access modifiers changed from: protected */
    public void startRecording() {
        super.startRecording();
        if (this.mAudioThread == null) {
            this.mAudioThread = new AudioThread();
            this.mAudioThread.start();
        }
    }
}
