/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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