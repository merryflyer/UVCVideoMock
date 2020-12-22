package com.b.a.b;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.android.nightvision.R;
import com.serenegiant.usb.LogUtil;

/* compiled from: ThumbnailViewManager */
public class MThumbnailViewManager {
    /* access modifiers changed from: private */

    /* renamed from: a reason: collision with root package name */
    public static final String f71a = "e";
    /* access modifiers changed from: private */

    /* renamed from: b reason: collision with root package name */
    public final Activity f72b;
    private Bitmap c = null;
    /* access modifiers changed from: private */
    public ImageView d;
    private ImageView e;
    /* access modifiers changed from: private */
    public RoundedBitmapDrawable f;
    private RoundedBitmapDrawable g;
    /* access modifiers changed from: private */
    public RelativeLayout h;
    private Object i = new Object();
    private AsyncTask<Void, Void, Bitmap> j;
    /* access modifiers changed from: private */
    public IOnThumbnailUpdateListener k;
    private int l;
    private boolean m = true;

    /* compiled from: ThumbnailViewManager */
    private class a extends AsyncTask<Void, Void, Bitmap> {
        /* synthetic */ a(MThumbnailViewManager MThumbnailViewManagerVar, MOnClickListener cVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Bitmap doInBackground(Void... voidArr) {
            LogUtil.d(f71a, "[doInBackground]begin.");
            try {
                if (isCancelled()) {
                    LogUtil.w(f71a, "[doInBackground]task is cancel,return.");
                    return null;
                }
                Bitmap a2 = BitmapCreator.a(MThumbnailViewManager.this.f72b.getContentResolver());
                if (MThumbnailViewManager.this.k != null) {
                    MThumbnailViewManager.this.k.a(BitmapCreator.a());
                }
                String a3 = f71a;
                StringBuilder sb = new StringBuilder();
                sb.append("getLastBitmapFromDatabase bitmap = ");
                sb.append(a2);
                LogUtil.d(a3, sb.toString());
                return a2;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private a() {
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Bitmap bitmap) {
            String a2 = f71a;
            StringBuilder sb = new StringBuilder();
            sb.append("[onPostExecute]isCancelled()=");
            sb.append(isCancelled());
            LogUtil.d(a2, sb.toString());
            if (!isCancelled()) {
                MThumbnailViewManager.this.b(bitmap);
                MThumbnailViewManager.this.d.setImageDrawable(MThumbnailViewManager.this.f);
                MThumbnailViewManager.this.h.setVisibility(View.VISIBLE);
            }
        }
    }

    public MThumbnailViewManager(Activity activity, IOnThumbnailUpdateListener IOnThumbnailUpdateListenerVar) {
        this.f72b = activity;
        this.k = IOnThumbnailUpdateListenerVar;
        f();
    }

    private void f() {
        LogUtil.d(f71a, "[initView]...");
        this.h = (RelativeLayout) this.f72b.findViewById(R.id.thumbnail_container);
        this.d = (ImageView) this.f72b.findViewById(R.id.thumbnail);
        this.e = (ImageView) this.f72b.findViewById(R.id.thumbnail_animation);
        this.f = a((Bitmap) null, -13619152);
        this.g = a((Bitmap) null, -1);
        this.d.setImageDrawable(this.f);
        this.e.setImageDrawable(this.g);
        this.d.setOnClickListener(new MOnClickListener(this));
        this.l = Math.min(this.d.getLayoutParams().width, this.d.getLayoutParams().height);
    }

    /* access modifiers changed from: private */
    public void b(Bitmap bitmap) {
        LogUtil.d(f71a, "[updateThumbnailView]...");
        if (this.d == null) {
            return;
        }
        if (bitmap != null) {
            LogUtil.d(f71a, "[updateThumbnailView] set created thumbnail");
            this.f = a(bitmap, -13619152);
            return;
        }
        LogUtil.d(f71a, "[updateThumbnailView] set default thumbnail");
        this.f = a((Bitmap) null, -13619152);
    }

    private void e() {
        synchronized (this.i) {
            if (this.j != null) {
                LogUtil.d(f71a, "[cancelLoadThumbnail]...");
                this.j.cancel(true);
                this.j = null;
            }
        }
    }

    public int c() {
        return this.l;
    }

    public void d() {
        String str = f71a;
        StringBuilder sb = new StringBuilder();
        sb.append("[onResume] IsNeedQueryDB: ");
        sb.append(this.m);
        LogUtil.d(str, sb.toString());
        if (this.m) {
            b();
        }
        this.m = true;
    }

    public void a(boolean z) {
        this.h.setVisibility(z ? View.INVISIBLE : View.VISIBLE);
    }

    public void a(Bitmap bitmap) {
        b(bitmap);
        if (bitmap != null) {
            a(this.e);
            return;
        }
        this.d.setImageDrawable(this.f);
        this.m = false;
    }

    public void b() {
        e();
        synchronized (this.i) {
            this.j = new a(this, null).execute(new Void[0]);
        }
    }

    private RoundedBitmapDrawable a(Bitmap bitmap, int i2) {
        int i3 = this.d.getLayoutParams().width;
        int i4 = this.d.getLayoutParams().height;
        this.d.setContentDescription("Has Content");
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(i3, i4, Config.ARGB_8888);
            this.d.setContentDescription("No Content");
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bitmap2 = height > width ? Bitmap.createBitmap(bitmap, 0, (height - width) / 2, width, width) : height < width ? Bitmap.createBitmap(bitmap, (width - height) / 2, 0, height, height) : bitmap;
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap2, i3, i4, true);
        int width2 = createScaledBitmap.getWidth();
        int height2 = createScaledBitmap.getHeight();
        int min = Math.min(width2, height2) / 2;
        int min2 = Math.min(width2, height2) - 4;
        this.c = Bitmap.createBitmap(min2, min2, Config.ARGB_8888);
        Canvas canvas = new Canvas(this.c);
        canvas.drawColor(i2);
        canvas.drawBitmap(createScaledBitmap, (float) (min2 - width2), (float) (min2 - height2), null);
        Paint paint = new Paint();
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setColor(1291845632);
        canvas.drawCircle((float) (canvas.getWidth() / 2), (float) (canvas.getWidth() / 2), (float) (min2 / 2), paint);
        RoundedBitmapDrawable create = RoundedBitmapDrawableFactory.create(this.f72b.getResources(), this.c);
        create.setCornerRadius((float) min);
        create.setAntiAlias(true);
        bitmap.recycle();
        bitmap2.recycle();
        createScaledBitmap.recycle();
        return create;
    }

    private void a(ImageView imageView) {
        imageView.clearAnimation();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(0);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setRepeatMode(2);
        alphaAnimation.setRepeatCount(1);
        imageView.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new MAnimationListener(this));
    }
}
