package com.android.nightvision;

import android.app.Application;
import android.content.Context;

public class NightVisionApplication extends Application {

    /* renamed from: a reason: collision with root package name */
    public static Context f78a;

    public void onCreate() {
        super.onCreate();
        f78a = getApplicationContext();
    }
}
