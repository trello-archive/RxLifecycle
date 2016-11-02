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
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
        observable = PublishSubject.create().hide();
    }

    @Test
    public void testBindUntilFragmentEvent() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        TestObserver<Object> testObserver =
            observable.compose(RxLifecycle.bindUntilEvent(lifecycle, FragmentEvent.STOP)).test();

        lifecycle.onNext(FragmentEvent.ATTACH);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(FragmentEvent.CREATE);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(FragmentEvent.START);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(FragmentEvent.RESUME);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(FragmentEvent.PAUSE);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(FragmentEvent.STOP);
        testObserver.assertComplete();
        assertTrue(testObserver.isDisposed());
    }

    @Test
    public void testBindUntilActivityEvent() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        TestObserver<Object> testObserver =
            observable.compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.STOP)).test();

        lifecycle.onNext(ActivityEvent.CREATE);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(ActivityEvent.START);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(ActivityEvent.RESUME);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(ActivityEvent.PAUSE);
        assertFalse(testObserver.isDisposed());
        lifecycle.onNext(ActivityEvent.STOP);
        testObserver.assertComplete();
        assertTrue(testObserver.isDisposed());
    }

    @Test
    public void testBindActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(ActivityEvent.CREATE);
        TestObserver<Object> createObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.START);
        assertFalse(createObserver.isDisposed());
        TestObserver<Object> startObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.RESUME);
        assertFalse(createObserver.isDisposed());
        assertFalse(startObserver.isDisposed());
        TestObserver<Object> resumeObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.PAUSE);
        assertFalse(createObserver.isDisposed());
        assertFalse(startObserver.isDisposed());
        resumeObserver.assertComplete();
        assertTrue(resumeObserver.isDisposed());

        TestObserver<Object> pauseObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        lifecycle.onNext(ActivityEvent.STOP);
        assertFalse(createObserver.isDisposed());
        startObserver.assertComplete();
        assertTrue(startObserver.isDisposed());
        pauseObserver.assertComplete();
        assertTrue(pauseObserver.isDisposed());
        TestObserver<Object> stopObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.DESTROY);
        createObserver.assertComplete();
        assertTrue(createObserver.isDisposed());
        stopObserver.assertComplete();
        assertTrue(stopObserver.isDisposed());
    }

    @Test
    public void testEndsImmediatelyOutsideActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(ActivityEvent.DESTROY);

        TestObserver<Object> testObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        testObserver.assertComplete();
        assertTrue(testObserver.isDisposed());
    }

    @Test
    public void testBindFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(FragmentEvent.ATTACH);
        TestObserver<Object> attachObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.CREATE);
        assertFalse(attachObserver.isDisposed());
        TestObserver<Object> createObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        TestObserver<Object> createViewObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.START);
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        assertFalse(createViewObserver.isDisposed());
        TestObserver<Object> startObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.RESUME);
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        assertFalse(createViewObserver.isDisposed());
        assertFalse(startObserver.isDisposed());
        TestObserver<Object> resumeObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.PAUSE);
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        assertFalse(createViewObserver.isDisposed());
        assertFalse(startObserver.isDisposed());
        resumeObserver.assertComplete();
        assertTrue(resumeObserver.isDisposed());
        TestObserver<Object> pauseObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.STOP);
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        assertFalse(createViewObserver.isDisposed());
        startObserver.assertComplete();
        assertTrue(startObserver.isDisposed());
        pauseObserver.assertComplete();
        assertTrue(pauseObserver.isDisposed());
        TestObserver<Object> stopObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.DESTROY_VIEW);
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        createViewObserver.assertComplete();
        assertTrue(createViewObserver.isDisposed());
        stopObserver.assertComplete();
        assertTrue(stopObserver.isDisposed());
        TestObserver<Object> destroyViewObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle))
            .test();

        lifecycle.onNext(FragmentEvent.DESTROY);
        assertFalse(attachObserver.isDisposed());
        createObserver.assertComplete();
        assertTrue(createObserver.isDisposed());
        destroyViewObserver.assertComplete();
        assertTrue(destroyViewObserver.isDisposed());
        TestObserver<Object> destroyObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.DETACH);
        attachObserver.assertComplete();
        assertTrue(attachObserver.isDisposed());
        destroyObserver.assertComplete();
        assertTrue(destroyObserver.isDisposed());
    }

    @Test
    public void testEndsImmediatelyOutsideFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(FragmentEvent.DETACH);

        TestObserver<Object> testObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        testObserver.assertComplete();
        assertTrue(testObserver.isDisposed());
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
        TestObserver<Object> viewAttachObserver = observable.compose(RxLifecycleAndroid.bindView(view)).test();
        assertFalse(viewAttachObserver.isDisposed());
        listeners = TestUtil.getAttachStateChangeListeners(view);
        assertNotNull(listeners);
        assertFalse(listeners.isEmpty());

        // Now detach
        for (View.OnAttachStateChangeListener listener : listeners) {
            listener.onViewDetachedFromWindow(view);
        }
        assertTrue(viewAttachObserver.isDisposed());
    }

    // Null checks

    @Test(expected = NullPointerException.class)
    public void testBindFragmentThrowsOnNull() {
        //noinspection ResourceType
        RxLifecycleAndroid.bindFragment(null);
    }

    @Test(expected = NullPointerException.class)
    public void testBindActivityThrowsOnNull() {
        //noinspection ResourceType
        RxLifecycleAndroid.bindActivity(null);
    }

    @Test(expected = NullPointerException.class)
    public void testBindViewThrowsOnNullView() {
        //noinspection ResourceType
        RxLifecycleAndroid.bindView((View) null);
    }
}
