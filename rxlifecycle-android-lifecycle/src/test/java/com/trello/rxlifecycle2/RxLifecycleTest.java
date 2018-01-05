package com.trello.rxlifecycle2;

import android.arch.lifecycle.Lifecycle;

import com.trello.lifecycle2.android.lifecycle.RxLifecycleAndroidLifecycle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxLifecycleTest {

    private PublishSubject<Object> stream;

    @Before
    public void setup() {
        // Simulate an actual lifecycle (hot Observable that does not end)
        stream = PublishSubject.create();
    }

    @Test
    public void testBindUntilLifecycleEvent() {
        BehaviorSubject<Lifecycle.Event> lifecycle = BehaviorSubject.create();

        TestObserver<Object> testObserver =
                stream.hide().compose(RxLifecycle.bindUntilEvent(lifecycle, Lifecycle.Event.ON_STOP)).test();

        lifecycle.onNext(Lifecycle.Event.ON_CREATE);
        stream.onNext("create");
        lifecycle.onNext(Lifecycle.Event.ON_START);
        stream.onNext("start");
        lifecycle.onNext(Lifecycle.Event.ON_RESUME);
        stream.onNext("resume");
        lifecycle.onNext(Lifecycle.Event.ON_PAUSE);
        stream.onNext("pause");
        lifecycle.onNext(Lifecycle.Event.ON_STOP);
        stream.onNext("stop");
        testObserver.assertValues("create", "start", "resume", "pause");
        testObserver.assertNotComplete();
    }

    @Test
    public void testBindLifecycle() {
        BehaviorSubject<Lifecycle.Event> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(Lifecycle.Event.ON_CREATE);
        TestObserver<Object> createObserver = stream.hide().compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        stream.onNext("create");

        lifecycle.onNext(Lifecycle.Event.ON_START);
        TestObserver<Object> startObserver = stream.hide().compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        stream.onNext("start");

        lifecycle.onNext(Lifecycle.Event.ON_RESUME);
        TestObserver<Object> resumeObserver = stream.hide().compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        stream.onNext("resume");

        lifecycle.onNext(Lifecycle.Event.ON_PAUSE);
        TestObserver<Object> pauseObserver = stream.hide().compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        stream.onNext("pause");
        resumeObserver.assertNotComplete();
        resumeObserver.assertValues("resume");

        lifecycle.onNext(Lifecycle.Event.ON_STOP);
        TestObserver<Object> stopObserver = stream.hide().compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        stream.onNext("stop");
        startObserver.assertNotComplete();
        startObserver.assertValues("start", "resume", "pause");
        pauseObserver.assertNotComplete();
        pauseObserver.assertValues("pause");

        lifecycle.onNext(Lifecycle.Event.ON_DESTROY);
        stream.onNext("destroy");
        createObserver.assertNotComplete();
        createObserver.assertValues("create", "start", "resume", "pause", "stop");
        stopObserver.assertNotComplete();
        stopObserver.assertValues("stop");
    }

    @Test
    public void testEndsImmediatelyOutsideLifecycle() {
        BehaviorSubject<Lifecycle.Event> lifecycle = BehaviorSubject.create();
        lifecycle.onNext(Lifecycle.Event.ON_DESTROY);
        stream.onNext("destroy");

        TestObserver<Object> testObserver = stream.hide().compose(RxLifecycleAndroidLifecycle.bindLifecycle(lifecycle)).test();
        testObserver.assertNoValues();
        testObserver.assertNotComplete();
    }

    // Null checks

    @Test(expected = NullPointerException.class)
    public void testBindLifecycleThrowsOnNull() {
        //noinspection ResourceType
        RxLifecycleAndroidLifecycle.bindLifecycle(null);
    }
}
