package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class UntilEventSingleTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;
    TestScheduler testScheduler; // Since Single is not backpressure aware, use this to simulate it taking time

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>();
        testScheduler = new TestScheduler();
    }

    @Test
    public void noEvents() {
        Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneWrongEvent() {
        Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        lifecycle.onNext("keep going");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void twoEvents() {
        Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        lifecycle.onNext("keep going");
        lifecycle.onNext("stop");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testSubscriber.assertNoValues();
        testSubscriber.assertError(CancellationException.class);
    }

}