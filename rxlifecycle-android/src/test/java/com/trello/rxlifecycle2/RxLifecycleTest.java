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

    private PublishSubject<Object> stream;

    @Before
    public void setup() {
        stream = PublishSubject.create();
    }

    @Test
    public void testBindUntilFragmentEvent() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        TestObserver<Object> testObserver =
            stream.hide().compose(RxLifecycle.bindUntilEvent(lifecycle, FragmentEvent.STOP)).test();

        lifecycle.onNext(FragmentEvent.ATTACH);
        stream.onNext("attach");
        lifecycle.onNext(FragmentEvent.CREATE);
        stream.onNext("create");
        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        stream.onNext("createView");
        lifecycle.onNext(FragmentEvent.START);
        stream.onNext("start");
        lifecycle.onNext(FragmentEvent.RESUME);
        stream.onNext("resume");
        lifecycle.onNext(FragmentEvent.PAUSE);
        stream.onNext("pause");
        lifecycle.onNext(FragmentEvent.STOP);
        stream.onNext("stop");
        testObserver.assertValues("attach", "create", "createView", "start", "resume", "pause");
        testObserver.assertNotComplete();
    }

    @Test
    public void testBindUntilActivityEvent() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        TestObserver<Object> testObserver =
            stream.hide().compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.STOP)).test();

        lifecycle.onNext(ActivityEvent.CREATE);
        stream.onNext("create");
        lifecycle.onNext(ActivityEvent.START);
        stream.onNext("start");
        lifecycle.onNext(ActivityEvent.RESUME);
        stream.onNext("resume");
        lifecycle.onNext(ActivityEvent.PAUSE);
        stream.onNext("pause");
        lifecycle.onNext(ActivityEvent.STOP);
        stream.onNext("stop");
        testObserver.assertValues("create", "start", "resume", "pause");
        testObserver.assertNotComplete();
    }

    @Test
    public void testBindActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(ActivityEvent.CREATE);
        TestObserver<Object> createObserver = stream.hide().compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        stream.onNext("create");

        lifecycle.onNext(ActivityEvent.START);
        TestObserver<Object> startObserver = stream.hide().compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        stream.onNext("start");

        lifecycle.onNext(ActivityEvent.RESUME);
        TestObserver<Object> resumeObserver = stream.hide().compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        stream.onNext("resume");

        lifecycle.onNext(ActivityEvent.PAUSE);
        TestObserver<Object> pauseObserver = stream.hide().compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        stream.onNext("pause");
        resumeObserver.assertNotComplete();
        resumeObserver.assertValues("resume");

        lifecycle.onNext(ActivityEvent.STOP);
        TestObserver<Object> stopObserver = stream.hide().compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        stream.onNext("stop");
        startObserver.assertNotComplete();
        startObserver.assertValues("start", "resume", "pause");
        pauseObserver.assertNotComplete();
        pauseObserver.assertValues("pause");

        lifecycle.onNext(ActivityEvent.DESTROY);
        stream.onNext("destroy");
        createObserver.assertNotComplete();
        createObserver.assertValues("create", "start", "resume", "pause", "stop");
        stopObserver.assertNotComplete();
        stopObserver.assertValues("stop");
    }

    @Test
    public void testEndsImmediatelyOutsideActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(ActivityEvent.DESTROY);

        TestObserver<Object> testObserver = stream.hide().compose(RxLifecycleAndroid.bindActivity(lifecycle)).test();
        stream.onNext("outside");
        testObserver.assertNoValues();
        testObserver.assertNotComplete();
        testObserver.assertNoErrors();
    }

    @Test
    public void testBindFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(FragmentEvent.ATTACH);
        TestObserver<Object> attachObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("attach");

        lifecycle.onNext(FragmentEvent.CREATE);
        TestObserver<Object> createObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("create");

        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        TestObserver<Object> createViewObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("createView");

        lifecycle.onNext(FragmentEvent.START);
        TestObserver<Object> startObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("start");

        lifecycle.onNext(FragmentEvent.RESUME);
        TestObserver<Object> resumeObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("resume");

        lifecycle.onNext(FragmentEvent.PAUSE);
        TestObserver<Object> pauseObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("pause");
        resumeObserver.assertNotComplete();
        resumeObserver.assertValues("resume");

        lifecycle.onNext(FragmentEvent.STOP);
        TestObserver<Object> stopObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("stop");
        startObserver.assertNotComplete();
        startObserver.assertValues("start", "resume", "pause");
        pauseObserver.assertNotComplete();
        pauseObserver.assertValues("pause");

        lifecycle.onNext(FragmentEvent.DESTROY_VIEW);
        TestObserver<Object> destroyViewObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle))
            .test();
        stream.onNext("destroyView");
        createViewObserver.assertNotComplete();
        createViewObserver.assertValues("createView", "start", "resume", "pause", "stop");
        stopObserver.assertNotComplete();
        stopObserver.assertValues("stop");

        lifecycle.onNext(FragmentEvent.DESTROY);
        TestObserver<Object> destroyObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("destroy");
        createObserver.assertNotComplete();
        createObserver.assertValues("create", "createView", "start", "resume", "pause", "stop", "destroyView");
        destroyViewObserver.assertNotComplete();
        destroyViewObserver.assertValues("destroyView");

        lifecycle.onNext(FragmentEvent.DETACH);
        stream.onNext("detach");
        attachObserver.assertNotComplete();
        attachObserver.assertValues("attach", "create", "createView", "start", "resume",
                "pause", "stop", "destroyView", "destroy");
        destroyObserver.assertNotComplete();
        destroyObserver.assertValues("destroy");
    }

    @Test
    public void testEndsImmediatelyOutsideFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(FragmentEvent.DETACH);

        TestObserver<Object> testObserver = stream.hide().compose(RxLifecycleAndroid.bindFragment(lifecycle)).test();
        stream.onNext("outside");
        testObserver.assertNoValues();
        testObserver.assertNotComplete();
        testObserver.assertNoErrors();
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
        TestObserver<Object> viewAttachObserver = stream.hide().compose(RxLifecycleAndroid.bindView(view)).test();
        viewAttachObserver.assertNotComplete();
        listeners = TestUtil.getAttachStateChangeListeners(view);
        assertNotNull(listeners);
        assertFalse(listeners.isEmpty());

        // Now detach
        for (View.OnAttachStateChangeListener listener : listeners) {
            listener.onViewDetachedFromWindow(view);
            stream.onNext("detach");
        }
        viewAttachObserver.assertNoValues();
        viewAttachObserver.assertNotComplete();
        viewAttachObserver.assertNoErrors();
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
