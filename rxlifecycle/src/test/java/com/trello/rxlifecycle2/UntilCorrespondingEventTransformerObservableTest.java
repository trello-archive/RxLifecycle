package com.trello.rxlifecycle2;

import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

public class UntilCorrespondingEventTransformerObservableTest {

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
            .compose(RxLifecycle.<String, String>bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        stream.onNext("1");
        stream.onNext("2");
        testObserver.assertValues("1", "2");
        testObserver.assertNotTerminated();
    }

    @Test
    public void oneStartEvent() {
        TestObserver<String> testObserver = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        stream.onNext("1");
        stream.onNext("2");

        testObserver.assertValues("1", "2");
        testObserver.assertNotTerminated();
    }

    @Test
    public void twoOpenEvents() {
        TestObserver<String> testObserver = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        stream.onNext("1");
        lifecycle.onNext("start");
        stream.onNext("2");

        testObserver.assertValues("1", "2");
        testObserver.assertNotTerminated();
    }

    @Test
    public void openAndCloseEvent() {
        TestObserver<String> testObserver = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        stream.onNext("1");
        lifecycle.onNext("destroy");
        stream.onNext("2");

        testObserver.assertValues("1");
        testObserver.assertComplete();
    }

    private static final Function<String, String> CORRESPONDING_EVENTS = new Function<String, String>() {
        @Override
        public String apply(String s) throws Exception {
            if (s.equals("create")) {
                return "destroy";
            }

            throw new IllegalArgumentException("Cannot handle: " + s);
        }
    };
}