package com.android.nightvision;

import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.serenegiant.usb.LogUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* compiled from: PermissionManager */
public class PermissionUtils {

    /* renamed from: a reason: collision with root package name */
    private final Activity f94a;

    /* renamed from: b reason: collision with root package name */
    private List<String> f95b = new ArrayList();

    public PermissionUtils(Activity activity) {
        this.f94a = activity;
        d();
    }

    private void d() {
        this.f95b.add("android.permission.CAMERA");
        this.f95b.add("android.permission.RECORD_AUDIO");
        this.f95b.add("android.permission.WRITE_EXTERNAL_STORAGE");
        this.f95b.add("android.permission.READ_EXTERNAL_STORAGE");
    }

    public boolean a() {
        if (a(this.f95b).size() > 0) {
            return false;
        }
        LogUtil.dd("PermissionManager", "CheckCameraPermissions(), all on");
        return true;
    }

    public int b() {
        return 100;
    }

    public boolean c() {
        List a2 = a(this.f95b);
        String str = "PermissionManager";
        if (a2.size() > 0) {
            LogUtil.dd(str, "requestCameraLaunchPermissions(), user check");
            ActivityCompat.requestPermissions(this.f94a, (String[]) a2.toArray(new String[a2.size()]), 100);
            return false;
        }
        LogUtil.dd(str, "requestCameraLaunchPermissions(), all on");
        return true;
    }

    public boolean a(String[] strArr, int[] iArr) {
        HashMap hashMap = new HashMap();
        Integer valueOf = Integer.valueOf(0);
        String str = "android.permission.CAMERA";
        hashMap.put(str, valueOf);
        String str2 = "android.permission.RECORD_AUDIO";
        hashMap.put(str2, valueOf);
        String str3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        hashMap.put(str3, valueOf);
        String str4 = "android.permission.READ_EXTERNAL_STORAGE";
        hashMap.put(str4, valueOf);
        for (int i = 0; i < strArr.length; i++) {
            hashMap.put(strArr[i], Integer.valueOf(iArr[i]));
        }
        if (((Integer) hashMap.get(str)).intValue() == 0 && ((Integer) hashMap.get(str2)).intValue() == 0 && ((Integer) hashMap.get(str3)).intValue() == 0 && ((Integer) hashMap.get(str4)).intValue() == 0) {
            return true;
        }
        return false;
    }

    private List<String> a(List<String> list) {
        if (list.size() <= 0) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (true) {
            String str = "PermissionManager";
            if (it.hasNext()) {
                String str2 = (String) it.next();
                if (ContextCompat.checkSelfPermission(this.f94a, str2) != 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("getNeedCheckPermissionList() permission =");
                    sb.append(str2);
                    LogUtil.dd(str, sb.toString());
                    arrayList.add(str2);
                }
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("getNeedCheckPermissionList() listSize =");
                sb2.append(arrayList.size());
                LogUtil.dd(str, sb2.toString());
                return arrayList;
            }
        }
    }
}
