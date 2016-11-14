/*
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

package com.trello.rxlifecycle2.navi;

import com.trello.navi2.Event;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.functions.Function;

/**
 * Maps from Navi events to RxLifecycleAndroid events
 */
final class NaviLifecycleMaps {

    static final Function<Event.Type, ActivityEvent> ACTIVITY_EVENT_MAP =
        new Function<Event.Type, ActivityEvent>() {
            @Override
            public ActivityEvent apply(Event.Type type) throws Exception {
                switch (type) {
                    case CREATE:
                        return ActivityEvent.CREATE;
                    case START:
                        return ActivityEvent.START;
                    case RESUME:
                        return ActivityEvent.RESUME;
                    case PAUSE:
                        return ActivityEvent.PAUSE;
                    case STOP:
                        return ActivityEvent.STOP;
                    case DESTROY:
                        return ActivityEvent.DESTROY;
                    default:
                        return null;
                }
            }
        };

    static final Function<Event.Type, FragmentEvent> FRAGMENT_EVENT_MAP =
        new Function<Event.Type, FragmentEvent>() {
            @Override
            public FragmentEvent apply(Event.Type type) throws Exception {
                switch (type) {
                    case ATTACH:
                        return FragmentEvent.ATTACH;
                    case CREATE:
                        return FragmentEvent.CREATE;
                    case CREATE_VIEW:
                        return FragmentEvent.CREATE_VIEW;
                    case START:
                        return FragmentEvent.START;
                    case RESUME:
                        return FragmentEvent.RESUME;
                    case PAUSE:
                        return FragmentEvent.PAUSE;
                    case STOP:
                        return FragmentEvent.STOP;
                    case DESTROY_VIEW:
                        return FragmentEvent.DESTROY_VIEW;
                    case DESTROY:
                        return FragmentEvent.DESTROY;
                    case DETACH:
                        return FragmentEvent.DETACH;
                    default:
                        return null;
                }
            }
        };

    private NaviLifecycleMaps() {
        throw new AssertionError("No instances!");
    }
}
