package com.trello.rxlifecycle2;

import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;

public class UntilEventTransformerFlowableTest {

    PublishProcessor<String> stream;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        stream = PublishProcessor.create();
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvents() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        stream.onNext("2");
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void oneWrongEvent() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        lifecycle.onNext("keep going");
        stream.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void twoEvents() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        lifecycle.onNext("keep going");
        stream.onNext("2");
        lifecycle.onNext("stop");
        stream.onNext("3");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertComplete();
    }

}