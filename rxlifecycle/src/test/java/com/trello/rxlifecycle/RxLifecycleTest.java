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

import android.app.Activity;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();

        observable.compose(RxLifecycle.bindUntilFragmentEvent(lifecycle, FragmentEvent.STOP))
            .subscribe(testSubscriber);

        lifecycle.onNext(FragmentEvent.ATTACH);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.CREATE);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.START);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.RESUME);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.PAUSE);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(FragmentEvent.STOP);
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testBindUntilActivityEvent() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();

        observable.compose(RxLifecycle.bindUntilActivityEvent(lifecycle, ActivityEvent.STOP))
            .subscribe(testSubscriber);

        lifecycle.onNext(ActivityEvent.CREATE);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(ActivityEvent.START);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(ActivityEvent.RESUME);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(ActivityEvent.PAUSE);
        assertFalse(testSubscriber.isUnsubscribed());
        lifecycle.onNext(ActivityEvent.STOP);
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testBindActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(ActivityEvent.CREATE);
        TestSubscriber<Object> createTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe(createTestSub);

        lifecycle.onNext(ActivityEvent.START);
        assertFalse(createTestSub.isUnsubscribed());
        TestSubscriber<Object> startTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe(startTestSub);

        lifecycle.onNext(ActivityEvent.RESUME);
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        TestSubscriber<Object> resumeTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe(resumeTestSub);

        lifecycle.onNext(ActivityEvent.PAUSE);
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        resumeTestSub.assertCompleted();
        resumeTestSub.assertUnsubscribed();
        TestSubscriber<Object> pauseTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe(pauseTestSub);

        lifecycle.onNext(ActivityEvent.STOP);
        assertFalse(createTestSub.isUnsubscribed());
        startTestSub.assertCompleted();
        startTestSub.assertUnsubscribed();
        pauseTestSub.assertCompleted();
        pauseTestSub.assertUnsubscribed();
        TestSubscriber<Object> stopTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe(stopTestSub);

        lifecycle.onNext(ActivityEvent.DESTROY);
        createTestSub.assertCompleted();
        createTestSub.assertUnsubscribed();
        stopTestSub.assertCompleted();
        stopTestSub.assertUnsubscribed();
    }

    public void testThrowsExceptionOutsideActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(ActivityEvent.DESTROY);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindActivity(lifecycle)).subscribe(testSubscriber);
        testSubscriber.assertError(IllegalStateException.class);
    }

    @Test
    public void testBindFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(FragmentEvent.ATTACH);
        TestSubscriber<Object> attachTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(attachTestSub);

        lifecycle.onNext(FragmentEvent.CREATE);
        assertFalse(attachTestSub.isUnsubscribed());
        TestSubscriber<Object> createTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(createTestSub);

        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        TestSubscriber<Object> createViewTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(createViewTestSub);

        lifecycle.onNext(FragmentEvent.START);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        TestSubscriber<Object> startTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(startTestSub);

        lifecycle.onNext(FragmentEvent.RESUME);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        TestSubscriber<Object> resumeTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(resumeTestSub);

        lifecycle.onNext(FragmentEvent.PAUSE);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        resumeTestSub.assertCompleted();
        resumeTestSub.assertUnsubscribed();
        TestSubscriber<Object> pauseTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(pauseTestSub);

        lifecycle.onNext(FragmentEvent.STOP);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        startTestSub.assertCompleted();
        startTestSub.assertUnsubscribed();
        pauseTestSub.assertCompleted();
        pauseTestSub.assertUnsubscribed();
        TestSubscriber<Object> stopTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(stopTestSub);

        lifecycle.onNext(FragmentEvent.DESTROY_VIEW);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        createViewTestSub.assertCompleted();
        createViewTestSub.assertUnsubscribed();
        stopTestSub.assertCompleted();
        stopTestSub.assertUnsubscribed();
        TestSubscriber<Object> desroyViewTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(desroyViewTestSub);

        lifecycle.onNext(FragmentEvent.DESTROY);
        assertFalse(attachTestSub.isUnsubscribed());
        createTestSub.assertCompleted();
        createTestSub.assertUnsubscribed();
        desroyViewTestSub.assertCompleted();
        desroyViewTestSub.assertUnsubscribed();
        TestSubscriber<Object> destroyTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(destroyTestSub);

        lifecycle.onNext(FragmentEvent.DETACH);
        attachTestSub.assertCompleted();
        attachTestSub.assertUnsubscribed();
        destroyTestSub.assertCompleted();
        destroyTestSub.assertUnsubscribed();
    }

    public void testThrowsExceptionOutsideFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(FragmentEvent.DETACH);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.compose(RxLifecycle.bindFragment(lifecycle)).subscribe(testSubscriber);
        testSubscriber.assertError(IllegalStateException.class);
    }

    @Test
    public void testBindViewLifecycle() {
        BehaviorSubject<ViewEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(ViewEvent.ATTACH);
        Subscription attachSub = observable.compose(RxLifecycle.bindView(lifecycle)).subscribe();
        assertFalse(attachSub.isUnsubscribed());
        lifecycle.onNext(ViewEvent.DETACH);
        assertTrue(attachSub.isUnsubscribed());
    }

    @Test
    public void testBindView() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View view = new View(activity);
        CopyOnWriteArrayList<View.OnAttachStateChangeListener> listeners = TestUtil.getAttachStateChangeListeners(view);

        // Do the attach notification
        if (listeners != null) {
            for (View.OnAttachStateChangeListener listener : listeners) {
                listener.onViewAttachedToWindow(view);
            }
        }

        // Subscribe
        Subscription viewAttachSub = observable.compose(RxLifecycle.bindView(view)).subscribe();
        assertFalse(viewAttachSub.isUnsubscribed());
        listeners = TestUtil.getAttachStateChangeListeners(view);
        assertNotNull(listeners);
        assertFalse(listeners.isEmpty());

        // Now detach
        for (View.OnAttachStateChangeListener listener : listeners) {
            listener.onViewDetachedFromWindow(view);
        }
        assertTrue(viewAttachSub.isUnsubscribed());
    }
}
