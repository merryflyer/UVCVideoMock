package com.serenegiant.usb;

import java.nio.ByteBuffer;

public interface IFrameCallback {
    void onFrame(ByteBuffer byteBuffer);
}
