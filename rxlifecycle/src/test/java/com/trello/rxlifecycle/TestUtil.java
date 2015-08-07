package com.trello.rxlifecycle;

import android.view.View;

import org.robolectric.util.ReflectionHelpers;

import java.util.concurrent.CopyOnWriteArrayList;

public class TestUtil {

    /**
     * Manually retrieve the view's attach state change listeners of an event. Robolectric
     * doesn't currently support manually firing these, and it would seem the events are not called
     * in normal Robolectric usage either.
     *
     * @param view View with listeners to notify
     */
    static CopyOnWriteArrayList<View.OnAttachStateChangeListener> getAttachStateChangeListeners(View view) {
        Object listenerInfo = ReflectionHelpers.callInstanceMethod(view, "getListenerInfo");
        return ReflectionHelpers.getField(listenerInfo, "mOnAttachStateChangeListeners");
    }

}
