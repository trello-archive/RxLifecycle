package com.trello.rxlifecycle2;

import io.reactivex.Maybe;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

public class UntilLifecycleTransformerMaybeTest {

    PublishSubject<String> stream;
    Maybe maybe;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        stream = PublishSubject.create();
        maybe = stream.firstElement();
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvent() {
        TestObserver<String> testObserver = maybe
            .compose(RxLifecycle.<String, String>bind(lifecycle))
            .test();

        stream.onNext("1");
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneEvent() {
        TestObserver<String> testObserver = maybe
            .compose(RxLifecycle.<String, String>bind(lifecycle))
            .test();

        lifecycle.onNext("stop");
        stream.onNext("1");

        testObserver.assertNoValues();
        testObserver.assertComplete();
    }
}