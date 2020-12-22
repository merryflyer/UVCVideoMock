package com.android.nightvision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NightVisionReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(intent.getAction())) {
            context.getSharedPreferences(Constant.f93b, 0).edit().putBoolean(Constant.c, true).apply();
        }
    }
}
