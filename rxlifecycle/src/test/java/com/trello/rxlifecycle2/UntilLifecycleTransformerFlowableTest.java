package com.trello.rxlifecycle2;

import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;

public class UntilLifecycleTransformerFlowableTest {

    PublishProcessor<String> stream;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        stream = PublishProcessor.create();
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvent() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle))
            .test();

        stream.onNext("1");
        stream.onNext("2");
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void oneEvent() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle))
            .test();

        stream.onNext("1");
        lifecycle.onNext("stop");
        stream.onNext("2");

        testSubscriber.assertValues("1");
        testSubscriber.assertComplete();
    }
}