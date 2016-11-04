package com.trello.rxlifecycle2;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

public class UntilLifecycleTransformerObservableTest {

    PublishSubject<String> stream;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        stream = PublishSubject.create();
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvent() {
        TestObserver<String> testObserver = stream
            .compose(new UntilLifecycleTransformer<String, String>(lifecycle))
            .test();

        stream.onNext("1");
        stream.onNext("2");
        testObserver.assertValues("1", "2");
        testObserver.assertNotTerminated();
    }

    @Test
    public void oneEvent() {
        TestObserver<String> testObserver = stream
            .compose(new UntilLifecycleTransformer<String, String>(lifecycle))
            .test();

        stream.onNext("1");
        lifecycle.onNext("stop");
        stream.onNext("2");

        testObserver.assertValues("1");
        testObserver.assertComplete();
    }
}