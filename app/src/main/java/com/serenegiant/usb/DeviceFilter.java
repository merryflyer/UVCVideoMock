package com.serenegiant.usb;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.text.TextUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class DeviceFilter {
    private static final String TAG = "DeviceFilter";
    public final boolean isExclude;
    public final int mClass;
    public final String mManufacturerName;
    public final int mProductId;
    public final String mProductName;
    public final int mProtocol;
    public final String mSerialNumber;
    public final int mSubclass;
    public final int mVendorId;

    public DeviceFilter(int i, int i2, int i3, int i4, int i5, String str, String str2, String str3) {
        this(i, i2, i3, i4, i5, str, str2, str3, false);
    }

    private static final boolean getAttributeBoolean(Context context, XmlPullParser xmlPullParser, String str, String str2, boolean z) {
        try {
            String attributeValue = xmlPullParser.getAttributeValue(str, str2);
            boolean z2 = false;
            if ("TRUE".equalsIgnoreCase(attributeValue)) {
                return true;
            }
            if ("FALSE".equalsIgnoreCase(attributeValue)) {
                return false;
            }
            if (TextUtils.isEmpty(attributeValue) || !attributeValue.startsWith("@")) {
                int i = 10;
                if (attributeValue != null && attributeValue.length() > 2 && attributeValue.charAt(0) == '0' && (attributeValue.charAt(1) == 'x' || attributeValue.charAt(1) == 'X')) {
                    i = 16;
                    attributeValue = attributeValue.substring(2);
                }
                if (Integer.parseInt(attributeValue, i) != 0) {
                    z2 = true;
                }
                return z2;
            }
            int identifier = context.getResources().getIdentifier(attributeValue.substring(1), null, context.getPackageName());
            if (identifier > 0) {
                return context.getResources().getBoolean(identifier);
            }
            return z;
        } catch (NotFoundException | NullPointerException | NumberFormatException unused) {
            return z;
        }
    }

    private static final int getAttributeInteger(Context context, XmlPullParser xmlPullParser, String str, String str2, int i) {
        try {
            String attributeValue = xmlPullParser.getAttributeValue(str, str2);
            if (TextUtils.isEmpty(attributeValue) || !attributeValue.startsWith("@")) {
                int i2 = 10;
                if (attributeValue != null && attributeValue.length() > 2 && attributeValue.charAt(0) == '0' && (attributeValue.charAt(1) == 'x' || attributeValue.charAt(1) == 'X')) {
                    i2 = 16;
                    attributeValue = attributeValue.substring(2);
                }
                return Integer.parseInt(attributeValue, i2);
            }
            int identifier = context.getResources().getIdentifier(attributeValue.substring(1), null, context.getPackageName());
            if (identifier > 0) {
                return context.getResources().getInteger(identifier);
            }
            return i;
        } catch (NotFoundException | NullPointerException | NumberFormatException unused) {
            return i;
        }
    }

    private static final String getAttributeString(Context context, XmlPullParser xmlPullParser, String str, String str2, String str3) {
        try {
            String attributeValue = xmlPullParser.getAttributeValue(str, str2);
            if (attributeValue == null) {
                attributeValue = str3;
            }
            if (!TextUtils.isEmpty(attributeValue) && attributeValue.startsWith("@")) {
                int identifier = context.getResources().getIdentifier(attributeValue.substring(1), null, context.getPackageName());
                if (identifier > 0) {
                    return context.getResources().getString(identifier);
                }
            }
            return attributeValue;
        } catch (NotFoundException | NullPointerException | NumberFormatException unused) {
            return str3;
        }
    }

    public static List<DeviceFilter> getDeviceFilters(Context context, int i) {
        String str = TAG;
        XmlResourceParser xml = context.getResources().getXml(i);
        ArrayList arrayList = new ArrayList();
        try {
            for (int eventType = xml.getEventType(); eventType != 1; eventType = xml.next()) {
                if (eventType == 2) {
                    DeviceFilter readEntryOne = readEntryOne(context, xml);
                    if (readEntryOne != null) {
                        arrayList.add(readEntryOne);
                    }
                }
            }
        } catch (XmlPullParserException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("XmlPullParserException");
            sb.append(e.getMessage());
            LogUtil.d(str, sb.toString());
        } catch (IOException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("IOException");
            sb2.append(e2.getMessage());
            LogUtil.d(str, sb2.toString());
        }
        return Collections.unmodifiableList(arrayList);
    }

    private boolean matches(int i, int i2, int i3) {
        int i4 = this.mClass;
        if (i4 == -1 || i == i4) {
            int i5 = this.mSubclass;
            if (i5 == -1 || i2 == i5) {
                int i6 = this.mProtocol;
                if (i6 == -1 || i3 == i6) {
                    return true;
                }
            }
        }
        return false;
    }

    public static DeviceFilter readEntryOne(Context context, XmlPullParser xmlPullParser) {
        Context context2 = context;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        int eventType = 0;
        try {
            eventType = xmlPullParser.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        String str = null;
        String str2 = null;
        String str3 = null;
        boolean z = false;
        int i = -1;
        int i2 = -1;
        int i3 = -1;
        int i4 = -1;
        int i5 = -1;
        boolean z2 = false;
        while (eventType != 1) {
            String name = xmlPullParser.getName();
            if (!TextUtils.isEmpty(name) && name.equalsIgnoreCase("usb-device")) {
                if (eventType == 2) {
                    int attributeInteger = getAttributeInteger(context2, xmlPullParser2, null, "vendor-id", -1);
                    if (attributeInteger == -1) {
                        attributeInteger = getAttributeInteger(context2, xmlPullParser2, null, "vendorId", -1);
                        if (attributeInteger == -1) {
                            attributeInteger = getAttributeInteger(context2, xmlPullParser2, null, "venderId", -1);
                        }
                    }
                    int attributeInteger2 = getAttributeInteger(context2, xmlPullParser2, null, "product-id", -1);
                    if (attributeInteger2 == -1) {
                        attributeInteger2 = getAttributeInteger(context2, xmlPullParser2, null, "productId", -1);
                    }
                    int attributeInteger3 = getAttributeInteger(context2, xmlPullParser2, null, "class", -1);
                    int attributeInteger4 = getAttributeInteger(context2, xmlPullParser2, null, "subclass", -1);
                    int attributeInteger5 = getAttributeInteger(context2, xmlPullParser2, null, "protocol", -1);
                    String attributeString = getAttributeString(context2, xmlPullParser2, null, "manufacturer-name", null);
                    if (TextUtils.isEmpty(attributeString)) {
                        attributeString = getAttributeString(context2, xmlPullParser2, null, "manufacture", null);
                    }
                    String attributeString2 = getAttributeString(context2, xmlPullParser2, null, "product-name", null);
                    if (TextUtils.isEmpty(attributeString2)) {
                        attributeString2 = getAttributeString(context2, xmlPullParser2, null, "product", null);
                    }
                    String attributeString3 = getAttributeString(context2, xmlPullParser2, null, "serial-number", null);
                    if (TextUtils.isEmpty(attributeString3)) {
                        attributeString3 = getAttributeString(context2, xmlPullParser2, null, "serial", null);
                    }
                    str = attributeString;
                    str2 = attributeString2;
                    str3 = attributeString3;
                    z2 = getAttributeBoolean(context2, xmlPullParser2, null, "exclude", false);
                    i2 = attributeInteger2;
                    i3 = attributeInteger3;
                    i4 = attributeInteger4;
                    i5 = attributeInteger5;
                    z = true;
                    i = attributeInteger;
                } else if (eventType == 3 && z) {
                    DeviceFilter deviceFilter = new DeviceFilter(i, i2, i3, i4, i5, str, str2, str3, z2);
                    return deviceFilter;
                }
            }
            try {
                eventType = xmlPullParser.next();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0071, code lost:
        if (r1.equals(r0) == false) goto L_0x008f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x007f, code lost:
        if (r1.equals(r0) == false) goto L_0x008f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x008d, code lost:
        if (r1.equals(r0) == false) goto L_0x008f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x008f, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r9) {
        /*
            r8 = this;
            int r0 = r8.mVendorId
            r1 = -1
            r2 = 0
            if (r0 == r1) goto L_0x00cc
            int r3 = r8.mProductId
            if (r3 == r1) goto L_0x00cc
            int r4 = r8.mClass
            if (r4 == r1) goto L_0x00cc
            int r5 = r8.mSubclass
            if (r5 == r1) goto L_0x00cc
            int r6 = r8.mProtocol
            if (r6 != r1) goto L_0x0018
            goto L_0x00cc
        L_0x0018:
            boolean r1 = r9 instanceof com.serenegiant.usb.DeviceFilter
            r7 = 1
            if (r1 == 0) goto L_0x0098
            com.serenegiant.usb.DeviceFilter r9 = (com.serenegiant.usb.DeviceFilter) r9
            int r1 = r9.mVendorId
            if (r1 != r0) goto L_0x0097
            int r0 = r9.mProductId
            if (r0 != r3) goto L_0x0097
            int r0 = r9.mClass
            if (r0 != r4) goto L_0x0097
            int r0 = r9.mSubclass
            if (r0 != r5) goto L_0x0097
            int r0 = r9.mProtocol
            if (r0 == r6) goto L_0x0034
            goto L_0x0097
        L_0x0034:
            java.lang.String r0 = r9.mManufacturerName
            if (r0 == 0) goto L_0x003c
            java.lang.String r0 = r8.mManufacturerName
            if (r0 == 0) goto L_0x0064
        L_0x003c:
            java.lang.String r0 = r9.mManufacturerName
            if (r0 != 0) goto L_0x0044
            java.lang.String r0 = r8.mManufacturerName
            if (r0 != 0) goto L_0x0064
        L_0x0044:
            java.lang.String r0 = r9.mProductName
            if (r0 == 0) goto L_0x004c
            java.lang.String r0 = r8.mProductName
            if (r0 == 0) goto L_0x0064
        L_0x004c:
            java.lang.String r0 = r9.mProductName
            if (r0 != 0) goto L_0x0054
            java.lang.String r0 = r8.mProductName
            if (r0 != 0) goto L_0x0064
        L_0x0054:
            java.lang.String r0 = r9.mSerialNumber
            if (r0 == 0) goto L_0x005c
            java.lang.String r0 = r8.mSerialNumber
            if (r0 == 0) goto L_0x0064
        L_0x005c:
            java.lang.String r0 = r9.mSerialNumber
            if (r0 != 0) goto L_0x0065
            java.lang.String r0 = r8.mSerialNumber
            if (r0 == 0) goto L_0x0065
        L_0x0064:
            return r2
        L_0x0065:
            java.lang.String r0 = r9.mManufacturerName
            if (r0 == 0) goto L_0x0073
            java.lang.String r1 = r8.mManufacturerName
            if (r1 == 0) goto L_0x0073
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x008f
        L_0x0073:
            java.lang.String r0 = r9.mProductName
            if (r0 == 0) goto L_0x0081
            java.lang.String r1 = r8.mProductName
            if (r1 == 0) goto L_0x0081
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x008f
        L_0x0081:
            java.lang.String r0 = r9.mSerialNumber
            if (r0 == 0) goto L_0x0090
            java.lang.String r1 = r8.mSerialNumber
            if (r1 == 0) goto L_0x0090
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0090
        L_0x008f:
            return r2
        L_0x0090:
            boolean r9 = r9.isExclude
            boolean r0 = r8.isExclude
            if (r9 == r0) goto L_0x0097
            r2 = 1
        L_0x0097:
            return r2
        L_0x0098:
            boolean r0 = r9 instanceof android.hardware.usb.UsbDevice
            if (r0 == 0) goto L_0x00cc
            android.hardware.usb.UsbDevice r9 = (android.hardware.usb.UsbDevice) r9
            boolean r0 = r8.isExclude
            if (r0 != 0) goto L_0x00cc
            int r0 = r9.getVendorId()
            int r1 = r8.mVendorId
            if (r0 != r1) goto L_0x00cc
            int r0 = r9.getProductId()
            int r1 = r8.mProductId
            if (r0 != r1) goto L_0x00cc
            int r0 = r9.getDeviceClass()
            int r1 = r8.mClass
            if (r0 != r1) goto L_0x00cc
            int r0 = r9.getDeviceSubclass()
            int r1 = r8.mSubclass
            if (r0 != r1) goto L_0x00cc
            int r9 = r9.getDeviceProtocol()
            int r0 = r8.mProtocol
            if (r9 == r0) goto L_0x00cb
            goto L_0x00cc
        L_0x00cb:
            return r7
        L_0x00cc:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.usb.DeviceFilter.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        return ((this.mVendorId << 16) | this.mProductId) ^ (((this.mClass << 16) | (this.mSubclass << 8)) | this.mProtocol);
    }

    public boolean isExclude(UsbDevice usbDevice) {
        return this.isExclude && matches(usbDevice);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeviceFilter[mVendorId=");
        sb.append(this.mVendorId);
        sb.append(",mProductId=");
        sb.append(this.mProductId);
        sb.append(",mClass=");
        sb.append(this.mClass);
        sb.append(",mSubclass=");
        sb.append(this.mSubclass);
        sb.append(",mProtocol=");
        sb.append(this.mProtocol);
        sb.append(",mManufacturerName=");
        sb.append(this.mManufacturerName);
        sb.append(",mProductName=");
        sb.append(this.mProductName);
        sb.append(",mSerialNumber=");
        sb.append(this.mSerialNumber);
        sb.append(",isExclude=");
        sb.append(this.isExclude);
        sb.append("]");
        return sb.toString();
    }

    public DeviceFilter(int i, int i2, int i3, int i4, int i5, String str, String str2, String str3, boolean z) {
        this.mVendorId = i;
        this.mProductId = i2;
        this.mClass = i3;
        this.mSubclass = i4;
        this.mProtocol = i5;
        this.mManufacturerName = str;
        this.mProductName = str2;
        this.mSerialNumber = str3;
        this.isExclude = z;
    }

    public boolean matches(UsbDevice usbDevice) {
        if (this.mVendorId != -1 && usbDevice.getVendorId() != this.mVendorId) {
            return false;
        }
        if (this.mProductId != -1 && usbDevice.getProductId() != this.mProductId) {
            return false;
        }
        if (matches(usbDevice.getDeviceClass(), usbDevice.getDeviceSubclass(), usbDevice.getDeviceProtocol())) {
            return true;
        }
        int interfaceCount = usbDevice.getInterfaceCount();
        for (int i = 0; i < interfaceCount; i++) {
            UsbInterface usbInterface = usbDevice.getInterface(i);
            if (matches(usbInterface.getInterfaceClass(), usbInterface.getInterfaceSubclass(), usbInterface.getInterfaceProtocol())) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(DeviceFilter deviceFilter) {
        if (this.isExclude != deviceFilter.isExclude) {
            return false;
        }
        int i = this.mVendorId;
        if (i != -1 && deviceFilter.mVendorId != i) {
            return false;
        }
        int i2 = this.mProductId;
        if (i2 != -1 && deviceFilter.mProductId != i2) {
            return false;
        }
        if (deviceFilter.mManufacturerName != null && this.mManufacturerName == null) {
            return false;
        }
        if (deviceFilter.mProductName != null && this.mProductName == null) {
            return false;
        }
        if (deviceFilter.mSerialNumber != null && this.mSerialNumber == null) {
            return false;
        }
        String str = this.mManufacturerName;
        if (str != null) {
            String str2 = deviceFilter.mManufacturerName;
            if (str2 != null && !str.equals(str2)) {
                return false;
            }
        }
        String str3 = this.mProductName;
        if (str3 != null) {
            String str4 = deviceFilter.mProductName;
            if (str4 != null && !str3.equals(str4)) {
                return false;
            }
        }
        String str5 = this.mSerialNumber;
        if (str5 != null) {
            String str6 = deviceFilter.mSerialNumber;
            if (str6 != null && !str5.equals(str6)) {
                return false;
            }
        }
        return matches(deviceFilter.mClass, deviceFilter.mSubclass, deviceFilter.mProtocol);
    }

    public DeviceFilter(UsbDevice usbDevice) {
        this(usbDevice, false);
    }

    public DeviceFilter(UsbDevice usbDevice, boolean z) {
        this.mVendorId = usbDevice.getVendorId();
        this.mProductId = usbDevice.getProductId();
        this.mClass = usbDevice.getDeviceClass();
        this.mSubclass = usbDevice.getDeviceSubclass();
        this.mProtocol = usbDevice.getDeviceProtocol();
        this.mManufacturerName = null;
        this.mProductName = null;
        this.mSerialNumber = null;
        this.isExclude = z;
    }
}
