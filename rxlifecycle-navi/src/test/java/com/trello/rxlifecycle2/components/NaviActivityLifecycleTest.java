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

import android.os.Bundle;
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

import static com.trello.navi2.internal.NaviEmitter.createActivityEmitter;
import static com.trello.rxlifecycle2.android.ActivityEvent.STOP;
import static com.trello.rxlifecycle2.navi.NaviLifecycle.createActivityLifecycleProvider;
import static io.reactivex.subjects.PublishSubject.create;
import static org.junit.Assert.assertNull;

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
    public void testNonLifecycleEvents() {
        NaviEmitter activity = NaviEmitter.createActivityEmitter();
        LifecycleProvider<ActivityEvent> provider = NaviLifecycle.createActivityLifecycleProvider(activity);
        TestObserver<ActivityEvent> testObserver = provider.lifecycle().test();
        activity.onViewStateRestored(new Bundle());
        testObserver.assertNoValues();
        testObserver.assertNoErrors();
    }

    @Test
    public void testBindUntilEvent() {
        NaviEmitter activity = createActivityEmitter();
        LifecycleProvider<ActivityEvent> provider = createActivityLifecycleProvider(activity);

        Observable<Object> observable = create().hide();
        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(STOP)).test();

        activity.onCreate(null);
        testObserver.assertNotComplete();
        activity.onStart();
        testObserver.assertNotComplete();
        activity.onResume();
        testObserver.assertNotComplete();
        activity.onPause();
        testObserver.assertNotComplete();
        activity.onStop();
        testObserver.assertComplete();
    }

    @Test
    public void testBindToLifecycle() {
        NaviEmitter activity = createActivityEmitter();
        LifecycleProvider<ActivityEvent> provider = createActivityLifecycleProvider(activity);

        Observable<Object> observable = create().hide();

        activity.onCreate(null);
        TestObserver<Object> createObserver = observable.compose(provider.bindToLifecycle()).test();

        activity.onStart();
        createObserver.assertNotComplete();
        TestObserver<Object> startObserver = observable.compose(provider.bindToLifecycle()).test();

        activity.onResume();
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        TestObserver<Object> resumeTestSub = observable.compose(provider.bindToLifecycle()).test();

        activity.onPause();
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        resumeTestSub.assertComplete();
        TestObserver<Object> pauseObserver = observable.compose(provider.bindToLifecycle()).test();

        activity.onStop();
        createObserver.assertNotComplete();
        startObserver.assertComplete();
        pauseObserver.assertComplete();
        TestObserver<Object> stopObserver = observable.compose(provider.bindToLifecycle()).test();

        activity.onDestroy();
        createObserver.assertComplete();
        stopObserver.assertComplete();
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
