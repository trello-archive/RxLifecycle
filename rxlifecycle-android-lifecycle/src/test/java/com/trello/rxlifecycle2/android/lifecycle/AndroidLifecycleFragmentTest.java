package com.trello.rxlifecycle2.android.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LifecycleOwner;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
@Config(manifest = Config.NONE)
public class AndroidLifecycleFragmentTest {
    private PublishSubject<Object> stream;

    @Before
    public void setup() {
        stream = PublishSubject.create();
    }

    @Test
    public void testLifecycleFragment() {
        testLifecycle(new LifecycleFragment());
        testBindUntilEvent(new LifecycleFragment());
        testBindToLifecycle(new LifecycleFragment());
    }

    private void testLifecycle(LifecycleOwner owner) {
        Fragment fragment = (Fragment) owner;
        ActivityController<?> controller = startFragment(fragment);

        TestObserver<Lifecycle.Event> testObserver = AndroidLifecycle.createLifecycleProvider(owner).lifecycle().test();

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

    // Tests bindUntil for any given AndroidLifecycle Fragment implementation
    private void testBindUntilEvent(LifecycleOwner owner) {
        Fragment fragment = (Fragment) owner;
        ActivityController<?> controller = startFragment(fragment);

        TestObserver<Object> testObserver = stream.hide().compose(AndroidLifecycle.createLifecycleProvider(owner).bindUntilEvent(Lifecycle.Event.ON_STOP)).test();

        testObserver.assertNotComplete();
        controller.start();
        stream.onNext("create");
        testObserver.assertNotComplete();
        controller.resume();
        stream.onNext("resume");
        testObserver.assertNotComplete();
        controller.pause();
        stream.onNext("pause");
        testObserver.assertNotComplete();
        controller.stop();
        stream.onNext("stop");
        testObserver.assertValues("create", "resume", "pause");
        testObserver.assertNotComplete();
    }

    // Tests bindToLifecycle for any given RxLifecycle Fragment implementation
    private void testBindToLifecycle(LifecycleOwner owner) {
        Fragment fragment = (Fragment) owner;
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(owner);
        ActivityController<?> controller = startFragment(fragment);

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
        stopObserver.assertNotComplete();
        stopObserver.assertValues("stop");
    }

    // Easier than making everyone create their own shadows
    private ActivityController<FragmentActivity> startFragment(Fragment fragment) {
        ActivityController<FragmentActivity> controller = Robolectric.buildActivity(FragmentActivity.class);
        controller.create();
        controller.get().getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow();
        return controller;
    }
}
