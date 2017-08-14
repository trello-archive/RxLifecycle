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

package com.trello.rxlifecycle2;

import android.app.Activity;
import android.view.View;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
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
        testObserver.assertNotComplete();
        lifecycle.onNext(FragmentEvent.CREATE);
        testObserver.assertNotComplete();
        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        testObserver.assertNotComplete();
        lifecycle.onNext(FragmentEvent.START);
        testObserver.assertNotComplete();
        lifecycle.onNext(FragmentEvent.RESUME);
        testObserver.assertNotComplete();
        lifecycle.onNext(FragmentEvent.PAUSE);
        testObserver.assertNotComplete();
        testObserver.assertNotComplete();
        lifecycle.onNext(FragmentEvent.STOP);
        testObserver.assertComplete();
    }

    @Test
    public void testBindUntilActivityEvent() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        TestObserver<Object> testObserver =
            observable.compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.STOP)).test();

        lifecycle.onNext(ActivityEvent.CREATE);
        testObserver.assertNotComplete();
        lifecycle.onNext(ActivityEvent.START);
        testObserver.assertNotComplete();
        lifecycle.onNext(ActivityEvent.RESUME);
        testObserver.assertNotComplete();
        lifecycle.onNext(ActivityEvent.PAUSE);
        testObserver.assertNotComplete();
        lifecycle.onNext(ActivityEvent.STOP);
        testObserver.assertComplete();
    }

    @Test
    public void testBindActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(ActivityEvent.CREATE);
        TestObserver<Object> createObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.START);
        createObserver.assertNotComplete();
        TestObserver<Object> startObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.RESUME);
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        TestObserver<Object> resumeObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.PAUSE);
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        resumeObserver.assertComplete();

        TestObserver<Object> pauseObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        lifecycle.onNext(ActivityEvent.STOP);
        createObserver.assertNotComplete();
        startObserver.assertComplete();
        pauseObserver.assertComplete();
        TestObserver<Object> stopObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.DESTROY);
        createObserver.assertComplete();
        stopObserver.assertComplete();
    }

    @Test
    public void testEndsImmediatelyOutsideActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(ActivityEvent.DESTROY);

        TestObserver<Object> testObserver = observable.compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        testObserver.assertComplete();
    }

    @Test
    public void testBindFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(FragmentEvent.ATTACH);
        TestObserver<Object> attachObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.CREATE);
        attachObserver.assertNotComplete();
        TestObserver<Object> createObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        TestObserver<Object> createViewObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.START);
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        TestObserver<Object> startObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.RESUME);
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertNotComplete();
        TestObserver<Object> resumeObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.PAUSE);
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertNotComplete();
        resumeObserver.assertComplete();
        TestObserver<Object> pauseObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.STOP);
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertComplete();
        pauseObserver.assertComplete();
        TestObserver<Object> stopObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.DESTROY_VIEW);
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertComplete();
        stopObserver.assertComplete();
        TestObserver<Object> destroyViewObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle))
            .test();

        lifecycle.onNext(FragmentEvent.DESTROY);
        attachObserver.assertNotComplete();
        createObserver.assertComplete();
        destroyViewObserver.assertComplete();
        TestObserver<Object> destroyObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.DETACH);
        attachObserver.assertComplete();
        destroyObserver.assertComplete();
    }

    @Test
    public void testEndsImmediatelyOutsideFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(FragmentEvent.DETACH);

        TestObserver<Object> testObserver = observable.compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        testObserver.assertComplete();
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
        viewAttachObserver.assertNotComplete();
        listeners = TestUtil.getAttachStateChangeListeners(view);
        assertNotNull(listeners);
        assertFalse(listeners.isEmpty());

        // Now detach
        for (View.OnAttachStateChangeListener listener : listeners) {
            listener.onViewDetachedFromWindow(view);
        }
        viewAttachObserver.assertComplete();
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
