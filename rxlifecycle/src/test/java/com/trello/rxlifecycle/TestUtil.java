/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
