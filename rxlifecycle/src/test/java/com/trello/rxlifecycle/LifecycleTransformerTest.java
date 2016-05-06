package com.trello.rxlifecycle;

import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;

import static org.junit.Assert.*;

public class LifecycleTransformerTest {

    Observable<String> lifecycle = Observable.never();

    @Test
    public void correspondingEventObservableConversionEqualsSingle() {
        UntilCorrespondingEventObservableTransformer<String, String> observableTransformer =
            new UntilCorrespondingEventObservableTransformer<>(lifecycle, CORRESPONDING_EVENTS);

        UntilCorrespondingEventSingleTransformer<String, String> singleTransformer =
            new UntilCorrespondingEventSingleTransformer<>(lifecycle, CORRESPONDING_EVENTS);

        assertEquals(singleTransformer, observableTransformer.forSingle());
    }

    @Test
    public void correspondingEventObservableConversionEqualsCompletable() {
        UntilCorrespondingEventObservableTransformer<String, String> observableTransformer =
            new UntilCorrespondingEventObservableTransformer<>(lifecycle, CORRESPONDING_EVENTS);

        UntilCorrespondingEventCompletableTransformer<String> completableTransformer =
            new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS);

        assertEquals(completableTransformer, observableTransformer.forCompletable());
    }

    @Test
    public void untilEventObservableConversionEqualsSingle() {
        UntilEventObservableTransformer<String, String> observableTransformer =
            new UntilEventObservableTransformer<>(lifecycle, "stop");

        UntilEventSingleTransformer<String, String> singleTransformer =
            new UntilEventSingleTransformer<>(lifecycle, "stop");

        assertEquals(singleTransformer, observableTransformer.forSingle());
    }

    @Test
    public void untilEventObservableConversionEqualsCompletable() {
        UntilEventObservableTransformer<String, String> observableTransformer =
            new UntilEventObservableTransformer<>(lifecycle, "stop");

        UntilEventCompletableTransformer<String> completableTransformer =
            new UntilEventCompletableTransformer<>(lifecycle, "stop");

        assertEquals(completableTransformer, observableTransformer.forCompletable());
    }

    @Test
    public void untilLifecycleObservableConversionEqualsSingle() {
        UntilLifecycleObservableTransformer<String, String> observableTransformer =
            new UntilLifecycleObservableTransformer<>(lifecycle);

        UntilLifecycleSingleTransformer<String, String> singleTransformer =
            new UntilLifecycleSingleTransformer<>(lifecycle);

        assertEquals(singleTransformer, observableTransformer.forSingle());
    }

    @Test
    public void untilLifecycleObservableConversionEqualsCompletable() {
        UntilLifecycleObservableTransformer<String, String> observableTransformer =
            new UntilLifecycleObservableTransformer<>(lifecycle);

        UntilLifecycleCompletableTransformer<String> completableTransformer =
            new UntilLifecycleCompletableTransformer<>(lifecycle);

        assertEquals(completableTransformer, observableTransformer.forCompletable());
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