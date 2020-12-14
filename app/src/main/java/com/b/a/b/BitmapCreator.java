package com.b.a.b;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import com.serenegiant.usb.LogUtil;
import java.io.IOException;
import java.util.Locale;

/* compiled from: BitmapCreator */
public class BitmapCreator {

    /* renamed from: a reason: collision with root package name */
    private static final String f65a = "a";

    /* renamed from: b reason: collision with root package name */
    private static final String f66b;
    private static Uri c = null;

    /* renamed from: b.a.b.a$a reason: collision with other inner class name */
    /* compiled from: BitmapCreator */
    private static class C0004a {

        /* renamed from: a reason: collision with root package name */
        public final long f67a;

        /* renamed from: b reason: collision with root package name */
        public final int f68b;
        public final long c;
        public final Uri d;
        public final int e;
        public final String f;

        public C0004a(long j, int i, long j2, Uri uri, int i2, String str) {
            this.f67a = j;
            this.f68b = i;
            this.c = j2;
            this.d = uri;
            this.e = i2;
            this.f = str;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Media(id=");
            sb.append(this.f67a);
            sb.append(", orientation=");
            sb.append(this.f68b);
            sb.append(", dateTaken=");
            sb.append(this.c);
            sb.append(", uri=");
            sb.append(this.d);
            sb.append(", mediaType=");
            sb.append(this.e);
            sb.append(", filePath=");
            sb.append(this.f);
            sb.append(")");
            return sb.toString();
        }
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
        sb.append("/NVCamera");
        f66b = sb.toString();
    }

    public static Bitmap a(Bitmap bitmap, int i) {
        String str = f65a;
        StringBuilder sb = new StringBuilder();
        sb.append("[createThumbnailBitmap] targetWidth = ");
        sb.append(i);
        LogUtil.d(str, sb.toString());
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double d = (double) width;
        double d2 = (double) i;
        Double.isNaN(d);
        Double.isNaN(d2);
        return Bitmap.createScaledBitmap(bitmap, i, height / ((int) Math.ceil(d / d2)), false);
    }

    private static Bitmap b(Bitmap bitmap, int i) {
        if (i != 0) {
            Matrix matrix = new Matrix();
            matrix.setRotate((float) i, (float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2));
            try {
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (IllegalArgumentException e) {
                LogUtil.e(f65a, "Failed to rotate bitmap", e);
            }
        }
        return bitmap;
    }

    private static String c() {
        return f66b;
    }

    public static Bitmap a(String str, int i) {
        return a(str, null, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0141  */
    @android.annotation.TargetApi(26)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static Bitmap a(android.content.ContentResolver r24) {
        /*
            r0 = r24
            java.lang.String r1 = f65a
            java.lang.String r2 = "getLastBitmapFromDatabase() begin."
            com.serenegiant.usb.LogUtil.d(r1, r2)
            java.lang.String r7 = "external"
            android.net.Uri r8 = android.provider.MediaStore.Files.getContentUri(r7)
            android.net.Uri$Builder r1 = r8.buildUpon()
            java.lang.String r2 = "limit"
            java.lang.String r3 = "1"
            android.net.Uri$Builder r1 = r1.appendQueryParameter(r2, r3)
            android.net.Uri r2 = r1.build()
            r1 = 5
            java.lang.String[] r3 = new java.lang.String[r1]
            r9 = 0
            java.lang.String r1 = "_id"
            r3[r9] = r1
            r10 = 1
            java.lang.String r1 = "orientation"
            r3[r10] = r1
            r11 = 2
            java.lang.String r1 = "datetaken"
            r3[r11] = r1
            r12 = 3
            java.lang.String r1 = "_data"
            r3[r12] = r1
            java.lang.String r1 = "media_type"
            r13 = 4
            r3[r13] = r1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "((media_type="
            r4.append(r5)
            java.lang.String r5 = java.lang.Integer.toString(r10)
            r4.append(r5)
            java.lang.String r5 = " OR "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r1 = "="
            r4.append(r1)
            java.lang.String r1 = java.lang.Integer.toString(r12)
            r4.append(r1)
            java.lang.String r1 = " ) AND "
            r4.append(r1)
            java.lang.String r1 = "bucket_id"
            r4.append(r1)
            r1 = 61
            r4.append(r1)
            java.lang.String r1 = b()
            r4.append(r1)
            java.lang.String r1 = ")"
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            java.lang.String r6 = "datetaken DESC,_id DESC"
            r5 = 0
            r14 = 0
            r1 = r24
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x013d }
            if (r1 == 0) goto L_0x00b5
            boolean r2 = r1.moveToFirst()     // Catch:{ all -> 0x00b2 }
            if (r2 == 0) goto L_0x00b5
            long r2 = r1.getLong(r9)     // Catch:{ all -> 0x00b2 }
            int r18 = r1.getInt(r10)     // Catch:{ all -> 0x00b2 }
            long r19 = r1.getLong(r11)     // Catch:{ all -> 0x00b2 }
            java.lang.String r23 = r1.getString(r12)     // Catch:{ all -> 0x00b2 }
            b.a.b.a$a r4 = new b.a.b.a$a     // Catch:{ all -> 0x00b2 }
            android.net.Uri r21 = android.content.ContentUris.withAppendedId(r8, r2)     // Catch:{ all -> 0x00b2 }
            int r22 = r1.getInt(r13)     // Catch:{ all -> 0x00b2 }
            r15 = r4
            r16 = r2
            r15.<init>(r16, r18, r19, r21, r22, r23)     // Catch:{ all -> 0x00b2 }
            goto L_0x00b6
        L_0x00b2:
            r0 = move-exception
            goto L_0x013f
        L_0x00b5:
            r4 = r14
        L_0x00b6:
            if (r1 == 0) goto L_0x00bb
            r1.close()
        L_0x00bb:
            java.lang.String r1 = f65a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "getLastBitmapFromDatabase() media="
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            com.serenegiant.usb.LogUtil.d(r1, r2)
            if (r4 != 0) goto L_0x00d6
            c = r14
            return r14
        L_0x00d6:
            int r1 = r4.f68b
            int r2 = r4.e     // Catch:{ OutOfMemoryError -> 0x012d }
            if (r2 != r10) goto L_0x010f
            java.lang.String r2 = r4.f     // Catch:{ OutOfMemoryError -> 0x012d }
            android.media.ExifInterface r2 = a(r2)     // Catch:{ OutOfMemoryError -> 0x012d }
            if (r2 == 0) goto L_0x00fc
            boolean r3 = r2.hasThumbnail()     // Catch:{ OutOfMemoryError -> 0x012d }
            if (r3 == 0) goto L_0x00fc
            android.graphics.Bitmap r3 = r2.getThumbnailBitmap()     // Catch:{ OutOfMemoryError -> 0x012d }
            if (r3 == 0) goto L_0x00fc
            java.lang.String r0 = f65a     // Catch:{ OutOfMemoryError -> 0x012d }
            java.lang.String r3 = "get bitmap from exif thumbnail"
            com.serenegiant.usb.LogUtil.d(r0, r3)     // Catch:{ OutOfMemoryError -> 0x012d }
            android.graphics.Bitmap r0 = r2.getThumbnailBitmap()     // Catch:{ OutOfMemoryError -> 0x012d }
            goto L_0x0102
        L_0x00fc:
            long r2 = r4.f67a     // Catch:{ OutOfMemoryError -> 0x012d }
            android.graphics.Bitmap r0 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r2, r10, r14)     // Catch:{ OutOfMemoryError -> 0x012d }
        L_0x0102:
            android.net.Uri r2 = android.provider.MediaStore.Images.Media.getContentUri(r7)     // Catch:{ OutOfMemoryError -> 0x012d }
            long r3 = r4.f67a     // Catch:{ OutOfMemoryError -> 0x012d }
            android.net.Uri r2 = android.content.ContentUris.withAppendedId(r2, r3)     // Catch:{ OutOfMemoryError -> 0x012d }
            c = r2     // Catch:{ OutOfMemoryError -> 0x012d }
            goto L_0x0128
        L_0x010f:
            int r2 = r4.e     // Catch:{ OutOfMemoryError -> 0x012d }
            if (r2 != r12) goto L_0x0127
            long r1 = r4.f67a     // Catch:{ OutOfMemoryError -> 0x012d }
            android.graphics.Bitmap r0 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r1, r10, r14)     // Catch:{ OutOfMemoryError -> 0x012d }
            android.net.Uri r1 = android.provider.MediaStore.Video.Media.getContentUri(r7)     // Catch:{ OutOfMemoryError -> 0x012d }
            long r2 = r4.f67a     // Catch:{ OutOfMemoryError -> 0x012d }
            android.net.Uri r1 = android.content.ContentUris.withAppendedId(r1, r2)     // Catch:{ OutOfMemoryError -> 0x012d }
            c = r1     // Catch:{ OutOfMemoryError -> 0x012d }
            r1 = 0
            goto L_0x0128
        L_0x0127:
            r0 = r14
        L_0x0128:
            android.graphics.Bitmap r0 = b(r0, r1)     // Catch:{ OutOfMemoryError -> 0x012d }
            return r0
        L_0x012d:
            r0 = move-exception
            java.lang.String r1 = f65a
            java.lang.String r2 = "getThumbnail fail"
            com.serenegiant.usb.LogUtil.e(r1, r2, r0)
            java.lang.String r0 = f65a
            java.lang.String r1 = "Quit getLastBitmap"
            com.serenegiant.usb.LogUtil.d(r0, r1)
            return r14
        L_0x013d:
            r0 = move-exception
            r1 = r14
        L_0x013f:
            if (r1 == 0) goto L_0x0144
            r1.close()
        L_0x0144:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.a.b.a.a(android.content.ContentResolver):android.graphics.Bitmap");
    }

    private static String b() {
        String c2 = c();
        String str = f65a;
        StringBuilder sb = new StringBuilder();
        sb.append("getBucketId directory = ");
        sb.append(c2);
        LogUtil.d(str, sb.toString());
        return String.valueOf(c2.toLowerCase(Locale.ENGLISH).hashCode());
    }

    public static Uri a() {
        return c;
    }

    private static ExifInterface a(String str) {
        if (str != null) {
            try {
                return new ExifInterface(str);
            } catch (IOException e) {
                LogUtil.e(f65a, "Failed to read EXIF data", e);
            }
        }
        LogUtil.w(f65a, "filePath is null, can not get exif");
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0055 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0056  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static Bitmap a(String r4, java.io.FileDescriptor r5, int r6) {
        /*
            java.lang.String r0 = f65a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "[createBitmapFromVideo] filePath = "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r2 = ", targetWidth = "
            r1.append(r2)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            com.serenegiant.usb.LogUtil.d(r0, r1)
            android.media.MediaMetadataRetriever r0 = new android.media.MediaMetadataRetriever
            r0.<init>()
            r1 = 0
            if (r4 == 0) goto L_0x002a
            r0.setDataSource(r4)     // Catch:{ IllegalArgumentException -> 0x0046, RuntimeException -> 0x003e }
            goto L_0x002d
        L_0x002a:
            r0.setDataSource(r5)     // Catch:{ IllegalArgumentException -> 0x0046, RuntimeException -> 0x003e }
        L_0x002d:
            r4 = -1
            android.graphics.Bitmap r4 = r0.getFrameAtTime(r4)     // Catch:{ IllegalArgumentException -> 0x0046, RuntimeException -> 0x003e }
            r0.release()     // Catch:{ RuntimeException -> 0x0037 }
            goto L_0x0053
        L_0x0037:
            r5 = move-exception
            r5.printStackTrace()
            goto L_0x0053
        L_0x003c:
            r4 = move-exception
            goto L_0x00b2
        L_0x003e:
            r4 = move-exception
            r4.printStackTrace()     // Catch:{ all -> 0x003c }
            r0.release()     // Catch:{ RuntimeException -> 0x004e }
            goto L_0x0052
        L_0x0046:
            r4 = move-exception
            r4.printStackTrace()     // Catch:{ all -> 0x003c }
            r0.release()     // Catch:{ RuntimeException -> 0x004e }
            goto L_0x0052
        L_0x004e:
            r4 = move-exception
            r4.printStackTrace()
        L_0x0052:
            r4 = r1
        L_0x0053:
            if (r4 != 0) goto L_0x0056
            return r1
        L_0x0056:
            int r5 = r4.getWidth()
            int r0 = r4.getHeight()
            java.lang.String r1 = f65a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "[createBitmapFromVideo] bitmap = "
            r2.append(r3)
            r2.append(r5)
            java.lang.String r3 = "x"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.serenegiant.usb.LogUtil.v(r1, r2)
            if (r5 <= r6) goto L_0x00b1
            float r6 = (float) r6
            float r5 = (float) r5
            float r6 = r6 / r5
            float r5 = r5 * r6
            int r5 = java.lang.Math.round(r5)
            float r0 = (float) r0
            float r6 = r6 * r0
            int r6 = java.lang.Math.round(r6)
            java.lang.String r0 = f65a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "[createBitmapFromVideo] w = "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = "h"
            r1.append(r2)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            com.serenegiant.usb.LogUtil.v(r0, r1)
            r0 = 1
            android.graphics.Bitmap r4 = android.graphics.Bitmap.createScaledBitmap(r4, r5, r6, r0)
        L_0x00b1:
            return r4
        L_0x00b2:
            r0.release()     // Catch:{ RuntimeException -> 0x00b6 }
            goto L_0x00ba
        L_0x00b6:
            r5 = move-exception
            r5.printStackTrace()
        L_0x00ba:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: b.a.b.a.a(java.lang.String, java.io.FileDescriptor, int):android.graphics.Bitmap");
    }
}
