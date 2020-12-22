package com.serenegiant.usb;

import android.util.Log;

public class LogUtil {
    private static boolean DEBUG = false;
    public static final String TAG = "USB_NV/";

    public static void d(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(str);
        Log.d(sb.toString(), str2);
    }

    public static void dd(String str, String str2) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(str);
            Log.d(sb.toString(), str2);
        }
    }

    public static void di(String str, String str2) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(str);
            Log.i(sb.toString(), str2);
        }
    }

    public static void dv(String str, String str2) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(str);
            Log.v(sb.toString(), str2);
        }
    }

    public static void dw(String str, String str2) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(str);
            Log.w(sb.toString(), str2);
        }
    }

    public static void e(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(str);
        Log.e(sb.toString(), str2);
    }

    public static void i(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(str);
        Log.i(sb.toString(), str2);
    }

    public static void v(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(str);
        Log.v(sb.toString(), str2);
    }

    public static void w(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(str);
        Log.w(sb.toString(), str2);
    }

    public static void e(String str, String str2, Throwable th) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(str);
        Log.e(sb.toString(), str2, th);
    }
}
