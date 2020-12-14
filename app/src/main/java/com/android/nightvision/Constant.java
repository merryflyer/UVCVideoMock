package com.android.nightvision;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import java.util.LinkedList;

/* compiled from: NightVisionUtils */
public class Constant {

    /* renamed from: a reason: collision with root package name */
    private static LinkedList<Activity> f92a = new LinkedList<>();

    /* renamed from: b reason: collision with root package name */
    public static String f93b = "NightVision";
    public static String c = "night_vision_detach";

    public static void a(Context context, String i) {
        Toast makeText = Toast.makeText(context, i, Toast.LENGTH_SHORT);
        makeText.setGravity(80, 0, 0);
        makeText.show();
    }
}
