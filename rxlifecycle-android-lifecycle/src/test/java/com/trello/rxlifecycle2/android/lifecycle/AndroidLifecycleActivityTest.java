package com.trello.rxlifecycle2.android.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleOwner;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 17)
public class AndroidLifecycleActivityTest {

    private PublishSubject<Object> stream;

    @Before
    public void setup() {
        stream = PublishSubject.create();
    }

    @Test
    public void testLifecycleActivity() {
        testLifecycle(Robolectric.buildActivity(LifecycleActivity.class));
        testBindUntilEvent(Robolectric.buildActivity(LifecycleActivity.class));
        testBindToLifecycle(Robolectric.buildActivity(LifecycleActivity.class));
    }


    private void testLifecycle(ActivityController<? extends LifecycleOwner> controller) {
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(controller.get());

        TestObserver<Lifecycle.Event> testObserver = provider.lifecycle().test();

        controller.create();
        controller.start();
        controller.resume();
        controller.pause();
        controller.stop();
        controller.destroy();

        testObserver.assertValues(
                Lifecycle.Event.ON_CREATE,
                Lifecycle.Event.ON_START,
                Lifecycle.Event.ON_RESUME,
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP,
                Lifecycle.Event.ON_DESTROY
        );
    }

    // Tests bindUntil for any given AndroidLifecycle Activity implementation
    private void testBindUntilEvent(ActivityController<? extends LifecycleOwner> controller) {
        LifecycleProvider<Lifecycle.Event> activity = AndroidLifecycle.createLifecycleProvider(controller.get());

        TestObserver<Object> testObserver = stream.hide().compose(activity.bindUntilEvent(Lifecycle.Event.ON_STOP)).test();

        controller.create();
        stream.onNext("create");
        testObserver.assertNotComplete();
        controller.start();
        stream.onNext("start");
        testObserver.assertNotComplete();
        controller.resume();
        stream.onNext("resume");
        testObserver.assertNotComplete();
        controller.pause();
        stream.onNext("pause");
        testObserver.assertNotComplete();
        controller.stop();
        stream.onNext("stop");
        testObserver.assertValues("create", "start", "resume", "pause");
        testObserver.assertNotComplete();
    }

    // Tests bindToLifecycle for any given AndroidLifecycle Activity implementation
    private void testBindToLifecycle(ActivityController<? extends LifecycleOwner> controller) {
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(controller.get());

        controller.create();
        TestObserver<Object> createObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("create");

        controller.start();
        TestObserver<Object> startObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("start");

        controller.resume();
        TestObserver<Object> resumeObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("resume");

        controller.pause();
        TestObserver<Object> pauseObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("pause");
        resumeObserver.assertNotComplete();
        resumeObserver.assertValues("resume");

        controller.stop();
        TestObserver<Object> stopObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("stop");
        startObserver.assertNotComplete();
        startObserver.assertValues("start", "resume", "pause");
        pauseObserver.assertNotComplete();
        pauseObserver.assertValues("pause");

        controller.destroy();
        stream.onNext("destroy");
        createObserver.assertNotComplete();
        createObserver.assertValues("create", "start", "resume", "pause", "stop");
        stopObserver.assertNotComplete();
        stopObserver.assertValues("stop");
    }
}
