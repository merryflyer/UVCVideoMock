package com.serenegiant.usb;

import java.nio.ByteBuffer;

public interface IStatusCallback {
    void onStatus(int i, int i2, int i3, int i4, ByteBuffer byteBuffer);
}
