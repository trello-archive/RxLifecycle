package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class UntilCorrespondingEventSingleTransformerTest {

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
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneStartEvent() {
        Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void twoOpenEvents() {
        Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void openAndCloseEvent() {
        Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        lifecycle.onNext("destroy");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testSubscriber.assertNoValues();
        testSubscriber.assertError(CancellationException.class);
    }

    private static final Func1<String, String> CORRESPONDING_EVENTS = new Func1<String, String>() {
        @Override
        public String call(String s) {
            if (s.equals("create")) {
                return "destroy";
            }

            throw new IllegalArgumentException("Cannot handle: " + s);
        }
    };
}