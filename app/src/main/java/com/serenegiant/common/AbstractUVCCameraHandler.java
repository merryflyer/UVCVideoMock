package com.serenegiant.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.serenegiant.common.VideoFileSizeObserver.OnRerecordListener;
import com.serenegiant.encoder.MediaAudioEncoder;
import com.serenegiant.encoder.MediaEncoder;
import com.serenegiant.encoder.MediaEncoder.MediaEncoderListener;
import com.serenegiant.encoder.MediaMuxerWrapper;
import com.serenegiant.encoder.MediaSurfaceEncoder;
import com.serenegiant.encoder.MediaVideoBufferEncoder;
import com.serenegiant.encoder.MediaVideoEncoder;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.LogUtil;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.widget.CameraViewInterface;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.List;

public abstract class AbstractUVCCameraHandler extends Handler {
    private static final int FILE_ERROR = -2;
    private static final int INVALID_DURATION = -1;
    private static final int MSG_CAPTURE_START = 5;
    private static final int MSG_CAPTURE_STILL = 4;
    private static final int MSG_CAPTURE_STOP = 6;
    private static final int MSG_CLOSE = 1;
    private static final int MSG_MEDIA_UPDATE = 7;
    private static final int MSG_OPEN = 0;
    private static final int MSG_PREVIEW_START = 2;
    private static final int MSG_PREVIEW_STOP = 3;
    private static final int MSG_RELEASE = 9;
    private static final int MSG_SET_PREVIEW_SIZE = 10;
    private static final String TAG = "UVCCameraHandler";
    /* access modifiers changed from: private */
    public volatile boolean mReleased;
    private final WeakReference<CameraThread> mWeakThread;

    static final class CameraThread extends Thread {
        private static final String TAG_THREAD = "CameraThread";
        private float mBandwidthFactor;
        /* access modifiers changed from: private */
        public OnScanCompletedListener mCompletedListener;
        private final int mEncoderType;
        /* access modifiers changed from: private */
        public AbstractUVCCameraHandler mHandler;
        private final Class<? extends AbstractUVCCameraHandler> mHandlerClass;
        private int mHeight;
        private final IFrameCallback mIFrameCallback = new IFrameCallback() {
            public void onFrame(ByteBuffer byteBuffer) {
                MediaVideoBufferEncoder access$500;
                synchronized (CameraThread.this.mSync) {
                    access$500 = CameraThread.this.mVideoEncoder;
                }
                if (access$500 != null) {
                    access$500.frameAvailableSoon();
                    byte[] bArr = new byte[byteBuffer.remaining()];
                    byte[] bArr2 = new byte[byteBuffer.remaining()];
                    if (CameraThread.this.mRecordingOrientation == 0) {
                        byteBuffer.get(bArr);
                        byte[] rotateYUV420Degree90 = MediaEncoder.rotateYUV420Degree90(bArr, CameraThread.this.getWidth(), CameraThread.this.getHeight());
                        System.arraycopy(rotateYUV420Degree90, 0, bArr2, 0, rotateYUV420Degree90.length);
                        access$500.encode(ByteBuffer.wrap(bArr2));
                    } else if (CameraThread.this.mRecordingOrientation == 90) {
                        byteBuffer.get(bArr);
                        byte[] rotateYUV420Degree180 = MediaEncoder.rotateYUV420Degree180(bArr, CameraThread.this.getWidth(), CameraThread.this.getHeight());
                        System.arraycopy(rotateYUV420Degree180, 0, bArr2, 0, rotateYUV420Degree180.length);
                        access$500.encode(ByteBuffer.wrap(bArr2));
                    } else if (CameraThread.this.mRecordingOrientation == 180) {
                        byteBuffer.get(bArr);
                        byte[] rotateYUV420Degree270 = MediaEncoder.rotateYUV420Degree270(bArr, CameraThread.this.getWidth(), CameraThread.this.getHeight());
                        System.arraycopy(rotateYUV420Degree270, 0, bArr2, 0, rotateYUV420Degree270.length);
                        access$500.encode(ByteBuffer.wrap(bArr2));
                    } else {
                        access$500.encode(byteBuffer);
                    }
                }
            }
        };
        private boolean mIsPreviewing;
        /* access modifiers changed from: private */
        public boolean mIsRecording;
        private final MediaEncoderListener mMediaEncoderListener = new MediaEncoderListener() {
            public void onPrepared(MediaEncoder mediaEncoder) {
                StringBuilder sb = new StringBuilder();
                sb.append("onPrepared:encoder=");
                sb.append(mediaEncoder);
                String sb2 = sb.toString();
                String str = CameraThread.TAG_THREAD;
                LogUtil.dv(str, sb2);
                CameraThread.this.mIsRecording = true;
                String str2 = "onPrepared:";
                if (mediaEncoder instanceof MediaVideoEncoder) {
                    try {
                        ((CameraViewInterface) CameraThread.this.mWeakCameraView.get()).setVideoEncoder((MediaVideoEncoder) mediaEncoder);
                    } catch (Exception e) {
                        LogUtil.e(str, str2, e);
                    }
                }
                if (mediaEncoder instanceof MediaSurfaceEncoder) {
                    try {
                        ((CameraViewInterface) CameraThread.this.mWeakCameraView.get()).setVideoEncoder((MediaSurfaceEncoder) mediaEncoder);
                        CameraThread.this.mUVCCamera.startCapture(((MediaSurfaceEncoder) mediaEncoder).getInputSurface());
                    } catch (Exception e2) {
                        LogUtil.e(str, str2, e2);
                    }
                }
            }

            @SuppressLint({"NewApi"})
            public void onStopped(MediaEncoder mediaEncoder) {
                StringBuilder sb = new StringBuilder();
                sb.append("onStopped:encoder=");
                sb.append(mediaEncoder);
                LogUtil.dv(CameraThread.TAG_THREAD, sb.toString());
                boolean z = false;
                CameraThread.this.mIsRecording = false;
                try {
                    Activity activity = (Activity) CameraThread.this.mWeakParent.get();
                    if ((mediaEncoder instanceof MediaVideoEncoder) || (mediaEncoder instanceof MediaSurfaceEncoder)) {
                        ((CameraViewInterface) CameraThread.this.mWeakCameraView.get()).setVideoEncoder(null);
                        synchronized (CameraThread.this.mSync) {
                            if (CameraThread.this.mUVCCamera != null) {
                                CameraThread.this.mUVCCamera.stopCapture();
                            }
                        }
                    }
                    String outputPath = mediaEncoder.getOutputPath();
                    if (TextUtils.isEmpty(outputPath) || !(mediaEncoder instanceof MediaVideoBufferEncoder)) {
                        if (CameraThread.this.mHandler == null || CameraThread.this.mHandler.mReleased) {
                            z = true;
                        }
                        if (z || activity == null || activity.isDestroyed()) {
                            CameraThread.this.handleRelease();
                            return;
                        }
                        return;
                    }
                    CameraThread.this.handleUpdateMedia(outputPath, true);
                } catch (Exception e) {
                    LogUtil.e(CameraThread.TAG_THREAD, "onPrepared:", e);
                }
            }
        };
        private MediaMuxerWrapper mMuxer;
        /* access modifiers changed from: private */
        public OnRerecordListener mOnRerecordListener;
        private final IFrameCallback mPreviewFrameCallback = new IFrameCallback() {
            public void onFrame(ByteBuffer byteBuffer) {
                byte[] bArr = new byte[byteBuffer.remaining()];
                byteBuffer.get(bArr);
                CameraThread.this.mUVCCamera.setFrameCallback(null, 0);
                if (CameraThread.this.mPreviewListener != null) {
                    CameraThread.this.mPreviewListener.onPreviewResult(bArr);
                }
            }
        };
        /* access modifiers changed from: private */
        public OnPreViewResultListener mPreviewListener;
        private int mPreviewMode;
        /* access modifiers changed from: private */
        public int mRecordingOrientation = -1;
        android.media.MediaScannerConnection.OnScanCompletedListener mScanCaptureListener = new android.media.MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String str, Uri uri) {
                Activity activity = (Activity) CameraThread.this.mWeakParent.get();
                if (activity != null && activity.getApplicationContext() != null) {
                    if (CameraThread.this.mCompletedListener != null) {
                        CameraThread.this.mCompletedListener.onScanCompleted(null, uri, str, false);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("onScanCompleted:Uri = ");
                    sb.append(uri);
                    LogUtil.dv(CameraThread.TAG_THREAD, sb.toString());
                }
            }
        };
        android.media.MediaScannerConnection.OnScanCompletedListener mScanVideoListener = new android.media.MediaScannerConnection.OnScanCompletedListener() {
            @TargetApi(16)
            public void onScanCompleted(String str, Uri uri) {
                Activity activity = (Activity) CameraThread.this.mWeakParent.get();
                if (activity != null && activity.getApplicationContext() != null) {
                    ContentResolver contentResolver = activity.getApplicationContext().getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    StringBuilder sb = new StringBuilder();
                    sb.append(CameraThread.this.getWidth());
                    sb.append("x");
                    sb.append(CameraThread.this.getHeight());
                    contentValues.put("resolution", sb.toString());
                    contentValues.put("width", Integer.valueOf(CameraThread.this.getWidth()));
                    contentValues.put("height", Integer.valueOf(CameraThread.this.getHeight()));
                    contentValues.put("duration", Long.valueOf(CameraThread.this.getDuration(str)));
                    int update = contentResolver.update(uri, contentValues, null, null);
                    if (CameraThread.this.mCompletedListener != null) {
                        CameraThread.this.mCompletedListener.onScanCompleted(null, uri, str, true);
                    }
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("onScanCompleted:isOK = ");
                    sb2.append(update);
                    sb2.append(", Uri = ");
                    sb2.append(uri);
                    LogUtil.dv(CameraThread.TAG_THREAD, sb2.toString());
                }
            }
        };
        /* access modifiers changed from: private */
        public final Object mSync = new Object();
        /* access modifiers changed from: private */
        public UVCCamera mUVCCamera;
        /* access modifiers changed from: private */
        public MediaVideoBufferEncoder mVideoEncoder;
        private VideoFileSizeObserver mVideoFileSizeObserver;
        /* access modifiers changed from: private */
        public final WeakReference<CameraViewInterface> mWeakCameraView;
        /* access modifiers changed from: private */
        public final WeakReference<Activity> mWeakParent;
        private int mWidth;

        CameraThread(Class<? extends AbstractUVCCameraHandler> cls, Activity activity, CameraViewInterface cameraViewInterface, int i, int i2, int i3, int i4, float f) {
            super(TAG_THREAD);
            this.mHandlerClass = cls;
            this.mEncoderType = i;
            this.mWidth = i2;
            this.mHeight = i3;
            this.mPreviewMode = i4;
            this.mBandwidthFactor = f;
            this.mWeakParent = new WeakReference<>(activity);
            this.mWeakCameraView = new WeakReference<>(cameraViewInterface);
        }

        /* access modifiers changed from: private */
        public long getDuration(String str) {
            long j;
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            try {
                mediaMetadataRetriever.setDataSource(str);
                long longValue = Long.valueOf(mediaMetadataRetriever.extractMetadata(9)).longValue();
                mediaMetadataRetriever.release();
                return longValue;
            } catch (IllegalArgumentException unused) {
                j = -1;
                mediaMetadataRetriever.release();
                return j;
            } catch (RuntimeException unused2) {
                j = -2;
                mediaMetadataRetriever.release();
                return j;
            } catch (Throwable th) {
                mediaMetadataRetriever.release();
                throw th;
            }
        }

        /* access modifiers changed from: protected */
        public void finalize() {
            LogUtil.i(TAG_THREAD, "CameraThread#finalize");
            try {
                super.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(5:2|3|(2:5|6)|7|8) */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0013 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.serenegiant.common.AbstractUVCCameraHandler getHandler() {
            Log.v(TAG_THREAD, "getHandler:");
            synchronized (mSync) {
                if (mHandler == null)
                    try {
                        mSync.wait();
                    } catch (final InterruptedException e) {
                    }
            }
            return mHandler;
        }

        public int getHeight() {
            int i;
            synchronized (this.mSync) {
                i = this.mHeight;
            }
            return i;
        }

        public int getWidth() {
            int i;
            synchronized (this.mSync) {
                i = this.mWidth;
            }
            return i;
        }

        @TargetApi(19)
        public void handleCaptureStill(int i) {
            BufferedOutputStream bufferedOutputStream = null;
            Throwable th;
            StringBuilder sb = new StringBuilder();
            sb.append("handleCaptureStill: mCompletedListener = ");
            sb.append(this.mCompletedListener);
            LogUtil.dv(TAG_THREAD, sb.toString());
            if (((Activity) this.mWeakParent.get()) != null) {
                try {
                    Bitmap captureStillImage = ((CameraViewInterface) this.mWeakCameraView.get()).captureStillImage();
                    File captureFile = MediaMuxerWrapper.getCaptureFile(Environment.DIRECTORY_DCIM, ".png");
                    try {
                        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(captureFile));
                        captureStillImage.compress(CompressFormat.PNG, 100, bufferedOutputStream);
                        bufferedOutputStream.flush();
                        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, captureFile.getPath()));
                        if (this.mCompletedListener != null) {
                            this.mCompletedListener.onScanCompleted(captureStillImage, null, captureFile.getPath(), false);
                        }
                    } catch (IOException unused) {
                    } catch (Throwable th2) {
                        captureStillImage.recycle();
                        throw th2;
                    }
                    bufferedOutputStream.close();
                    captureStillImage.recycle();
                } catch (Exception unused2) {
                }
                return;
            }
            return;
//            throw th; todo 屏蔽
        }

        public void handleClose() {
            UVCCamera uVCCamera;
            LogUtil.dv(TAG_THREAD, "handleClose:");
            handleStopRecording();
            synchronized (this.mSync) {
                uVCCamera = this.mUVCCamera;
                this.mUVCCamera = null;
            }
            if (uVCCamera != null) {
                LogUtil.dv(TAG_THREAD, "handleClose: destroy");
                uVCCamera.stopPreview();
                uVCCamera.destroy();
            }
        }

        public void handleOpen(UsbControlBlock usbControlBlock) {
            LogUtil.dv(TAG_THREAD, "handleOpen:");
            handleClose();
            try {
                UVCCamera uVCCamera = new UVCCamera();
                uVCCamera.open(usbControlBlock);
                synchronized (this.mSync) {
                    this.mUVCCamera = uVCCamera;
                }
                if (this.mPreviewListener != null) {
                    this.mPreviewListener.onOpen(uVCCamera);
                }
            } catch (Exception unused) {
            }
            StringBuilder sb = new StringBuilder();
            sb.append("supportedSize:");
            UVCCamera uVCCamera2 = this.mUVCCamera;
            sb.append(uVCCamera2 != null ? uVCCamera2.getSupportedSize() : null);
            LogUtil.di(TAG_THREAD, sb.toString());
        }

        public void handleRelease() {
            StringBuilder sb = new StringBuilder();
            sb.append("handleRelease:mIsRecording=");
            sb.append(this.mIsRecording);
            String sb2 = sb.toString();
            String str = TAG_THREAD;
            LogUtil.dv(str, sb2);
            handleClose();
            if (!this.mIsRecording) {
                this.mHandler.mReleased = true;
                Looper.myLooper().quit();
            }
            LogUtil.dv(str, "handleRelease:finished");
        }

        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0025 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleSetPreviewSize(java.lang.Object r11, int r12, int r13) {
            /*
                r10 = this;
                java.lang.String r0 = "CameraThread"
                java.lang.String r1 = "handleSetPreviewSize:"
                com.serenegiant.usb.LogUtil.dv(r0, r1)
                r10.mWidth = r12
                r10.mHeight = r13
                com.serenegiant.usb.UVCCamera r2 = r10.mUVCCamera
                if (r2 != 0) goto L_0x0010
                return
            L_0x0010:
                r5 = 1
                r6 = 30
                int r7 = r10.mPreviewMode     // Catch:{ IllegalArgumentException -> 0x0025 }
                float r8 = r10.mBandwidthFactor     // Catch:{ IllegalArgumentException -> 0x0025 }
                r3 = r12
                r4 = r13
                r2.setPreviewSize(r3, r4, r5, r6, r7, r8)     // Catch:{ IllegalArgumentException -> 0x0025 }
                com.serenegiant.usb.UVCCamera r0 = r10.mUVCCamera     // Catch:{ IllegalArgumentException -> 0x0025 }
                com.serenegiant.usb.IFrameCallback r1 = r10.mPreviewFrameCallback     // Catch:{ IllegalArgumentException -> 0x0025 }
                r2 = 5
                r0.setFrameCallback(r1, r2)     // Catch:{ IllegalArgumentException -> 0x0025 }
                goto L_0x0032
            L_0x0025:
                com.serenegiant.usb.UVCCamera r3 = r10.mUVCCamera     // Catch:{ IllegalArgumentException -> 0x0066 }
                r6 = 1
                r7 = 30
                r8 = 0
                float r9 = r10.mBandwidthFactor     // Catch:{ IllegalArgumentException -> 0x0066 }
                r4 = r12
                r5 = r13
                r3.setPreviewSize(r4, r5, r6, r7, r8, r9)     // Catch:{ IllegalArgumentException -> 0x0066 }
            L_0x0032:
                boolean r12 = r11 instanceof android.view.SurfaceHolder
                if (r12 == 0) goto L_0x003e
                com.serenegiant.usb.UVCCamera r12 = r10.mUVCCamera
                android.view.SurfaceHolder r11 = (android.view.SurfaceHolder) r11
                r12.setPreviewDisplay(r11)
                goto L_0x0051
            L_0x003e:
                boolean r12 = r11 instanceof android.view.Surface
                if (r12 == 0) goto L_0x004a
                com.serenegiant.usb.UVCCamera r12 = r10.mUVCCamera
                android.view.Surface r11 = (android.view.Surface) r11
                r12.setPreviewDisplay(r11)
                goto L_0x0051
            L_0x004a:
                com.serenegiant.usb.UVCCamera r12 = r10.mUVCCamera
                android.graphics.SurfaceTexture r11 = (android.graphics.SurfaceTexture) r11
                r12.setPreviewTexture(r11)
            L_0x0051:
                com.serenegiant.usb.UVCCamera r11 = r10.mUVCCamera
                r11.startPreview()
                com.serenegiant.usb.UVCCamera r11 = r10.mUVCCamera
                r11.updateCameraParams()
                java.lang.Object r11 = r10.mSync
                monitor-enter(r11)
                r12 = 1
                r10.mIsPreviewing = r12     // Catch:{ all -> 0x0063 }
                monitor-exit(r11)     // Catch:{ all -> 0x0063 }
                return
            L_0x0063:
                r12 = move-exception
                monitor-exit(r11)     // Catch:{ all -> 0x0063 }
                throw r12
            L_0x0066:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.common.AbstractUVCCameraHandler.CameraThread.handleSetPreviewSize(java.lang.Object, int, int):void");
        }

        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0027 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleStartPreview(final Object surface) {
            Log.v(TAG_THREAD, "handleStartPreview:");
            if ((mUVCCamera == null) || mIsPreviewing) return;
            try {
                mUVCCamera.setPreviewSize(mWidth, mHeight, 1, 31, mPreviewMode, mBandwidthFactor);
            } catch (final IllegalArgumentException e) {
                try {
                    // fallback to YUV mode
                    mUVCCamera.setPreviewSize(mWidth, mHeight, 1, 31, UVCCamera.DEFAULT_PREVIEW_MODE, mBandwidthFactor);
                } catch (final IllegalArgumentException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            if (surface instanceof SurfaceHolder) {
                mUVCCamera.setPreviewDisplay((SurfaceHolder)surface);
            } if (surface instanceof Surface) {
                mUVCCamera.setPreviewDisplay((Surface)surface);
            } else {
                mUVCCamera.setPreviewTexture((SurfaceTexture)surface);
            }
            mUVCCamera.startPreview();
            mUVCCamera.updateCameraParams();
            synchronized (mSync) {
                mIsPreviewing = true;
            }
//            callOnStartPreview();  todo 赵磊屏蔽，找不到源代码
        }


        public void handleStartRecording(int i) {
            StringBuilder sb = new StringBuilder();
            sb.append("handleStartRecording: orientation = ");
            sb.append(i);
            LogUtil.d(TAG_THREAD, sb.toString());
            this.mRecordingOrientation = i;
            try {
                if (this.mUVCCamera != null) {
                    if (this.mMuxer == null) {
                        MediaMuxerWrapper mediaMuxerWrapper = new MediaMuxerWrapper(UVCCameraHelper.SUFFIX_MP4);
                        MediaVideoBufferEncoder mediaVideoBufferEncoder = null;
                        int i2 = this.mEncoderType;
                        if (i2 == 1) {
                            new MediaVideoEncoder(mediaMuxerWrapper, getWidth(), getHeight(), this.mMediaEncoderListener);
                        } else if (i2 != 2) {
                            new MediaSurfaceEncoder(mediaMuxerWrapper, getWidth(), getHeight(), this.mMediaEncoderListener);
                        } else {
                            MediaVideoBufferEncoder mediaVideoBufferEncoder2 = new MediaVideoBufferEncoder(mediaMuxerWrapper, getWidth(), getHeight(), this.mRecordingOrientation, this.mMediaEncoderListener);
                            mediaVideoBufferEncoder = mediaVideoBufferEncoder2;
                        }
                        new MediaAudioEncoder(mediaMuxerWrapper, this.mMediaEncoderListener);
                        mediaMuxerWrapper.prepare();
                        mediaMuxerWrapper.startRecording();
                        if (mediaVideoBufferEncoder != null) {
                            this.mUVCCamera.setFrameCallback(this.mIFrameCallback, 5);
                        }
                        if (this.mOnRerecordListener != null) {
                            this.mVideoFileSizeObserver = new VideoFileSizeObserver(mediaMuxerWrapper.getOutputPath());
                            this.mVideoFileSizeObserver.setOnRerecordListener(this.mOnRerecordListener);
                            this.mVideoFileSizeObserver.startWatching();
                        }
                        synchronized (this.mSync) {
                            this.mMuxer = mediaMuxerWrapper;
                            this.mVideoEncoder = mediaVideoBufferEncoder;
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG_THREAD, "startCapture:", e);
            }
        }

        public void handleStopPreview() {
            LogUtil.dv(TAG_THREAD, "handleStopPreview:");
            if (this.mIsPreviewing) {
                UVCCamera uVCCamera = this.mUVCCamera;
                if (uVCCamera != null) {
                    uVCCamera.setFrameCallback(null, 0);
                    this.mUVCCamera.stopPreview();
                }
                synchronized (this.mSync) {
                    this.mIsPreviewing = false;
                    this.mSync.notifyAll();
                }
            }
            LogUtil.dv(TAG_THREAD, "handleStopPreview:finished");
        }

        public void handleStopRecording() {
            MediaMuxerWrapper mediaMuxerWrapper;
            StringBuilder sb = new StringBuilder();
            sb.append("handleStopRecording:mMuxer=");
            sb.append(this.mMuxer);
            LogUtil.d(TAG_THREAD, sb.toString());
            synchronized (this.mSync) {
                mediaMuxerWrapper = this.mMuxer;
                this.mMuxer = null;
                this.mVideoEncoder = null;
                if (this.mUVCCamera != null) {
                    this.mUVCCamera.stopCapture();
                }
            }
            try {
                ((CameraViewInterface) this.mWeakCameraView.get()).setVideoEncoder(null);
            } catch (Exception unused) {
            }
            VideoFileSizeObserver videoFileSizeObserver = this.mVideoFileSizeObserver;
            if (videoFileSizeObserver != null) {
                videoFileSizeObserver.setOnRerecordListener(null);
                this.mVideoFileSizeObserver.stopWatching();
                this.mVideoFileSizeObserver = null;
            }
            if (mediaMuxerWrapper != null) {
                mediaMuxerWrapper.stopRecording();
                this.mUVCCamera.setFrameCallback(null, 0);
            }
        }

        @SuppressLint({"NewApi"})
        public void handleUpdateMedia(String str, boolean z) {
            StringBuilder sb = new StringBuilder();
            sb.append("handleUpdateMedia:path=");
            sb.append(str);
            String sb2 = sb.toString();
            String str2 = TAG_THREAD;
            LogUtil.dv(str2, sb2);
            Activity activity = (Activity) this.mWeakParent.get();
            AbstractUVCCameraHandler abstractUVCCameraHandler = this.mHandler;
            boolean z2 = abstractUVCCameraHandler == null || abstractUVCCameraHandler.mReleased;
            if (activity == null || activity.getApplicationContext() == null) {
                LogUtil.w(str2, "MainActivity already destroyed");
                handleRelease();
                return;
            }
            try {
                LogUtil.di(str2, "MediaScannerConnection#scanFile");
                if (z) {
                    MediaScannerConnection.scanFile(activity.getApplicationContext(), new String[]{str}, null, this.mScanVideoListener);
                } else {
                    MediaScannerConnection.scanFile(activity.getApplicationContext(), new String[]{str}, null, this.mScanCaptureListener);
                }
            } catch (Exception e) {
                LogUtil.e(str2, "handleUpdateMedia:", e);
            }
            if (z2 || activity.isDestroyed()) {
                handleRelease();
            }
        }

        public boolean isCameraOpened() {
            boolean z;
            synchronized (this.mSync) {
                z = this.mUVCCamera != null;
            }
            return z;
        }

        public boolean isEqual(UsbDevice usbDevice) {
            UVCCamera uVCCamera = this.mUVCCamera;
            return (uVCCamera == null || uVCCamera.getDevice() == null || !this.mUVCCamera.getDevice().equals(usbDevice)) ? false : true;
        }

        public boolean isPreviewing() {
            boolean z;
            synchronized (this.mSync) {
                z = this.mUVCCamera != null && this.mIsPreviewing;
            }
            return z;
        }

        public boolean isRecording() {
            boolean z;
            synchronized (this.mSync) {
                z = (this.mUVCCamera == null || this.mMuxer == null) ? false : true;
            }
            return z;
        }

        public Bitmap rotateBitmap(int i, Bitmap bitmap) {
            Matrix matrix = new Matrix();
            if (i == 0) {
                matrix.postRotate(90.0f);
            } else if (i == 90) {
                matrix.postRotate(180.0f);
            } else if (i == 180) {
                matrix.postRotate(270.0f);
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        /* JADX WARNING: Removed duplicated region for block: B:13:0x003b  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x0057 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {

            Looper.prepare();
            AbstractUVCCameraHandler handler = null;
            try {
                final Constructor<? extends AbstractUVCCameraHandler> constructor = mHandlerClass.getDeclaredConstructor(CameraThread.class);
                handler = constructor.newInstance(this);
            } catch (final NoSuchMethodException e) {
                Log.w(TAG, e);
            } catch (final IllegalAccessException e) {
                Log.w(TAG, e);
            } catch (final InstantiationException e) {
                Log.w(TAG, e);
            } catch (final InvocationTargetException e) {
                Log.w(TAG, e);
            }
            if (handler != null) {
                synchronized (mSync) {
                    mHandler = handler;
                    mSync.notifyAll();
                }
                Looper.loop();
//                if (mSoundPool != null) {
//                    mSoundPool.release();
//                    mSoundPool = null;
//                }
                if (mHandler != null) {
                    mHandler.mReleased = true;
                }
            }
//            mCallbacks.clear();
            synchronized (mSync) {
                mHandler = null;
                mSync.notifyAll();
            }
        }
    }

    public interface OnPreViewResultListener {
        void onOpen(UVCCamera uVCCamera);

        void onPreviewResult(byte[] bArr);
    }

    public interface OnScanCompletedListener {
        void onScanCompleted(Bitmap bitmap, Uri uri, String str, boolean z);
    }

    AbstractUVCCameraHandler(CameraThread cameraThread) {
        this.mWeakThread = new WeakReference<>(cameraThread);
    }

    /* access modifiers changed from: protected */
    public void captureStill() {
        checkReleased();
        sendEmptyMessage(4);
    }

    /* access modifiers changed from: protected */
    public void checkReleased() {
        if (isReleased()) {
            throw new IllegalStateException("already released");
        }
    }

    public boolean checkSupportFlag(long j) {
        checkReleased();
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        return (cameraThread == null || cameraThread.mUVCCamera == null || !cameraThread.mUVCCamera.checkSupportFlag(j)) ? false : true;
    }

    public void close() {
        String str = TAG;
        LogUtil.dv(str, "close:");
        if (isOpened()) {
            stopPreview();
            sendEmptyMessage(1);
        }
        LogUtil.dv(str, "close:finished");
    }

    public int getHeight() {
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        if (cameraThread != null) {
            return cameraThread.getHeight();
        }
        return 0;
    }

    public List<Size> getSupportedPreviewSizes() {
        checkReleased();
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        UVCCamera access$400 = cameraThread != null ? cameraThread.mUVCCamera : null;
        if (access$400 != null) {
            return access$400.getSupportedSizeList();
        }
        throw new IllegalStateException();
    }

    public UVCCamera getUVCCamera() {
        checkReleased();
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        if (cameraThread != null) {
            return cameraThread.mUVCCamera;
        }
        return null;
    }

    public int getValue(int i) {
        checkReleased();
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        UVCCamera access$400 = cameraThread != null ? cameraThread.mUVCCamera : null;
        if (access$400 != null) {
            if (i == -2147483647) {
                return access$400.getBrightness();
            }
            if (i == -2147483646) {
                return access$400.getContrast();
            }
            if (i == -2147483392) {
                return access$400.getBacklight();
            }
            if (i == -2147483136) {
                return access$400.getGain();
            }
        }
        throw new IllegalStateException();
    }

    public int getWidth() {
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        if (cameraThread != null) {
            return cameraThread.getWidth();
        }
        return 0;
    }

    public void handleMessage(Message message) {
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        if (cameraThread != null) {
            switch (message.what) {
                case 0:
                    cameraThread.handleOpen((UsbControlBlock) message.obj);
                    break;
                case 1:
                    cameraThread.handleClose();
                    break;
                case 2:
                    cameraThread.handleStartPreview(message.obj);
                    break;
                case 3:
                    cameraThread.handleStopPreview();
                    break;
                case 4:
                    cameraThread.handleCaptureStill(((Integer) message.obj).intValue());
                    break;
                case 5:
                    cameraThread.handleStartRecording(((Integer) message.obj).intValue());
                    break;
                case 6:
                    cameraThread.handleStopRecording();
                    break;
                case 7:
                    cameraThread.handleUpdateMedia((String) message.obj, false);
                    break;
                case 9:
                    cameraThread.handleRelease();
                    break;
                case 10:
                    cameraThread.handleSetPreviewSize(message.obj, message.arg1, message.arg2);
                    break;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("unsupported message:what=");
                    sb.append(message.what);
                    throw new RuntimeException(sb.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isCameraThread() {
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        return cameraThread != null && cameraThread.getId() == Thread.currentThread().getId();
    }

    public boolean isEqual(UsbDevice usbDevice) {
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        return cameraThread != null && cameraThread.isEqual(usbDevice);
    }

    public boolean isOpened() {
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        return cameraThread != null && cameraThread.isCameraOpened();
    }

    public boolean isPreviewing() {
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        return cameraThread != null && cameraThread.isPreviewing();
    }

    public boolean isRecording() {
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        return cameraThread != null && cameraThread.isRecording();
    }

    /* access modifiers changed from: protected */
    public boolean isReleased() {
        return this.mReleased || ((CameraThread) this.mWeakThread.get()) == null;
    }

    public void open(UsbControlBlock usbControlBlock) {
        checkReleased();
        sendMessage(obtainMessage(0, usbControlBlock));
    }

    public void release() {
        this.mReleased = true;
        close();
        sendEmptyMessage(9);
    }

    public int resetValue(int i) {
        checkReleased();
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        UVCCamera access$400 = cameraThread != null ? cameraThread.mUVCCamera : null;
        if (access$400 != null) {
            if (i == -2147483647) {
                access$400.resetBrightness();
                return access$400.getBrightness();
            } else if (i == -2147483646) {
                access$400.resetContrast();
                return access$400.getContrast();
            } else if (i == -2147483392) {
                access$400.resetBacklight();
                return access$400.getBacklight();
            } else if (i == -2147483136) {
                access$400.resetGain();
                return access$400.getGain();
            }
        }
        throw new IllegalStateException();
    }

    public void resize(int i, int i2) {
        checkReleased();
        throw new UnsupportedOperationException("does not support now");
    }

    public void setOnPreViewResultListener(OnPreViewResultListener onPreViewResultListener) {
        checkReleased();
        if (!this.mReleased) {
            CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
            if (cameraThread != null) {
                cameraThread.mPreviewListener = onPreViewResultListener;
            }
        }
    }

    public void setOnRerecordListener(OnRerecordListener onRerecordListener) {
        checkReleased();
        if (!this.mReleased) {
            CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
            if (cameraThread != null) {
                cameraThread.mOnRerecordListener = onRerecordListener;
            }
        }
    }

    public void setOnScanCompletedListener(OnScanCompletedListener onScanCompletedListener) {
        checkReleased();
        if (!this.mReleased && onScanCompletedListener != null) {
            CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
            if (cameraThread != null) {
                cameraThread.mCompletedListener = onScanCompletedListener;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setPreviewSize(Object obj, int i, int i2) {
        checkReleased();
        if ((obj instanceof SurfaceHolder) || (obj instanceof Surface) || (obj instanceof SurfaceTexture)) {
            sendMessageDelayed(obtainMessage(10, i, i2, obj), 200);
            return;
        }
        throw new IllegalArgumentException("surface should be one of SurfaceHolder, Surface or SurfaceTexture");
    }

    public int setValue(int i, int i2) {
        checkReleased();
        CameraThread cameraThread = (CameraThread) this.mWeakThread.get();
        UVCCamera access$400 = cameraThread != null ? cameraThread.mUVCCamera : null;
        if (access$400 != null) {
            if (i == -2147483647) {
                access$400.setBrightness(i2);
                return access$400.getBrightness();
            } else if (i == -2147483646) {
                access$400.setContrast(i2);
                return access$400.getContrast();
            } else if (i == -2147483392) {
                access$400.setBacklight(i2);
                return access$400.getBacklight();
            } else if (i == -2147483136) {
                access$400.setGain(i2);
                return access$400.getGain();
            }
        }
        throw new IllegalStateException();
    }

    /* access modifiers changed from: protected */
    public void startPreview(Object obj) {
        checkReleased();
        if ((obj instanceof SurfaceHolder) || (obj instanceof Surface) || (obj instanceof SurfaceTexture)) {
            sendMessage(obtainMessage(2, obj));
            return;
        }
        throw new IllegalArgumentException("surface should be one of SurfaceHolder, Surface or SurfaceTexture");
    }

    public void startRecording(int i) {
        checkReleased();
        sendMessage(obtainMessage(5, Integer.valueOf(i)));
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:11|12|13|(3:15|16|17)|18|19) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:18:0x0057 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void stopPreview() {
       Log.v(TAG, "stopPreview:");
        removeMessages(MSG_PREVIEW_START);
        stopRecording();
        if (isPreviewing()) {
            final CameraThread thread = mWeakThread.get();
            if (thread == null) return;
            synchronized (thread.mSync) {
                sendEmptyMessage(MSG_PREVIEW_STOP);
                if (!isCameraThread()) {
                    // wait for actually preview stopped to avoid releasing Surface/SurfaceTexture
                    // while preview is still running.
                    // therefore this method will take a time to execute
                    try {
                        thread.mSync.wait();
                    } catch (final InterruptedException e) {
                    }
                }
            }
        }
       Log.v(TAG, "stopPreview:finished");
    }

    public void stopRecording() {
        sendEmptyMessage(6);
    }

    /* access modifiers changed from: protected */
    public void updateMedia(String str) {
        sendMessage(obtainMessage(7, str));
    }

    /* access modifiers changed from: protected */
    public void captureStill(int i) {
        checkReleased();
        sendMessage(obtainMessage(4, Integer.valueOf(i)));
    }
}
