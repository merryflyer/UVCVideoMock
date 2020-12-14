package com.serenegiant.common;

import android.os.FileObserver;

import androidx.annotation.Nullable;

import com.serenegiant.usb.LogUtil;
import java.io.File;

public class VideoFileSizeObserver extends FileObserver {
    private static final int INTERVAL_TIME = 60000;
    private static final long LIMIT_SIZE = 3758096384L;
    private static final String TAG = "VideoFileSize";
    private static final long TOTAL_SIZE = 4294967296L;
    private String mFullPath;
    private long mLastTime;
    private OnRerecordListener mListener;

    public interface OnRerecordListener {
        void onRerecord();
    }

    public VideoFileSizeObserver(String str) {
        super(str);
        this.mFullPath = str;
    }

    private void checkFileSize() {
        String str = this.mFullPath;
        String str2 = TAG;
        if (str == null) {
            LogUtil.e(str2, "full path is null");
            return;
        }
        File file = new File(str);
        if (file.exists() && file.isFile()) {
            long length = file.length();
            if (this.mListener != null && length >= LIMIT_SIZE) {
                StringBuilder sb = new StringBuilder();
                sb.append("size: ");
                sb.append(length);
                LogUtil.i(str2, sb.toString());
                this.mListener.onRerecord();
            }
        }
    }

    public void onEvent(int i, @Nullable String str) {
        if (i == 2) {
            long currentTimeMillis = System.currentTimeMillis();
            long j = this.mLastTime;
            if (j == 0) {
                this.mLastTime = currentTimeMillis;
                checkFileSize();
            } else if (currentTimeMillis - j > 60000) {
                this.mLastTime = currentTimeMillis;
                checkFileSize();
            }
        }
    }

    public void setOnRerecordListener(OnRerecordListener onRerecordListener) {
        this.mListener = onRerecordListener;
    }
}
