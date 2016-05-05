package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import java.util.concurrent.CancellationException;

public class UntilEventSingleTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvents() {
        Single.just("1")
            .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneWrongEvent() {
        Single.just("1")
            .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        lifecycle.onNext("keep going");
        testSubscriber.requestMore(1);

        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void twoEvents() {
        Single.just("1")
            .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        lifecycle.onNext("keep going");
        lifecycle.onNext("stop");
        testSubscriber.requestMore(1);

        testSubscriber.assertNoValues();
        testSubscriber.assertError(CancellationException.class);
    }

}