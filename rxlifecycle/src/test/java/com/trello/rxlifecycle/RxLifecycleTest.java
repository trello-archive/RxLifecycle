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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        // Simulate an actual lifecycle (hot Observable that does not end)
        observable = PublishSubject.create().asObservable();
    }

    @Test
    public void testBindUntilFragmentEvent() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        Subscription untilStop =
            observable.compose(RxLifecycle.bindUntilFragmentEvent(lifecycle, FragmentEvent.STOP)).subscribe();

        lifecycle.onNext(FragmentEvent.ATTACH);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.CREATE);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.START);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.RESUME);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.PAUSE);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.STOP);
        assertTrue(untilStop.isUnsubscribed());
    }

    @Test
    public void testBindUntilActivityEvent() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        Subscription untilStop =
            observable.compose(RxLifecycle.bindUntilActivityEvent(lifecycle, ActivityEvent.STOP)).subscribe();

        lifecycle.onNext(ActivityEvent.CREATE);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(ActivityEvent.START);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(ActivityEvent.RESUME);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(ActivityEvent.PAUSE);
        assertFalse(untilStop.isUnsubscribed());
        lifecycle.onNext(ActivityEvent.STOP);
        assertTrue(untilStop.isUnsubscribed());
    }

    @Test
    public void testBindActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(ActivityEvent.CREATE);
        Subscription createSub = observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe();

        lifecycle.onNext(ActivityEvent.START);
        assertFalse(createSub.isUnsubscribed());
        Subscription startSub = observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe();

        lifecycle.onNext(ActivityEvent.RESUME);
        assertFalse(createSub.isUnsubscribed());
        assertFalse(startSub.isUnsubscribed());
        Subscription resumeSub = observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe();

        lifecycle.onNext(ActivityEvent.PAUSE);
        assertFalse(createSub.isUnsubscribed());
        assertFalse(startSub.isUnsubscribed());
        assertTrue(resumeSub.isUnsubscribed());
        Subscription pauseSub = observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe();

        lifecycle.onNext(ActivityEvent.STOP);
        assertFalse(createSub.isUnsubscribed());
        assertTrue(startSub.isUnsubscribed());
        assertTrue(pauseSub.isUnsubscribed());
        Subscription stopSub = observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe();

        lifecycle.onNext(ActivityEvent.DESTROY);
        assertTrue(createSub.isUnsubscribed());
        assertTrue(stopSub.isUnsubscribed());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowsExceptionOutsideActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(ActivityEvent.CREATE);
        lifecycle.onNext(ActivityEvent.START);
        lifecycle.onNext(ActivityEvent.RESUME);
        lifecycle.onNext(ActivityEvent.PAUSE);
        lifecycle.onNext(ActivityEvent.STOP);
        lifecycle.onNext(ActivityEvent.DESTROY);

        observable.compose(RxLifecycle.bindActivity(lifecycle))
            .subscribe(null, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            });
    }

    @Test
    public void testBindFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(FragmentEvent.ATTACH);
        Subscription attachSub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.CREATE);
        assertFalse(attachSub.isUnsubscribed());
        Subscription createSub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        Subscription createViewSub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.START);
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertFalse(createViewSub.isUnsubscribed());
        Subscription startSub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.RESUME);
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertFalse(createViewSub.isUnsubscribed());
        assertFalse(startSub.isUnsubscribed());
        Subscription resumeSub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.PAUSE);
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertFalse(createViewSub.isUnsubscribed());
        assertFalse(startSub.isUnsubscribed());
        assertTrue(resumeSub.isUnsubscribed());
        Subscription pauseSub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.STOP);
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertFalse(createViewSub.isUnsubscribed());
        assertTrue(startSub.isUnsubscribed());
        assertTrue(pauseSub.isUnsubscribed());
        Subscription stopSub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.DESTROY_VIEW);
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertTrue(createViewSub.isUnsubscribed());
        assertTrue(stopSub.isUnsubscribed());
        Subscription destroyViewSub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.DESTROY);
        assertFalse(attachSub.isUnsubscribed());
        assertTrue(createSub.isUnsubscribed());
        assertTrue(destroyViewSub.isUnsubscribed());
        Subscription destroySub = observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe();

        lifecycle.onNext(FragmentEvent.DETACH);
        assertTrue(attachSub.isUnsubscribed());
        assertTrue(destroySub.isUnsubscribed());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowsExceptionOutsideFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(FragmentEvent.ATTACH);
        lifecycle.onNext(FragmentEvent.CREATE);
        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        lifecycle.onNext(FragmentEvent.START);
        lifecycle.onNext(FragmentEvent.RESUME);
        lifecycle.onNext(FragmentEvent.PAUSE);
        lifecycle.onNext(FragmentEvent.STOP);
        lifecycle.onNext(FragmentEvent.DESTROY_VIEW);
        lifecycle.onNext(FragmentEvent.DESTROY);
        lifecycle.onNext(FragmentEvent.DETACH);

        observable.compose(RxLifecycle.bindFragment(lifecycle))
            .subscribe(null, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            });
    }
}
