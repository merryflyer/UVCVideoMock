package com.serenegiant.encoder;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import com.serenegiant.usb.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public abstract class MediaEncoder implements Runnable {
    protected static final int MSG_FRAME_AVAILABLE = 1;
    protected static final int MSG_STOP_RECORDING = 9;
    private static final String TAG = "MediaEncoder";
    protected static final int TIMEOUT_USEC = 10000;
    private BufferInfo mBufferInfo;
    protected volatile boolean mIsCapturing;
    protected boolean mIsEOS;
    protected final MediaEncoderListener mListener;
    protected MediaCodec mMediaCodec;
    protected boolean mMuxerStarted;
    private int mRequestDrain;
    protected volatile boolean mRequestStop;
    protected final Object mSync = new Object();
    protected int mTrackIndex;
    protected final WeakReference<MediaMuxerWrapper> mWeakMuxer;
    private long prevOutputPTSUs = 0;

    public interface MediaEncoderListener {
        void onPrepared(MediaEncoder mediaEncoder);

        void onStopped(MediaEncoder mediaEncoder);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:5|6|7|8|9|10) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x003d */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MediaEncoder(com.serenegiant.encoder.MediaMuxerWrapper r3, com.serenegiant.encoder.MediaEncoder.MediaEncoderListener r4) {
        /*
            r2 = this;
            r2.<init>()
            java.lang.Object r0 = new java.lang.Object
            r0.<init>()
            r2.mSync = r0
            r0 = 0
            r2.prevOutputPTSUs = r0
            if (r4 == 0) goto L_0x004a
            if (r3 == 0) goto L_0x0042
            java.lang.ref.WeakReference r0 = new java.lang.ref.WeakReference
            r0.<init>(r3)
            r2.mWeakMuxer = r0
            r3.addEncoder(r2)
            r2.mListener = r4
            java.lang.Object r3 = r2.mSync
            monitor-enter(r3)
            android.media.MediaCodec$BufferInfo r4 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x003f }
            r4.<init>()     // Catch:{ all -> 0x003f }
            r2.mBufferInfo = r4     // Catch:{ all -> 0x003f }
            java.lang.Thread r4 = new java.lang.Thread     // Catch:{ all -> 0x003f }
            java.lang.Class r0 = r2.getClass()     // Catch:{ all -> 0x003f }
            java.lang.String r0 = r0.getSimpleName()     // Catch:{ all -> 0x003f }
            r4.<init>(r2, r0)     // Catch:{ all -> 0x003f }
            r4.start()     // Catch:{ all -> 0x003f }
            java.lang.Object r4 = r2.mSync     // Catch:{ InterruptedException -> 0x003d }
            r4.wait()     // Catch:{ InterruptedException -> 0x003d }
        L_0x003d:
            monitor-exit(r3)     // Catch:{ all -> 0x003f }
            return
        L_0x003f:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x003f }
            throw r4
        L_0x0042:
            java.lang.NullPointerException r3 = new java.lang.NullPointerException
            java.lang.String r4 = "MediaMuxerWrapper is null"
            r3.<init>(r4)
            throw r3
        L_0x004a:
            java.lang.NullPointerException r3 = new java.lang.NullPointerException
            java.lang.String r4 = "MediaEncoderListener is null"
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.encoder.MediaEncoder.<init>(com.serenegiant.encoder.MediaMuxerWrapper, com.serenegiant.encoder.MediaEncoder$MediaEncoderListener):void");
    }

    public static byte[] rotateYUV420Degree180(byte[] bArr, int i, int i2) {
        int i3 = i * i2;
        int i4 = (i3 * 3) / 2;
        byte[] bArr2 = new byte[i4];
        int i5 = 0;
        for (int i6 = i3 - 1; i6 >= 0; i6--) {
            bArr2[i5] = bArr[i6];
            i5++;
        }
        for (int i7 = i4 - 1; i7 >= i3; i7 -= 2) {
            int i8 = i5 + 1;
            bArr2[i5] = bArr[i7 - 1];
            i5 = i8 + 1;
            bArr2[i8] = bArr[i7];
        }
        return bArr2;
    }

    public static byte[] rotateYUV420Degree270(byte[] bArr, int i, int i2) {
        int i3 = i * i2;
        byte[] bArr2 = new byte[((i3 * 3) / 2)];
        int i4 = i - 1;
        int i5 = i4;
        int i6 = 0;
        while (i5 >= 0) {
            int i7 = i6;
            for (int i8 = 0; i8 < i2; i8++) {
                bArr2[i7] = bArr[(i8 * i) + i5];
                i7++;
            }
            i5--;
            i6 = i7;
        }
        int i9 = i3;
        while (i4 > 0) {
            int i10 = i9;
            for (int i11 = 0; i11 < i2 / 2; i11++) {
                int i12 = (i11 * i) + i3;
                bArr2[i10] = bArr[(i4 - 1) + i12];
                int i13 = i10 + 1;
                bArr2[i13] = bArr[i12 + i4];
                i10 = i13 + 1;
            }
            i4 -= 2;
            i9 = i10;
        }
        return bArr2;
    }

    public static byte[] rotateYUV420Degree90(byte[] bArr, int i, int i2) {
        int i3 = i * i2;
        int i4 = (i3 * 3) / 2;
        byte[] bArr2 = new byte[i4];
        int i5 = 0;
        for (int i6 = 0; i6 < i; i6++) {
            for (int i7 = i2 - 1; i7 >= 0; i7--) {
                bArr2[i5] = bArr[(i7 * i) + i6];
                i5++;
            }
        }
        int i8 = i4 - 1;
        int i9 = i - 1;
        while (i9 > 0) {
            int i10 = i8;
            for (int i11 = 0; i11 < i2 / 2; i11++) {
                int i12 = (i11 * i) + i3;
                bArr2[i10] = bArr[i12 + i9];
                int i13 = i10 - 1;
                bArr2[i13] = bArr[i12 + (i9 - 1)];
                i10 = i13 - 1;
            }
            i9 -= 2;
            i8 = i10;
        }
        return bArr2;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:32:0x007b */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drain() {
        /*
            r9 = this;
            android.media.MediaCodec r0 = r9.mMediaCodec
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            java.nio.ByteBuffer[] r0 = r0.getOutputBuffers()
            java.lang.ref.WeakReference<com.serenegiant.encoder.MediaMuxerWrapper> r1 = r9.mWeakMuxer
            java.lang.Object r1 = r1.get()
            com.serenegiant.encoder.MediaMuxerWrapper r1 = (com.serenegiant.encoder.MediaMuxerWrapper) r1
            if (r1 != 0) goto L_0x001b
            java.lang.String r0 = "MediaEncoder"
            java.lang.String r1 = "muxer is unexpectedly null"
            com.serenegiant.usb.LogUtil.w(r0, r1)
            return
        L_0x001b:
            r2 = 0
            r3 = r0
            r0 = 0
        L_0x001e:
            boolean r4 = r9.mIsCapturing
            if (r4 == 0) goto L_0x0111
            android.media.MediaCodec r4 = r9.mMediaCodec
            android.media.MediaCodec$BufferInfo r5 = r9.mBufferInfo
            r6 = 10000(0x2710, double:4.9407E-320)
            int r4 = r4.dequeueOutputBuffer(r5, r6)
            r5 = -1
            if (r4 != r5) goto L_0x003a
            boolean r4 = r9.mIsEOS
            if (r4 != 0) goto L_0x001e
            int r0 = r0 + 1
            r4 = 5
            if (r0 <= r4) goto L_0x001e
            goto L_0x0111
        L_0x003a:
            r5 = -3
            if (r4 != r5) goto L_0x004b
            java.lang.String r3 = "MediaEncoder"
            java.lang.String r4 = "INFO_OUTPUT_BUFFERS_CHANGED"
            com.serenegiant.usb.LogUtil.dv(r3, r4)
            android.media.MediaCodec r3 = r9.mMediaCodec
            java.nio.ByteBuffer[] r3 = r3.getOutputBuffers()
            goto L_0x001e
        L_0x004b:
            r5 = -2
            if (r4 != r5) goto L_0x008b
            java.lang.String r4 = "MediaEncoder"
            java.lang.String r5 = "INFO_OUTPUT_FORMAT_CHANGED"
            com.serenegiant.usb.LogUtil.dv(r4, r5)
            boolean r4 = r9.mMuxerStarted
            if (r4 != 0) goto L_0x0083
            android.media.MediaCodec r4 = r9.mMediaCodec
            android.media.MediaFormat r4 = r4.getOutputFormat()
            int r4 = r1.addTrack(r4)
            r9.mTrackIndex = r4
            r4 = 1
            r9.mMuxerStarted = r4
            boolean r4 = r1.start()
            if (r4 != 0) goto L_0x001e
            monitor-enter(r1)
        L_0x006f:
            boolean r4 = r1.isStarted()     // Catch:{ all -> 0x0080 }
            if (r4 != 0) goto L_0x007e
            r4 = 100
            r1.wait(r4)     // Catch:{ InterruptedException -> 0x007b }
            goto L_0x006f
        L_0x007b:
            monitor-exit(r1)     // Catch:{ all -> 0x0080 }
            goto L_0x0111
        L_0x007e:
            monitor-exit(r1)     // Catch:{ all -> 0x0080 }
            goto L_0x001e
        L_0x0080:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0080 }
            throw r0
        L_0x0083:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "format changed twice"
            r0.<init>(r1)
            throw r0
        L_0x008b:
            if (r4 >= 0) goto L_0x00a5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "drain:unexpected result from encoder#dequeueOutputBuffer: "
            r5.append(r6)
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            java.lang.String r5 = "MediaEncoder"
            com.serenegiant.usb.LogUtil.dw(r5, r4)
            goto L_0x001e
        L_0x00a5:
            r5 = r3[r4]
            if (r5 == 0) goto L_0x00f5
            android.media.MediaCodec$BufferInfo r6 = r9.mBufferInfo
            int r6 = r6.flags
            r6 = r6 & 2
            if (r6 == 0) goto L_0x00bc
            java.lang.String r6 = "MediaEncoder"
            java.lang.String r7 = "drain:BUFFER_FLAG_CODEC_CONFIG"
            com.serenegiant.usb.LogUtil.dd(r6, r7)
            android.media.MediaCodec$BufferInfo r6 = r9.mBufferInfo
            r6.size = r2
        L_0x00bc:
            android.media.MediaCodec$BufferInfo r6 = r9.mBufferInfo
            int r7 = r6.size
            if (r7 == 0) goto L_0x00e3
            boolean r0 = r9.mMuxerStarted
            if (r0 == 0) goto L_0x00db
            long r7 = r9.getPTSUs()
            r6.presentationTimeUs = r7
            int r0 = r9.mTrackIndex
            android.media.MediaCodec$BufferInfo r6 = r9.mBufferInfo
            r1.writeSampleData(r0, r5, r6)
            android.media.MediaCodec$BufferInfo r0 = r9.mBufferInfo
            long r5 = r0.presentationTimeUs
            r9.prevOutputPTSUs = r5
            r0 = 0
            goto L_0x00e3
        L_0x00db:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "drain:muxer hasn't started"
            r0.<init>(r1)
            throw r0
        L_0x00e3:
            android.media.MediaCodec r5 = r9.mMediaCodec
            r5.releaseOutputBuffer(r4, r2)
            android.media.MediaCodec$BufferInfo r4 = r9.mBufferInfo
            int r4 = r4.flags
            r4 = r4 & 4
            if (r4 == 0) goto L_0x001e
            r9.mIsCapturing = r2
            r9.mMuxerStarted = r2
            goto L_0x0111
        L_0x00f5:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "encoderOutputBuffer "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r2 = " was null"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0111:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.encoder.MediaEncoder.drain():void");
    }

    /* access modifiers changed from: protected */
    public void encode(byte[] bArr, int i, long j) {
        byte[] bArr2 = bArr;
        int i2 = i;
        if (this.mIsCapturing) {
            int i3 = 0;
            ByteBuffer[] inputBuffers = this.mMediaCodec.getInputBuffers();
            while (true) {
                if (!this.mIsCapturing || i3 >= i2) {
                    break;
                }
                int dequeueInputBuffer = this.mMediaCodec.dequeueInputBuffer(10000);
                if (dequeueInputBuffer >= 0) {
                    ByteBuffer byteBuffer = inputBuffers[dequeueInputBuffer];
                    byteBuffer.clear();
                    int remaining = byteBuffer.remaining();
                    if (i3 + remaining >= i2) {
                        remaining = i2 - i3;
                    }
                    int i4 = remaining;
                    if (i4 > 0 && bArr2 != null) {
                        byteBuffer.put(bArr2, i3, i4);
                    }
                    i3 += i4;
                    if (i2 <= 0) {
                        this.mIsEOS = true;
                        LogUtil.di(TAG, "send BUFFER_FLAG_END_OF_STREAM");
                        this.mMediaCodec.queueInputBuffer(dequeueInputBuffer, 0, 0, j, 4);
                        break;
                    }
                    this.mMediaCodec.queueInputBuffer(dequeueInputBuffer, 0, i4, j, 0);
                }
            }
        }
    }

    public boolean frameAvailableSoon() {
        synchronized (this.mSync) {
            if (this.mIsCapturing) {
                if (!this.mRequestStop) {
                    this.mRequestDrain++;
                    this.mSync.notifyAll();
                    return true;
                }
            }
            return false;
        }
    }

    public String getOutputPath() {
        MediaMuxerWrapper mediaMuxerWrapper = (MediaMuxerWrapper) this.mWeakMuxer.get();
        if (mediaMuxerWrapper != null) {
            return mediaMuxerWrapper.getOutputPath();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public long getPTSUs() {
        long nanoTime = System.nanoTime() / 1000;
        long j = this.prevOutputPTSUs;
        return nanoTime < j ? nanoTime + (j - nanoTime) : nanoTime;
    }

    /* access modifiers changed from: 0000 */
    public abstract void prepare() throws IOException;

    /* access modifiers changed from: protected */
    public void release() {
        String str = TAG;
        LogUtil.dv(str, "release:");
        try {
            this.mListener.onStopped(this);
        } catch (Exception e) {
            LogUtil.e(str, "failed onStopped", e);
        }
        this.mIsCapturing = false;
        MediaCodec mediaCodec = this.mMediaCodec;
        if (mediaCodec != null) {
            try {
                mediaCodec.stop();
                this.mMediaCodec.release();
                this.mMediaCodec = null;
            } catch (Exception e2) {
                LogUtil.e(str, "failed releasing MediaCodec", e2);
            }
        }
        if (this.mMuxerStarted) {
            MediaMuxerWrapper mediaMuxerWrapper = (MediaMuxerWrapper) this.mWeakMuxer.get();
            if (mediaMuxerWrapper != null) {
                try {
                    mediaMuxerWrapper.stop();
                } catch (Exception e3) {
                    LogUtil.e(str, "failed stopping muxer", e3);
                }
            }
        }
        this.mBufferInfo = null;
    }

    public void run() {
        boolean z;
        boolean z2;
        synchronized (this.mSync) {
            this.mRequestStop = false;
            this.mRequestDrain = 0;
            this.mSync.notify();
        }
        while (true) {
            synchronized (this.mSync) {
                z = this.mRequestStop;
                z2 = this.mRequestDrain > 0;
                if (z2) {
                    this.mRequestDrain--;
                }
            }
            if (z) {
                drain();
                signalEndOfInputStream();
                drain();
                release();
                break;
            } else if (z2) {
                drain();
            } else {
                synchronized (this.mSync) {
                    try {
                        this.mSync.wait();
                        try {
                        } finally {
                            while (true) {
                            }
                        }
                    } catch (InterruptedException unused) {
                        LogUtil.dv(TAG, "Encoder thread exiting");
                        synchronized (this.mSync) {
                            this.mRequestStop = true;
                            this.mIsCapturing = false;
                        }
                        return;
                    }
                }
            }
        }
        while (true) {
        }
    }

    /* access modifiers changed from: protected */
    public void signalEndOfInputStream() {
        LogUtil.dv(TAG, "sending EOS to encoder");
        encode(null, 0, getPTSUs());
    }

    /* access modifiers changed from: 0000 */
    public void startRecording() {
        LogUtil.dv(TAG, "startRecording");
        synchronized (this.mSync) {
            this.mIsCapturing = true;
            this.mRequestStop = false;
            this.mSync.notifyAll();
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void stopRecording() {
        /*
            r2 = this;
            java.lang.String r0 = "MediaEncoder"
            java.lang.String r1 = "stopRecording"
            com.serenegiant.usb.LogUtil.dv(r0, r1)
            java.lang.Object r0 = r2.mSync
            monitor-enter(r0)
            boolean r1 = r2.mIsCapturing     // Catch:{ all -> 0x001f }
            if (r1 == 0) goto L_0x001d
            boolean r1 = r2.mRequestStop     // Catch:{ all -> 0x001f }
            if (r1 == 0) goto L_0x0013
            goto L_0x001d
        L_0x0013:
            r1 = 1
            r2.mRequestStop = r1     // Catch:{ all -> 0x001f }
            java.lang.Object r1 = r2.mSync     // Catch:{ all -> 0x001f }
            r1.notifyAll()     // Catch:{ all -> 0x001f }
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return
        L_0x001f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.encoder.MediaEncoder.stopRecording():void");
    }

    /* access modifiers changed from: protected */
    public void encode(ByteBuffer byteBuffer, int i, long j, boolean z) {
        ByteBuffer byteBuffer2 = byteBuffer;
        int i2 = i;
        if (this.mIsCapturing) {
            int i3 = 0;
            ByteBuffer[] inputBuffers = this.mMediaCodec.getInputBuffers();
            while (true) {
                if (!this.mIsCapturing || i3 >= i2) {
                    break;
                }
                int dequeueInputBuffer = this.mMediaCodec.dequeueInputBuffer(10000);
                if (dequeueInputBuffer >= 0) {
                    ByteBuffer byteBuffer3 = inputBuffers[dequeueInputBuffer];
                    byteBuffer3.clear();
                    int remaining = byteBuffer3.remaining();
                    if (i3 + remaining >= i2) {
                        remaining = i2 - i3;
                    }
                    int i4 = remaining;
                    if (i4 > 0 && byteBuffer2 != null) {
                        byteBuffer2.position(i3 + i4);
                        byteBuffer.flip();
                        byteBuffer3.put(byteBuffer2);
                    }
                    i3 += i4;
                    if (i2 <= 0) {
                        this.mIsEOS = true;
                        LogUtil.di(TAG, "send BUFFER_FLAG_END_OF_STREAM");
                        this.mMediaCodec.queueInputBuffer(dequeueInputBuffer, 0, 0, j, 4);
                        break;
                    }
                    this.mMediaCodec.queueInputBuffer(dequeueInputBuffer, 0, i4, j, 0);
                }
            }
        }
    }
}
