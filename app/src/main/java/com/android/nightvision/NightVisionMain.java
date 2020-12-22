package com.android.nightvision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.LogUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NightVisionMain extends Activity {

    /* renamed from: a reason: collision with root package name */
    private static final String f79a = "NightVisionMain";

    private List<UsbDevice> a() {
        List deviceFilters = DeviceFilter.getDeviceFilters(this, R.xml.device_filter);
        if (deviceFilters == null) {
            return null;
        }
        DeviceFilter deviceFilter = (DeviceFilter) deviceFilters.get(0);
        HashMap<String, UsbDevice> deviceList = ((UsbManager) getSystemService(Context.USB_SERVICE)).getDeviceList();
        ArrayList arrayList = new ArrayList();
        if (deviceList != null) {
            for (UsbDevice usbDevice : deviceList.values()) {
                if (deviceFilter == null || (deviceFilter.matches(usbDevice) && !deviceFilter.isExclude)) {
                    arrayList.add(usbDevice);
                }
            }
        }
        String str = f79a;
        StringBuilder sb = new StringBuilder();
        sb.append("device.size = ");
        sb.append(arrayList.size());
        LogUtil.d(str, sb.toString());
        return arrayList;
    }

    private boolean b() {
        List a2 = a();
        return a2 != null && a2.size() > 0;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String str = "device";
        UsbDevice usbDevice = (UsbDevice) getIntent().getParcelableExtra(str);
        if (usbDevice != null || b()) {
            getSharedPreferences(Constant.f93b, 0).edit().putBoolean(Constant.c, false).apply();
            Intent intent = new Intent(this, NightVisionActivity.class);
            if (usbDevice != null) {
                intent.putExtra(str, usbDevice);
            }
            startActivity(intent);
        } else {
            Constant.a(this, "没有USB 设备链接");
        }
        finish();
    }
}
