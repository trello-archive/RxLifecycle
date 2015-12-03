package com.trello.rxlifecycle.components;

import com.trello.rxlifecycle.ServiceEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ServiceController;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxServiceLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().asObservable();
    }

    @Test
    public void testRxActivity() {
        testLifecycle(Robolectric.buildService(RxService.class));
        testBindUntilEvent(Robolectric.buildService(RxService.class));
        testBindToLifecycle(Robolectric.buildService(RxService.class));
    }

    private void testLifecycle(ServiceController<? extends ServiceLifecycleProvider> controller) {
        ServiceLifecycleProvider activity = controller.get();

        TestSubscriber<ServiceEvent> testSubscriber = new TestSubscriber<>();
        activity.lifecycle().subscribe(testSubscriber);

        controller.create();
        controller.startCommand(0, 0);
        controller.bind();
        controller.rebind();
        controller.unbind();
        controller.destroy();

        testSubscriber.assertValues(
                ServiceEvent.CREATE,
                ServiceEvent.START_COMMAND,
                ServiceEvent.BIND,
                ServiceEvent.REBIND,
                ServiceEvent.UNBIND,
                ServiceEvent.DESTROY
        );
    }

    // Tests bindUntil for any given RxActivityLifecycle implementation
    private void testBindUntilEvent(ServiceController<? extends ServiceLifecycleProvider> controller) {
        ServiceLifecycleProvider activity = controller.get();

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.compose(activity.bindUntilEvent(ServiceEvent.UNBIND)).subscribe(testSubscriber);

        controller.create();
        assertFalse(testSubscriber.isUnsubscribed());
        controller.startCommand(0, 0);
        assertFalse(testSubscriber.isUnsubscribed());
        controller.bind();
        assertFalse(testSubscriber.isUnsubscribed());
        controller.rebind();
        assertFalse(testSubscriber.isUnsubscribed());
        controller.unbind();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
    }

    // Tests bindToLifecycle for any given RxActivityLifecycle implementation
    private void testBindToLifecycle(ServiceController<? extends ServiceLifecycleProvider> controller) {
        ServiceLifecycleProvider activity = controller.get();

        controller.create();
        TestSubscriber<Object> createTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(createTestSub);

        controller.startCommand(0, 0);
        assertFalse(createTestSub.isUnsubscribed());
        TestSubscriber<Object> startTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(startTestSub);

        controller.bind();
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        TestSubscriber<Object> bindTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(bindTestSub);

        controller.rebind();
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        assertFalse(bindTestSub.isUnsubscribed());
        TestSubscriber<Object> rebindTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(rebindTestSub);

        controller.unbind();
        assertFalse(createTestSub.isUnsubscribed());
        bindTestSub.assertCompleted();
        bindTestSub.assertUnsubscribed();
        rebindTestSub.assertCompleted();
        rebindTestSub.assertUnsubscribed();
        TestSubscriber<Object> unbindTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(unbindTestSub);

        controller.destroy();
        createTestSub.assertCompleted();
        createTestSub.assertUnsubscribed();
        startTestSub.assertCompleted();
        startTestSub.assertUnsubscribed();
        unbindTestSub.assertCompleted();
        unbindTestSub.assertUnsubscribed();
    }
}
