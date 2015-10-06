package com.trello.rxlifecycle.components;

import android.content.Intent;

public class ActivityResultEvent {

    private final int requestCode;
    private final int resultCode;
    private final Intent data;

    public static ActivityResultEvent create(int requestCode, int resultCode, Intent data) {
        return new ActivityResultEvent(requestCode, resultCode, data);
    }

    private ActivityResultEvent(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int requestCode() {
        return requestCode;
    }

    public int resultCode() {
        return resultCode;
    }

    public Intent data() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ActivityResultEvent)) return false;
        ActivityResultEvent other = (ActivityResultEvent) o;
        return requestCode == other.requestCode
                && resultCode == other.resultCode
                && data.equals(other.data);
    }


    @Override public int hashCode() {
        int result = 17;
        result = result * 37 + requestCode;
        result = result * 37 + resultCode;
        result = result * 37 + data.hashCode();
        return result;
    }


    @Override public String toString() {
        return "ActivityResultEvent{requestCode="
                + requestCode
                + ", resultCode="
                + resultCode
                + ", data="
                + data()
                + '}';
    }
}
