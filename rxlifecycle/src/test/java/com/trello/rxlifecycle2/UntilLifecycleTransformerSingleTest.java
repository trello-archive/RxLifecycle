package com.trello.rxlifecycle2;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class UntilLifecycleTransformerSingleTest {

    PublishSubject<String> lifecycle;
    TestScheduler testScheduler; // Since Single is not backpressure aware, use this to simulate it taking time

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testScheduler = new TestScheduler();
    }

    @Test
    public void noEvent() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilLifecycleTransformer<String, String>(lifecycle))
            .test();

        testObserver.assertNoValues();

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneEvent() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilLifecycleTransformer<String, String>(lifecycle))
            .test();

        testObserver.assertNoValues();
        testObserver.assertNoErrors();

        lifecycle.onNext("stop");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testObserver.assertNoValues();
        testObserver.assertError(CancellationException.class);
    }
}