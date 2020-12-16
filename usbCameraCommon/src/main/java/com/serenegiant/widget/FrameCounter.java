package com.serenegiant.widget;

public class FrameCounter {
    private int num;

    public FrameCounter() {
        reset();
    }

    public synchronized int getCount() {
        return this.num;
    }

    public synchronized FrameCounter reset() {
        this.num = 0;
        return this;
    }

    public synchronized void update() {
        this.num++;
    }
}
