package com.serenegiant.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import com.serenegiant.encoder.MediaEncoder.MediaEncoderListener;
import com.serenegiant.usb.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaVideoBufferEncoder extends MediaEncoder implements IVideoEncoder {
    private static final float BPP = 0.5f;
    private static final int FRAME_RATE = 15;
    private static final String MIME_TYPE = "video/avc";
    private static final String TAG = "MediaVideoBufferEncoder";
    protected static int[] recognizedFormats = {21};
    protected int mColorFormat;
    private final int mHeight;
    private final int mOrientation;
    private final int mWidth;

    public MediaVideoBufferEncoder(MediaMuxerWrapper mediaMuxerWrapper, int i, int i2, int i3, MediaEncoderListener mediaEncoderListener) {
        super(mediaMuxerWrapper, mediaEncoderListener);
        LogUtil.di(TAG, "MediaVideoEncoder: ");
        this.mWidth = i;
        this.mHeight = i2;
        this.mOrientation = i3;
    }

    private int calcBitRate() {
        int i = (int) (((float) this.mWidth) * 7.5f * ((float) this.mHeight));
        LogUtil.i(TAG, String.format("bitrate=%5.2f[Mbps]", new Object[]{Float.valueOf((((float) i) / 1024.0f) / 1024.0f)}));
        return i;
    }

    private static final boolean isRecognizedViewFormat(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("isRecognizedViewFormat:colorFormat=");
        sb.append(i);
        LogUtil.di(TAG, sb.toString());
        int[] iArr = recognizedFormats;
        int length = iArr != null ? iArr.length : 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (recognizedFormats[i2] == i) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: finally extract failed */
    protected static final int selectColorFormat(MediaCodecInfo mediaCodecInfo, String str) {
        String str2 = TAG;
        LogUtil.di(str2, "selectColorFormat: ");
        try {
            Thread.currentThread().setPriority(10);
            CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(str);
            Thread.currentThread().setPriority(5);
            int i = 0;
            int i2 = 0;
            while (true) {
                int[] iArr = capabilitiesForType.colorFormats;
                if (i2 >= iArr.length) {
                    break;
                }
                int i3 = iArr[i2];
                if (isRecognizedViewFormat(i3)) {
                    i = i3;
                    break;
                }
                i2++;
            }
            if (i == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("couldn't find a good color format for ");
                sb.append(mediaCodecInfo.getName());
                sb.append(" / ");
                sb.append(str);
                LogUtil.e(str2, sb.toString());
            }
            return i;
        } catch (Throwable th) {
            Thread.currentThread().setPriority(5);
            throw th;
        }
    }

    public void encode(ByteBuffer byteBuffer) {
        synchronized (this.mSync) {
            if (this.mIsCapturing) {
                if (!this.mRequestStop) {
                    encode(byteBuffer, byteBuffer.capacity(), getPTSUs(), true);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void prepare() {
        MediaFormat mediaFormat;
        String str = TAG;
        LogUtil.di(str, "prepare: ");
        this.mTrackIndex = -1;
        this.mIsEOS = false;
        this.mMuxerStarted = false;
        String str2 = MIME_TYPE;
        MediaCodecInfo selectVideoCodec = selectVideoCodec(str2);
        if (selectVideoCodec == null) {
            LogUtil.e(str, "Unable to find an appropriate codec for video/avc");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("selected codec: ");
        sb.append(selectVideoCodec.getName());
        LogUtil.di(str, sb.toString());
        int i = this.mOrientation;
        if (i == 0 || i == 180) {
            mediaFormat = MediaFormat.createVideoFormat(str2, this.mHeight, this.mWidth);
        } else {
            mediaFormat = MediaFormat.createVideoFormat(str2, this.mWidth, this.mHeight);
        }
        mediaFormat.setInteger("color-format", this.mColorFormat);
        mediaFormat.setInteger("bitrate", calcBitRate());
        mediaFormat.setInteger("frame-rate", 15);
        mediaFormat.setInteger("i-frame-interval", 10);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("format: ");
        sb2.append(mediaFormat);
        LogUtil.di(str, sb2.toString());
        try {
            this.mMediaCodec = MediaCodec.createEncoderByType(str2);
            this.mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            this.mMediaCodec.start();
            LogUtil.di(str, "prepare finishing");
            MediaEncoderListener mediaEncoderListener = this.mListener;
            if (mediaEncoderListener != null) {
                try {
                    mediaEncoderListener.onPrepared(this);
                } catch (Exception e) {
                    LogUtil.e(str, "prepare:", e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* access modifiers changed from: protected */
    public final MediaCodecInfo selectVideoCodec(String str) {
        String str2 = TAG;
        LogUtil.dv(str2, "selectVideoCodec:");
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
            if (codecInfoAt.isEncoder()) {
                String[] supportedTypes = codecInfoAt.getSupportedTypes();
                for (int i2 = 0; i2 < supportedTypes.length; i2++) {
                    if (supportedTypes[i2].equalsIgnoreCase(str)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("codec:");
                        sb.append(codecInfoAt.getName());
                        sb.append(",MIME=");
                        sb.append(supportedTypes[i2]);
                        LogUtil.di(str2, sb.toString());
                        int selectColorFormat = selectColorFormat(codecInfoAt, str);
                        if (selectColorFormat > 0) {
                            this.mColorFormat = selectColorFormat;
                            return codecInfoAt;
                        }
                    }
                }
                continue;
            }
        }
        return null;
    }
}
