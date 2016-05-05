package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import java.util.concurrent.CancellationException;

public class UntilLifecycleSingleTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvent() {
        Single.just("1")
            .compose(new UntilLifecycleSingleTransformer<String, String>(lifecycle))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneEvent() {
        Single.just("1")
            .compose(new UntilLifecycleSingleTransformer<String, String>(lifecycle))
            .subscribe(testSubscriber);

        lifecycle.onNext("stop");
        testSubscriber.requestMore(1);

        testSubscriber.assertNoValues();
        testSubscriber.assertError(CancellationException.class);
    }
}