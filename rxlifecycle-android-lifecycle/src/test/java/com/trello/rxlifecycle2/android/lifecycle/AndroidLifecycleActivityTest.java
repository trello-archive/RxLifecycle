package com.trello.rxlifecycle2.android.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRuntimeTrojanProvider;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 17)
public class AndroidLifecycleActivityTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().hide();
        // LifecycleRegistryOwner requires a content provider to init correctly.
        Robolectric.buildContentProvider(LifecycleRuntimeTrojanProvider.class).create();
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

        TestObserver<Object> testObserver = observable.compose(activity.bindUntilEvent(Lifecycle.Event.ON_STOP)).test();

        controller.create();
        testObserver.assertNotComplete();
        controller.start();
        testObserver.assertNotComplete();
        controller.resume();
        testObserver.assertNotComplete();
        controller.pause();
        testObserver.assertNotComplete();
        controller.stop();
        testObserver.assertComplete();
    }

    // Tests bindToLifecycle for any given AndroidLifecycle Activity implementation
    private void testBindToLifecycle(ActivityController<? extends LifecycleOwner> controller) {
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(controller.get());

        controller.create();
        TestObserver<Object> createObserver = observable.compose(provider.bindToLifecycle()).test();

        controller.start();
        createObserver.assertNotComplete();
        TestObserver<Object> startObserver = observable.compose(provider.bindToLifecycle()).test();

        controller.resume();
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        TestObserver<Object> resumeObserver = observable.compose(provider.bindToLifecycle()).test();

        controller.pause();
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        resumeObserver.assertComplete();
        TestObserver<Object> pauseObserver = observable.compose(provider.bindToLifecycle()).test();

        controller.stop();
        createObserver.assertNotComplete();
        startObserver.assertComplete();
        pauseObserver.assertComplete();
        TestObserver<Object> stopObserver = observable.compose(provider.bindToLifecycle()).test();

        controller.destroy();
        createObserver.assertComplete();
        stopObserver.assertComplete();
    }
}
