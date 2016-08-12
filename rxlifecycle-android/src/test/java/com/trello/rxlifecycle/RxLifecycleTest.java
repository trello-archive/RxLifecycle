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
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import java.util.concurrent.CopyOnWriteArrayList;

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
        BehaviorSubject<com.trello.rxlifecycle.android.FragmentEvent> lifecycle = BehaviorSubject.create();
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();

        observable.compose(
            RxLifecycle.bindUntilEvent(lifecycle, FragmentEvent.STOP))
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

        observable.compose(
            RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.STOP))
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
        observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).subscribe(createTestSub);

        lifecycle.onNext(ActivityEvent.START);
        assertFalse(createTestSub.isUnsubscribed());
        TestSubscriber<Object> startTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).subscribe(startTestSub);

        lifecycle.onNext(ActivityEvent.RESUME);
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        TestSubscriber<Object> resumeTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).subscribe(resumeTestSub);

        lifecycle.onNext(ActivityEvent.PAUSE);
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        resumeTestSub.assertCompleted();
        resumeTestSub.assertUnsubscribed();
        TestSubscriber<Object> pauseTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).subscribe(pauseTestSub);

        lifecycle.onNext(ActivityEvent.STOP);
        assertFalse(createTestSub.isUnsubscribed());
        startTestSub.assertCompleted();
        startTestSub.assertUnsubscribed();
        pauseTestSub.assertCompleted();
        pauseTestSub.assertUnsubscribed();
        TestSubscriber<Object> stopTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).subscribe(stopTestSub);

        lifecycle.onNext(ActivityEvent.DESTROY);
        createTestSub.assertCompleted();
        createTestSub.assertUnsubscribed();
        stopTestSub.assertCompleted();
        stopTestSub.assertUnsubscribed();
    }

    @Test
    public void testEndsImmediatelyOutsideActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(ActivityEvent.DESTROY);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testBindFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(FragmentEvent.ATTACH);
        TestSubscriber<Object> attachTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(attachTestSub);

        lifecycle.onNext(FragmentEvent.CREATE);
        assertFalse(attachTestSub.isUnsubscribed());
        TestSubscriber<Object> createTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(createTestSub);

        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        TestSubscriber<Object> createViewTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(createViewTestSub);

        lifecycle.onNext(FragmentEvent.START);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        TestSubscriber<Object> startTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(startTestSub);

        lifecycle.onNext(FragmentEvent.RESUME);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        TestSubscriber<Object> resumeTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(resumeTestSub);

        lifecycle.onNext(FragmentEvent.PAUSE);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        resumeTestSub.assertCompleted();
        resumeTestSub.assertUnsubscribed();
        TestSubscriber<Object> pauseTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(pauseTestSub);

        lifecycle.onNext(FragmentEvent.STOP);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        startTestSub.assertCompleted();
        startTestSub.assertUnsubscribed();
        pauseTestSub.assertCompleted();
        pauseTestSub.assertUnsubscribed();
        TestSubscriber<Object> stopTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(stopTestSub);

        lifecycle.onNext(FragmentEvent.DESTROY_VIEW);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        createViewTestSub.assertCompleted();
        createViewTestSub.assertUnsubscribed();
        stopTestSub.assertCompleted();
        stopTestSub.assertUnsubscribed();
        TestSubscriber<Object> desroyViewTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(desroyViewTestSub);

        lifecycle.onNext(FragmentEvent.DESTROY);
        assertFalse(attachTestSub.isUnsubscribed());
        createTestSub.assertCompleted();
        createTestSub.assertUnsubscribed();
        desroyViewTestSub.assertCompleted();
        desroyViewTestSub.assertUnsubscribed();
        TestSubscriber<Object> destroyTestSub = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(destroyTestSub);

        lifecycle.onNext(FragmentEvent.DETACH);
        attachTestSub.assertCompleted();
        attachTestSub.assertUnsubscribed();
        destroyTestSub.assertCompleted();
        destroyTestSub.assertUnsubscribed();
    }

    @Test
    public void testEndsImmediatelyOutsideFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(FragmentEvent.DETACH);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testBindLifecycle() {
        BehaviorSubject<Object> lifecycle = BehaviorSubject.create();
        Subscription attachSub = observable.compose(RxLifecycle.bind(lifecycle)).subscribe();
        assertFalse(attachSub.isUnsubscribed());
        lifecycle.onNext(new Object());
        assertTrue(attachSub.isUnsubscribed());
    }

    @Test
    public void testBindLifecycleOtherObject() {
        // Ensures it works with other types as well, and not just "Object"
        BehaviorSubject<String> lifecycle = BehaviorSubject.create();
        Subscription attachSub = observable.compose(RxLifecycle.bind(lifecycle)).subscribe();
        assertFalse(attachSub.isUnsubscribed());
        lifecycle.onNext("");
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
        Subscription viewAttachSub = observable.compose(RxLifecycleAndroid.bindView(view)).subscribe();
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

    // Null checks

    @Test(expected=NullPointerException.class)
    public void testBindUntilFragmentEventThrowsOnNullLifecycle() {
        //noinspection ResourceType
        RxLifecycle.bindUntilEvent(null, FragmentEvent.CREATE);
    }

    @Test(expected=NullPointerException.class)
    public void testBindUntilFragmentEventThrowsOnNullEvent() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();
        //noinspection ResourceType
        RxLifecycle.bindUntilEvent(lifecycle, null);
    }

    @Test(expected=NullPointerException.class)
    public void testBindFragmentThrowsOnNull() {
        //noinspection ResourceType
        RxLifecycleAndroid.bindFragment(null);
    }

    @Test(expected=NullPointerException.class)
    public void testBindUntilActivityThrowsOnNullLifecycle() {
        //noinspection ResourceType
        RxLifecycle.bindUntilEvent(null, ActivityEvent.CREATE);
    }

    @Test(expected=NullPointerException.class)
    public void testBindUntilActivityEventThrowsOnNullEvent() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
        //noinspection ResourceType
        RxLifecycle.bindUntilEvent(lifecycle, null);
    }

    @Test(expected=NullPointerException.class)
    public void testBindActivityThrowsOnNull() {
        //noinspection ResourceType
        RxLifecycleAndroid.bindActivity(null);
    }

    @Test(expected=NullPointerException.class)
    public void testBindViewThrowsOnNullView() {
        //noinspection ResourceType
        RxLifecycleAndroid.bindView((View) null);
    }

    @Test(expected=NullPointerException.class)
    public void testBindThrowsOnNullLifecycle() {
        //noinspection ResourceType
        RxLifecycle.bind((Observable) null);
    }
}
