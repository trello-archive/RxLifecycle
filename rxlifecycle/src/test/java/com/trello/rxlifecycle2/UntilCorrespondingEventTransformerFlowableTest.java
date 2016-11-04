package com.trello.rxlifecycle2;

import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;

public class UntilCorrespondingEventTransformerFlowableTest {

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
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        stream.onNext("1");
        stream.onNext("2");
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void oneStartEvent() {
        TestSubscriber<String> testSubscriber = stream
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        stream.onNext("1");
        stream.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void twoOpenEvents() {
        TestSubscriber<String> testSubscriber = stream
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        stream.onNext("1");
        lifecycle.onNext("start");
        stream.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void openAndCloseEvent() {
        TestSubscriber<String> testSubscriber = stream
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        stream.onNext("1");
        lifecycle.onNext("destroy");
        stream.onNext("2");

        testSubscriber.assertValues("1");
        testSubscriber.assertComplete();
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