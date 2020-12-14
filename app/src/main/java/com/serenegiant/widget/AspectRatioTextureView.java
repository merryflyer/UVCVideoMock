package com.serenegiant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View.MeasureSpec;
import com.serenegiant.usb.LogUtil;
import com.serenegiant.widget.CameraViewInterface.Callback;

public class AspectRatioTextureView extends TextureView implements IAspectRatioView {
    private static final String TAG = "AbstractCameraView";
    private Callback mCallback;
    private double mRequestedAspect;

    public AspectRatioTextureView(Context context) {
        this(context, null, 0);
    }

    public double getAspectRatio() {
        return this.mRequestedAspect;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        LogUtil.dd(TAG, "onMeasure");
        if (this.mRequestedAspect > 0.0d) {
            int size = MeasureSpec.getSize(i);
            int paddingLeft = getPaddingLeft() + getPaddingRight();
            int paddingTop = getPaddingTop() + getPaddingBottom();
            int i5 = size - paddingLeft;
            int size2 = MeasureSpec.getSize(i2) - paddingTop;
            double d = (double) i5;
            double d2 = (double) size2;
            Double.isNaN(d);
            Double.isNaN(d2);
            double d3 = (this.mRequestedAspect / (d / d2)) - 1.0d;
            if (Math.abs(d3) > 0.01d) {
                if (d3 > 0.0d) {
                    double d4 = this.mRequestedAspect;
                    Double.isNaN(d);
                    size2 = (int) (d / d4);
                } else {
                    double d5 = this.mRequestedAspect;
                    Double.isNaN(d2);
                    i5 = (int) (d2 * d5);
                }
                int i6 = size2 + paddingTop;
                i4 = MeasureSpec.makeMeasureSpec(i5 + paddingLeft, MeasureSpec.UNSPECIFIED); //todo 屏蔽
                i3 = MeasureSpec.makeMeasureSpec(i6, MeasureSpec.UNSPECIFIED);
                super.onMeasure(i4, i3);
            }
        }
        i4 = i;
        i3 = i2;
        super.onMeasure(i4, i3);
    }

    public void setAspectRatio(double d) {
        if (d < 0.0d) {
            throw new IllegalArgumentException();
        } else if (this.mRequestedAspect != d) {
            this.mRequestedAspect = d;
            requestLayout();
        }
    }

    public AspectRatioTextureView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AspectRatioTextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mRequestedAspect = -1.0d;
    }

    public void setAspectRatio(int i, int i2) {
        double d = (double) i;
        double d2 = (double) i2;
        Double.isNaN(d);
        Double.isNaN(d2);
        setAspectRatio(d / d2);
    }
}
