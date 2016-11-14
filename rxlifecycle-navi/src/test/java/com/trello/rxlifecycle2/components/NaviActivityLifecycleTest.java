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

package com.trello.rxlifecycle2.components;

import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.internal.NaviEmitter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NaviActivityLifecycleTest {

    @Test
    public void testLifecycle() {
        NaviEmitter activity = NaviEmitter.createActivityEmitter();
        LifecycleProvider<ActivityEvent> provider = NaviLifecycle.createActivityLifecycleProvider(activity);

        TestObserver<ActivityEvent> testObserver = provider.lifecycle().test();

        activity.onCreate(null);
        activity.onStart();
        activity.onResume();
        activity.onPause();
        activity.onStop();
        activity.onDestroy();

        testObserver.assertValues(
            ActivityEvent.CREATE,
            ActivityEvent.START,
            ActivityEvent.RESUME,
            ActivityEvent.PAUSE,
            ActivityEvent.STOP,
            ActivityEvent.DESTROY
        );
    }

    @Test
    public void testBindUntilEvent() {
        NaviEmitter activity = NaviEmitter.createActivityEmitter();
        LifecycleProvider<ActivityEvent> provider = NaviLifecycle.createActivityLifecycleProvider(activity);

        Observable<Object> observable = PublishSubject.create().hide();
        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(ActivityEvent.STOP)).test();

        activity.onCreate(null);
        assertFalse(testObserver.isDisposed());
        activity.onStart();
        assertFalse(testObserver.isDisposed());
        activity.onResume();
        assertFalse(testObserver.isDisposed());
        activity.onPause();
        assertFalse(testObserver.isDisposed());
        activity.onStop();
        testObserver.assertComplete();
        assertTrue(testObserver.isDisposed());
    }

    @Test
    public void testBindToLifecycle() {
        NaviEmitter activity = NaviEmitter.createActivityEmitter();
        LifecycleProvider<ActivityEvent> provider = NaviLifecycle.createActivityLifecycleProvider(activity);

        Observable<Object> observable = PublishSubject.create().hide();

        activity.onCreate(null);
        TestObserver<Object> createObserver = observable.compose(provider.bindToLifecycle()).test();

        activity.onStart();
        assertFalse(createObserver.isDisposed());
        TestObserver<Object> startObserver = observable.compose(provider.bindToLifecycle()).test();

        activity.onResume();
        assertFalse(createObserver.isDisposed());
        assertFalse(startObserver.isDisposed());
        TestObserver<Object> resumeTestSub = observable.compose(provider.bindToLifecycle()).test();

        activity.onPause();
        assertFalse(createObserver.isDisposed());
        assertFalse(startObserver.isDisposed());
        resumeTestSub.assertComplete();
        assertTrue(resumeTestSub.isDisposed());
        TestObserver<Object> pauseObserver = observable.compose(provider.bindToLifecycle()).test();

        activity.onStop();
        assertFalse(createObserver.isDisposed());
        startObserver.assertComplete();
        assertTrue(startObserver.isDisposed());
        pauseObserver.assertComplete();
        assertTrue(pauseObserver.isDisposed());
        TestObserver<Object> stopObserver = observable.compose(provider.bindToLifecycle()).test();

        activity.onDestroy();
        createObserver.assertComplete();
        assertTrue(createObserver.isDisposed());
        stopObserver.assertComplete();
        assertTrue(stopObserver.isDisposed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadHandler() {
        HashSet<Event<?>> notEnoughEvents = new HashSet<>();
        notEnoughEvents.add(Event.CREATE);
        NaviComponent badHandler = new NaviEmitter(notEnoughEvents);
        //noinspection CheckResult
        NaviLifecycle.createActivityLifecycleProvider(badHandler);
    }

    @Test
    public void testPersistance() {
        NaviEmitter activity = NaviEmitter.createActivityEmitter();
        LifecycleProvider<ActivityEvent> provider = NaviLifecycle.createActivityLifecycleProvider(activity);

        TestObserver<ActivityEvent> testObserver = provider.lifecycle().test();

        activity.onCreate(null);
        activity.onStart();
        activity.onResume();
        activity.onPause();
        activity.onStop();
        activity.onDestroy();

        // Verify that you can remain subscribed until the Activity is completely gone
        activity.onCreate(null);

        testObserver.assertValues(
            ActivityEvent.CREATE,
            ActivityEvent.START,
            ActivityEvent.RESUME,
            ActivityEvent.PAUSE,
            ActivityEvent.STOP,
            ActivityEvent.DESTROY,
            ActivityEvent.CREATE
        );
    }

    @Test
    public void testLeakFree() {
        NaviEmitter activity = NaviEmitter.createActivityEmitter();
        LifecycleProvider<ActivityEvent> provider = NaviLifecycle.createActivityLifecycleProvider(activity);
        WeakReference<NaviEmitter> activityRef = new WeakReference<>(activity);
        WeakReference<LifecycleProvider<ActivityEvent>> providerRef = new WeakReference<>(provider);

        Observable<Object> observable = PublishSubject.create().hide();
        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(ActivityEvent.STOP)).test();

        activity.onCreate(null);
        activity.onStart();
        activity.onResume();
        activity.onPause();
        activity.onStop();
        activity.onDestroy();

        activity = null;
        provider = null;
        TestUtil.cleanGarbage();

        assertNull(activityRef.get());
        assertNull(providerRef.get());
    }
}
