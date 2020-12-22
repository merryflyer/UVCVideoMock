package com.b.a.b;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.serenegiant.usb.LogUtil;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Locale;

/* compiled from: BitmapCreator */
public class BitmapCreator {

    /* renamed from: a reason: collision with root package name */
    private static final String f65a = "a";

    /* renamed from: b reason: collision with root package name */
    private static final String f66b;
    private static String stra = "BitmapCreator";

    private static Uri uric = null;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap a(ContentResolver p0) {
        String[] stringArray;
        ContentResolver contentResol1;
        Cursor cquery;
        int vi;
        ExifInterface ea;
        Bitmap bThumbnailBi;
        ContentResolver contentResol = p0;
        LogUtil.d(stra, "getLastBitmapFromDatabase begin.");
        String str = "external";
        Uri uContentUri = MediaStore.Files.getContentUri(str);
        Uri ubuild = uContentUri.buildUpon().appendQueryParameter("limit", "1").build();
        stringArray = new String[5];
        stringArray[0]="_id";
        stringArray[1]="orientation";
        stringArray[2]="datetaken";
        stringArray[3]="_data";
        String str1 = "media_type";
        stringArray[4]=str1;
        String sstr = new StringBuilder().append("/(/(media_type=").
                append(Integer.toString(1)).append(" OR ").append(str1).append("=").
                append(Integer.toString(3)).append(" /) AND ").append("bucket_id").append('=').
                append(b()).append("/)").toString();
        String str2 = "datetaken DESC,_id DESC";

        C0004a v4 = null;
       try{
            contentResol1 = p0;
            cquery = contentResol1.query(ubuild, stringArray, sstr, null, str2);
           if (cquery.moveToFirst()) {
                long lLong = cquery.getLong(0);
               v4 = new C0004a(lLong, cquery.getInt(1),
                        cquery.getLong(2),
                        ContentUris.withAppendedId(uContentUri, lLong),
                        cquery.getInt(4),
                        cquery.getString(3));
            }else {
                Object object = null;
            }
            if (!cquery.isClosed()) {
                cquery.close();
            }
            LogUtil.d(stra, new StringBuilder().append("getLastBitmapFromDatabase/(/) media=").append(v4).toString());
            if (null == v4) {
                uric = null;
                return null;
            }else {
                try{
                    vi = v4.f68b;
                    if (v4.e == 1) {
                        ea = a(v4.f);
                        if (ea.hasThumbnail() && null != ea.getThumbnailBitmap()) {
                            LogUtil.d(stra, "get bitmap from exif thumbnail");
                            bThumbnailBi = ea.getThumbnailBitmap();
                        }else {
                            bThumbnailBi = MediaStore.Images.Thumbnails.getThumbnail(contentResol, v4.f67a, 1, null);
                        }
                        uric = ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri(str), v4.f67a);
                    }else if(v4.e == 3){
                        bThumbnailBi = MediaStore.Video.Thumbnails.getThumbnail(contentResol, v4.f67a, 1, null);
                        uric = ContentUris.withAppendedId(MediaStore.Video.Media.getContentUri(str), v4.f67a);
                        vi = 0;
                    }else {
                        bThumbnailBi = null;
                    }
                    return b(bThumbnailBi, vi);
                }catch(java.lang.OutOfMemoryError e0){
                    LogUtil.e(stra, "getThumbnail fail", e0);
                    LogUtil.d(stra, "Quit getLastBitmap");
                    return null;
                }
            }
       }catch(Exception e0){
           vi = 0;
       }

        return null;
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
        return uric;
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

    private static Bitmap a(String p0,
                                 FileDescriptor p1,
                                 int p2) {
        LogUtil.d(stra, new StringBuilder().append("[createBitmapFromVideo] filePath = ").append(p0).append(", targetWidth = ").append(p2).toString());
        MediaMetadataRetriever mediaMetadat = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(p0)) {
            mediaMetadat.setDataSource(p0);
        }else {
            mediaMetadat.setDataSource(p1);
        }
        Bitmap bFrameAtTime = mediaMetadat.getFrameAtTime(-1);
        mediaMetadat.release();
        try{
            if (null != bFrameAtTime) {
                return bitmap;
            }else {
                int iWidth = bFrameAtTime.getWidth();
                int iHeight = bFrameAtTime.getHeight();
                LogUtil.v(stra, new StringBuilder().append("[createBitmapFromVideo] bitmap = ").append(iWidth).append("x").append(iHeight).toString());
                if (iWidth > p2) {
                    float vf = (float)iWidth;
                    float vf1 = (float)p2/vf;
                    iWidth = Math.round((vf*vf1));
                    int iround = Math.round((vf1*(float)iHeight));
                    LogUtil.v(stra, new StringBuilder().append("[createBitmapFromVideo] w = ").append(iWidth).append("h").append(iround).toString());
                    bFrameAtTime = Bitmap.createScaledBitmap(bFrameAtTime, iWidth, iround, true);
                }
                return bFrameAtTime;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
