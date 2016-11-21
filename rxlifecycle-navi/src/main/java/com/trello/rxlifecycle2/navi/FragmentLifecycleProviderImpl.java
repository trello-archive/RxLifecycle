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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.rx.RxNavi;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import static com.trello.rxlifecycle2.navi.NaviLifecycleMaps.FRAGMENT_EVENT_FILTER;
import static com.trello.rxlifecycle2.navi.NaviLifecycleMaps.FRAGMENT_EVENT_MAP;

final class FragmentLifecycleProviderImpl implements LifecycleProvider<FragmentEvent> {
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    public FragmentLifecycleProviderImpl(final NaviComponent fragment) {
        if (!fragment.handlesEvents(Event.ATTACH, Event.CREATE, Event.CREATE_VIEW, Event.START, Event.RESUME,
            Event.PAUSE, Event.STOP, Event.DESTROY_VIEW, Event.DESTROY, Event.DETACH)) {
            throw new IllegalArgumentException("NaviComponent does not handle all required events");
        }

        RxNavi.observe(fragment, Event.ALL)
            .filter(FRAGMENT_EVENT_FILTER)
            .map(FRAGMENT_EVENT_MAP)
            .subscribe(lifecycleSubject);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }
}
