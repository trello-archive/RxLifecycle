package com.trello.rxlifecycle2;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

public class UntilEventTransformerObservableTest {

    PublishSubject<String> stream;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        stream = PublishSubject.create();
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvents() {
        TestObserver<String> testObserver = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        stream.onNext("2");
        testObserver.assertValues("1", "2");
        testObserver.assertNotTerminated();
    }

    @Test
    public void oneWrongEvent() {
        TestObserver<String> testObserver = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        lifecycle.onNext("keep going");
        stream.onNext("2");

        testObserver.assertValues("1", "2");
        testObserver.assertNotTerminated();
    }

    @Test
    public void twoEvents() {
        TestObserver<String> testObserver = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        lifecycle.onNext("keep going");
        stream.onNext("2");
        lifecycle.onNext("stop");
        stream.onNext("3");

        testObserver.assertValues("1", "2");
        testObserver.assertComplete();
    }

}