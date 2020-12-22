package com.serenegiant.common;

import android.os.Handler;
import android.os.Message;

public class PreviewRetry {
    private static final int MSG_HANDLE_RETRY = 4096;
    public static final int PREVIEW_TIME_DELAY = 3000;
    private static PreviewRetry mInstance;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 4096 && PreviewRetry.this.mOnPreviewRetryListener != null) {
                PreviewRetry.this.mOnPreviewRetryListener.handleTimeOut();
            }
        }
    };
    /* access modifiers changed from: private */
    public OnPreviewRetryListener mOnPreviewRetryListener;

    public interface OnPreviewRetryListener {
        void handleTimeOut();
    }

    public static synchronized PreviewRetry getInstance() {
        PreviewRetry previewRetry;
        synchronized (PreviewRetry.class) {
            if (mInstance == null) {
                synchronized (PreviewRetry.class) {
                    if (mInstance == null) {
                        mInstance = new PreviewRetry();
                    }
                }
            }
            previewRetry = mInstance;
        }
        return previewRetry;
    }

    public void cancelTime() {
        this.mHandler.removeMessages(4096);
    }

    public void setPreviewRetryListener(OnPreviewRetryListener onPreviewRetryListener) {
        this.mOnPreviewRetryListener = onPreviewRetryListener;
    }

    public void startTimeOut(long j) {
        this.mHandler.sendEmptyMessageDelayed(4096, j);
    }
}
