package com.serenegiant.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;

import com.serenegiant.common.FrameCounter;
import com.serenegiant.encoder.IVideoEncoder;
import com.serenegiant.encoder.MediaEncoder;
import com.serenegiant.encoder.MediaVideoEncoder;
import com.serenegiant.glutils.EGLBase;
import com.serenegiant.glutils.GLDrawer2D;
import com.serenegiant.glutils.es1.GLHelper;
import com.serenegiant.usb.LogUtil;
import com.serenegiant.utils.FpsCounter;
import com.serenegiant.widget.CameraViewInterface.Callback;

public class UVCCameraTextureView extends AspectRatioTextureView implements SurfaceTextureListener, CameraViewInterface {
    private static final String TAG = "UVCCameraTextureView";
    private Callback mCallback;
    private final Object mCaptureSync;
    private final FpsCounter mFpsCounter;
    private final FrameCounter mFrameCounter;
    private boolean mHasSurface;
    private int mHeight;
    private Surface mPreviewSurface;
    private RenderHandler mRenderHandler;
    private boolean mRequestCaptureStillImage;
    private Bitmap mTempBitmap;
    private int mWidth;

    private static final class RenderHandler extends Handler implements OnFrameAvailableListener {
        private static final int MSG_CREATE_SURFACE = 3;
        private static final int MSG_ERROR_INIT_OPENGL = 5;
        private static final int MSG_REQUEST_RENDER = 1;
        private static final int MSG_RESIZE = 4;
        private static final int MSG_SET_ENCODER = 2;
        private static final int MSG_TERMINATE = 9;
        private final FpsCounter mFpsCounter;
        private final FrameCounter mFrameCounter;
        private boolean mIsActive;
        private RenderThread mThread;

        private static final class RenderThread extends Thread {
            private GLDrawer2D mDrawer;
            private EGLBase mEgl;
            private EGLBase.IEglSurface mEglSurface;
            private MediaEncoder mEncoder;
            private final FpsCounter mFpsCounter;
            private final FrameCounter mFrameCounter;
            private RenderHandler mHandler;
            /* access modifiers changed from: private */
            public SurfaceTexture mPreviewSurface;
            private final float[] mStMatrix = new float[16];
            private final SurfaceTexture mSurface;
            /* access modifiers changed from: private */
            public final Object mSync = new Object();
            private int mTexId = -1;
            private int mViewHeight;
            private int mViewWidth;

            public RenderThread(FrameCounter frameCounter, FpsCounter fpsCounter, SurfaceTexture surfaceTexture, int i, int i2) {
                this.mFrameCounter = frameCounter;
                this.mFpsCounter = fpsCounter;
                this.mSurface = surfaceTexture;
                this.mViewWidth = i;
                this.mViewHeight = i2;
                setName("RenderThread");
            }

            /* access modifiers changed from: private */
            public final void init() {
                String str = UVCCameraTextureView.TAG;
                LogUtil.dv(str, "RenderThread#init:");
                if (this.mEgl == null) {
                    this.mEgl = EGLBase.createFrom(null, false, false);
                }
                if (this.mEglSurface == null) {
                    try {
                        this.mEglSurface = mEgl.createFromSurface(this.mSurface);
                        this.mEglSurface.makeCurrent();
                    } catch (Exception e) {
                        LogUtil.e(str, "RenderThread init error", e);
                        this.mEglSurface = null;
                    }
                }
                if (this.mDrawer == null) {
                    this.mDrawer = new GLDrawer2D(true);
                }
            }

            private final void release() {
                LogUtil.dv(UVCCameraTextureView.TAG, "RenderThread#release:");
                GLDrawer2D fVar = this.mDrawer;
                if (fVar != null) {
                    fVar.release();
                    this.mDrawer = null;
                }
                SurfaceTexture surfaceTexture = this.mPreviewSurface;
                if (surfaceTexture != null) {
                    surfaceTexture.setOnFrameAvailableListener(null);
                    this.mPreviewSurface.release();
                    this.mPreviewSurface = null;
                }
                int i = this.mTexId;
                if (i >= 0) {
                    GLHelper.deleteTex(mTexId);
                    this.mTexId = -1;
                }
                EGLBase.IEglSurface cVar = this.mEglSurface;
                if (cVar != null) {
                    cVar.release();
                    this.mEglSurface = null;
                }
                EGLBase aVar = this.mEgl;
                if (aVar != null) {
                    aVar.release();
                    this.mEgl = null;
                }
            }

            /* JADX WARNING: Can't wrap try/catch for region: R(5:2|3|(2:5|6)|7|8) */
            /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0013 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public final com.serenegiant.widget.UVCCameraTextureView.RenderHandler getHandler() {
                /*
                    r2 = this;
                    java.lang.String r0 = "UVCCameraTextureView"
                    java.lang.String r1 = "RenderThread#getHandler:"
                    com.serenegiant.usb.LogUtil.dv(r0, r1)
                    java.lang.Object r0 = r2.mSync
                    monitor-enter(r0)
                    com.serenegiant.widget.UVCCameraTextureView$RenderHandler r1 = r2.mHandler     // Catch:{ all -> 0x0017 }
                    if (r1 != 0) goto L_0x0013
                    java.lang.Object r1 = r2.mSync     // Catch:{ InterruptedException -> 0x0013 }
                    r1.wait()     // Catch:{ InterruptedException -> 0x0013 }
                L_0x0013:
                    monitor-exit(r0)     // Catch:{ all -> 0x0017 }
                    com.serenegiant.widget.UVCCameraTextureView$RenderHandler r0 = r2.mHandler
                    return r0
                L_0x0017:
                    r1 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x0017 }
                    throw r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.widget.UVCCameraTextureView.RenderHandler.RenderThread.getHandler():com.serenegiant.widget.UVCCameraTextureView$RenderHandler");
            }

            public final void onDrawFrame() {
                this.mEglSurface.makeCurrent();
                this.mPreviewSurface.updateTexImage();
                this.mPreviewSurface.getTransformMatrix(this.mStMatrix);
                MediaEncoder mediaEncoder = this.mEncoder;
                if (mediaEncoder != null) {
                    if (mediaEncoder instanceof MediaVideoEncoder) {
                        ((MediaVideoEncoder) mediaEncoder).frameAvailableSoon(this.mStMatrix);
                    } else {
                        mediaEncoder.frameAvailableSoon();
                    }
                }

                mDrawer.draw(mTexId, mStMatrix, 0);
                mEglSurface.swap();
            }

            public void resize(int i, int i2) {
                if ((i <= 0 || i == this.mViewWidth) && (i2 <= 0 || i2 == this.mViewHeight)) {
                    synchronized (this.mSync) {
                        this.mSync.notifyAll();
                    }
                    return;
                }
                this.mViewWidth = i;
                this.mViewHeight = i2;
                updatePreviewSurface();
            }

            public final void run() {
                StringBuilder sb = new StringBuilder();
                sb.append(getName());
                sb.append(" started");
                LogUtil.dd(UVCCameraTextureView.TAG, sb.toString());
                init();
                Looper.prepare();
                synchronized (this.mSync) {
                    this.mHandler = new RenderHandler(this.mFrameCounter, mFpsCounter, this);
                    this.mSync.notify();
                }
                Looper.loop();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(getName());
                sb2.append(" finishing");
                LogUtil.d(UVCCameraTextureView.TAG, sb2.toString());
                release();
                synchronized (this.mSync) {
                    this.mHandler = null;
                    this.mSync.notify();
                }
            }

            public final void setEncoder(MediaEncoder mediaEncoder) {
                StringBuilder sb = new StringBuilder();
                sb.append("RenderThread#setEncoder:encoder=");
                sb.append(mediaEncoder);
                LogUtil.dv(UVCCameraTextureView.TAG, sb.toString());
                if (mediaEncoder != null && (mediaEncoder instanceof MediaVideoEncoder)) {
                    ((MediaVideoEncoder) mediaEncoder).setEglContext(this.mEglSurface.getContext(), this.mTexId);
                }
                this.mEncoder = mediaEncoder;
            }

            public final void updatePreviewSurface() {
                LogUtil.di(UVCCameraTextureView.TAG, "RenderThread#updatePreviewSurface:");
                synchronized (this.mSync) {
                    if (this.mPreviewSurface != null) {
                        LogUtil.dd(UVCCameraTextureView.TAG, "updatePreviewSurface:release mPreviewSurface");
                        this.mPreviewSurface.setOnFrameAvailableListener(null);
                        this.mPreviewSurface.release();
                        this.mPreviewSurface = null;
                    }

                    mEglSurface.makeCurrent();
                    if (mTexId >= 0) {
                        mDrawer.deleteTex(mTexId);
                    }


                    mTexId = mDrawer.initTex();
                    String str = UVCCameraTextureView.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("updatePreviewSurface:tex_id=");
                    sb.append(this.mTexId);
                    LogUtil.dv(str, sb.toString());
                    this.mPreviewSurface = new SurfaceTexture(this.mTexId);
                    this.mPreviewSurface.setDefaultBufferSize(this.mViewWidth, this.mViewHeight);
                    this.mPreviewSurface.setOnFrameAvailableListener(this.mHandler);
                    this.mSync.notifyAll();
                }
            }
        }

        public static final RenderHandler createHandler(FrameCounter frameCounter, FpsCounter bVar, SurfaceTexture surfaceTexture, int i, int i2) {
            RenderThread renderThread = new RenderThread(frameCounter, bVar, surfaceTexture, i, i2);
            renderThread.start();
            return renderThread.getHandler();
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(8:4|5|6|7|8|9|10|11) */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x001f */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final android.graphics.SurfaceTexture getPreviewTexture() {
            /*
                r2 = this;
                java.lang.String r0 = "UVCCameraTextureView"
                java.lang.String r1 = "getPreviewTexture:"
                com.serenegiant.usb.LogUtil.dv(r0, r1)
                boolean r0 = r2.mIsActive
                if (r0 == 0) goto L_0x002a
                com.serenegiant.widget.UVCCameraTextureView$RenderHandler$RenderThread r0 = r2.mThread
                java.lang.Object r0 = r0.mSync
                monitor-enter(r0)
                r1 = 3
                r2.sendEmptyMessage(r1)     // Catch:{ all -> 0x0027 }
                com.serenegiant.widget.UVCCameraTextureView$RenderHandler$RenderThread r1 = r2.mThread     // Catch:{ InterruptedException -> 0x001f }
                java.lang.Object r1 = r1.mSync     // Catch:{ InterruptedException -> 0x001f }
                r1.wait()     // Catch:{ InterruptedException -> 0x001f }
            L_0x001f:
                com.serenegiant.widget.UVCCameraTextureView$RenderHandler$RenderThread r1 = r2.mThread     // Catch:{ all -> 0x0027 }
                android.graphics.SurfaceTexture r1 = r1.mPreviewSurface     // Catch:{ all -> 0x0027 }
                monitor-exit(r0)     // Catch:{ all -> 0x0027 }
                return r1
            L_0x0027:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0027 }
                throw r1
            L_0x002a:
                r0 = 0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.widget.UVCCameraTextureView.RenderHandler.getPreviewTexture():android.graphics.SurfaceTexture");
        }

        public final void handleMessage(Message message) {
            RenderThread renderThread = this.mThread;
            if (renderThread != null) {
                int i = message.what;
                if (i == 1) {
                    renderThread.onDrawFrame();
                } else if (i == 2) {
                    renderThread.setEncoder((MediaEncoder) message.obj);
                } else if (i == 3) {
                    renderThread.updatePreviewSurface();
                } else if (i == 4) {
                    renderThread.resize(message.arg1, message.arg2);
                } else if (i == 5) {
                    renderThread.init();
                } else if (i != 9) {
                    super.handleMessage(message);
                } else {
                    Looper.myLooper().quit();
                    this.mThread = null;
                }
            }
        }

        public final void initOpenGL() {
            LogUtil.dv(UVCCameraTextureView.TAG, "init:");
            if (this.mIsActive) {
                sendEmptyMessage(5);
            }
        }

        public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
            if (this.mIsActive) {
                this.mFrameCounter.update();
                this.mFpsCounter.count();
                sendEmptyMessage(1);
            }
        }

        public final void release() {
            LogUtil.dv(UVCCameraTextureView.TAG, "release:");
            if (this.mIsActive) {
                this.mIsActive = false;
                removeMessages(1);
                removeMessages(2);
                sendEmptyMessage(9);
            }
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(7:4|5|6|7|8|9|10) */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0023 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void resize(int r3, int r4) {
            /*
                r2 = this;
                java.lang.String r0 = "UVCCameraTextureView"
                java.lang.String r1 = "resize:"
                com.serenegiant.usb.LogUtil.dv(r0, r1)
                boolean r0 = r2.mIsActive
                if (r0 == 0) goto L_0x0028
                com.serenegiant.widget.UVCCameraTextureView$RenderHandler$RenderThread r0 = r2.mThread
                java.lang.Object r0 = r0.mSync
                monitor-enter(r0)
                r1 = 4
                android.os.Message r3 = r2.obtainMessage(r1, r3, r4)     // Catch:{ all -> 0x0025 }
                r2.sendMessage(r3)     // Catch:{ all -> 0x0025 }
                com.serenegiant.widget.UVCCameraTextureView$RenderHandler$RenderThread r3 = r2.mThread     // Catch:{ InterruptedException -> 0x0023 }
                java.lang.Object r3 = r3.mSync     // Catch:{ InterruptedException -> 0x0023 }
                r3.wait()     // Catch:{ InterruptedException -> 0x0023 }
            L_0x0023:
                monitor-exit(r0)     // Catch:{ all -> 0x0025 }
                goto L_0x0028
            L_0x0025:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0025 }
                throw r3
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.widget.UVCCameraTextureView.RenderHandler.resize(int, int):void");
        }

        public final void setVideoEncoder(IVideoEncoder iVideoEncoder) {
            StringBuilder sb = new StringBuilder();
            sb.append("setVideoEncoder: mIsActive = ");
            sb.append(this.mIsActive);
            LogUtil.dv(UVCCameraTextureView.TAG, sb.toString());
            if (this.mIsActive) {
                sendMessage(obtainMessage(2, iVideoEncoder));
            }
        }

        private RenderHandler(FrameCounter frameCounter, FpsCounter bVar, RenderThread renderThread) {
            this.mIsActive = true;
            this.mThread = renderThread;
            this.mFrameCounter = frameCounter;
            this.mFpsCounter = bVar;
        }
    }

    public UVCCameraTextureView(Context context) {
        this(context, null, 0);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(8:2|3|4|5|6|7|8|9) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x000b */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap captureStillImage() {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mCaptureSync
            monitor-enter(r0)
            r1 = 1
            r2.mRequestCaptureStillImage = r1     // Catch:{ all -> 0x000f }
            java.lang.Object r1 = r2.mCaptureSync     // Catch:{ InterruptedException -> 0x000b }
            r1.wait()     // Catch:{ InterruptedException -> 0x000b }
        L_0x000b:
            android.graphics.Bitmap r1 = r2.mTempBitmap     // Catch:{ all -> 0x000f }
            monitor-exit(r0)     // Catch:{ all -> 0x000f }
            return r1
        L_0x000f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x000f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.widget.UVCCameraTextureView.captureStillImage():android.graphics.Bitmap");
    }

    public float getFps() {
        return this.mFpsCounter.getFps();
    }

    public int getFrameNum() {
        return this.mFrameCounter.getCount();
    }

    public Surface getSurface() {
        StringBuilder sb = new StringBuilder();
        sb.append("getSurface:hasSurface=");
        sb.append(this.mHasSurface);
        LogUtil.dv(TAG, sb.toString());
        if (this.mPreviewSurface == null) {
            SurfaceTexture surfaceTexture = getSurfaceTexture();
            if (surfaceTexture != null) {
                this.mPreviewSurface = new Surface(surfaceTexture);
            }
        }
        return this.mPreviewSurface;
    }

    public SurfaceTexture getSurfaceTexture() {
        RenderHandler renderHandler = this.mRenderHandler;
        return renderHandler != null ? renderHandler.getPreviewTexture() : super.getSurfaceTexture();
    }

    public float getTotalFps() {
        return this.mFpsCounter.getTotalFps();
    }

    public boolean hasSurface() {
        return this.mHasSurface;
    }

    public void initOpenGL() {
        RenderHandler renderHandler = this.mRenderHandler;
        if (renderHandler != null) {
            renderHandler.initOpenGL();
        }
    }

    public void onPause() {
        String str = TAG;
        LogUtil.dv(str, "onPause:");
        resetFrame();
        if (this.mRenderHandler != null) {
            LogUtil.dv(str, "onPause: mRenderHandler");
            this.mRenderHandler.release();
            this.mRenderHandler = null;
        }
        if (this.mTempBitmap != null) {
            LogUtil.dv(str, "onPause: mTempBitmap");
            this.mTempBitmap.recycle();
            this.mTempBitmap = null;
        }
    }

    public void onResume() {
        StringBuilder sb = new StringBuilder();
        sb.append("onResume: mHasSurface = ");
        sb.append(this.mHasSurface);
        LogUtil.dv(TAG, sb.toString());
        if (this.mHasSurface) {
            this.mRenderHandler = RenderHandler.createHandler(this.mFrameCounter, this.mFpsCounter, super.getSurfaceTexture(), getWidth(), getHeight());
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        StringBuilder sb = new StringBuilder();
        sb.append("onSurfaceTextureAvailable:");
        sb.append(surfaceTexture);
        LogUtil.dv(TAG, sb.toString());
        RenderHandler renderHandler = this.mRenderHandler;
        if (renderHandler == null) {
            this.mRenderHandler = RenderHandler.createHandler(this.mFrameCounter, this.mFpsCounter, surfaceTexture, i, i2);
        } else {
            renderHandler.resize(i, i2);
        }
        this.mHasSurface = true;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onSurfaceCreated(this, getSurface());
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        StringBuilder sb = new StringBuilder();
        sb.append("onSurfaceTextureDestroyed:");
        sb.append(surfaceTexture);
        LogUtil.dv(TAG, sb.toString());
        RenderHandler renderHandler = this.mRenderHandler;
        if (renderHandler != null) {
            renderHandler.release();
            this.mRenderHandler = null;
        }
        this.mHasSurface = false;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onSurfaceDestroy(this, getSurface());
        }
        Surface surface = this.mPreviewSurface;
        if (surface != null) {
            surface.release();
            this.mPreviewSurface = null;
        }
        return true;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        StringBuilder sb = new StringBuilder();
        sb.append("onSurfaceTextureSizeChanged:");
        sb.append(surfaceTexture);
        String sb2 = sb.toString();
        String str = TAG;
        LogUtil.dv(str, sb2);
        RenderHandler renderHandler = this.mRenderHandler;
        if (renderHandler != null) {
            renderHandler.resize(i, i2);
        }
        if (this.mCallback != null) {
            LogUtil.dv(str, "onSurfaceTextureSizeChanged: mCallback");
            this.mCallback.onSurfaceChanged(this, getSurface(), i, i2);
        }
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        synchronized (this.mCaptureSync) {
            if (this.mRequestCaptureStillImage) {
                this.mRequestCaptureStillImage = false;
                this.mTempBitmap = getBitmap(this.mWidth, this.mHeight);
                this.mCaptureSync.notifyAll();
            }
        }
    }

    public void resetFps() {
        this.mFpsCounter.reset();
    }

    public void resetFrame() {
        this.mFrameCounter.reset();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void setPictureSize(int i, int i2) {
        this.mWidth = i;
        this.mHeight = i2;
        RenderHandler renderHandler = this.mRenderHandler;
        if (renderHandler != null) {
            renderHandler.resize(i, i2);
        }
    }

    public void setVideoEncoder(IVideoEncoder iVideoEncoder) {
        RenderHandler renderHandler = this.mRenderHandler;
        if (renderHandler != null) {
            renderHandler.setVideoEncoder(iVideoEncoder);
        }
    }

    public void updateFps() {
        this.mFpsCounter.update();
    }

    public UVCCameraTextureView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public UVCCameraTextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCaptureSync = new Object();
        this.mFpsCounter = new FpsCounter();
        this.mFrameCounter = new FrameCounter();
        setSurfaceTextureListener(this);
    }
}