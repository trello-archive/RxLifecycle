package com.trello.rxlifecycle;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class UntilEventTransformerSingleTest {

    PublishSubject<String> lifecycle;
    TestScheduler testScheduler; // Since Single is not backpressure aware, use this to simulate it taking time

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testScheduler = new TestScheduler();
    }

    @Test
    public void noEvents() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        testObserver.assertNoValues();

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneWrongEvent() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        testObserver.assertNoValues();

        lifecycle.onNext("keep going");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void twoEvents() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        lifecycle.onNext("keep going");
        testObserver.assertNoErrors();

        lifecycle.onNext("stop");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testObserver.assertNoValues();
        testObserver.assertError(CancellationException.class);
    }

}