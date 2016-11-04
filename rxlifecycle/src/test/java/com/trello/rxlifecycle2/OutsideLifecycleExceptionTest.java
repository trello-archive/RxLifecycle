/**
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

import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;

public class OutsideLifecycleExceptionTest {

    @Test
    public void eventOutOfLifecycle() {
        PublishSubject<String> stream = PublishSubject.create();
        PublishSubject<String> lifecycle = PublishSubject.create();

        TestObserver<String> testObserver = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        // Event is out of lifecycle, but this just results in completing the stream
        lifecycle.onNext("destroy");
        stream.onNext("1");

        testObserver.assertNoValues();
        testObserver.assertComplete();
    }

    @Test
    public void eventThrowsBadException() {
        PublishSubject<String> stream = PublishSubject.create();
        PublishSubject<String> lifecycle = PublishSubject.create();

        TestObserver<String> testObserver = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        // We get an error from the function for this lifecycle event
        lifecycle.onNext("ick");
        stream.onNext("1");

        testObserver.assertNoValues();

        // We only want to check for our IllegalArgumentException, but may have
        // to wade through a CompositeException to get at it.
        testObserver.assertError(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                if (throwable instanceof CompositeException) {
                    CompositeException ce = (CompositeException) throwable;
                    for (Throwable t : ce.getExceptions()) {
                        if (t instanceof IllegalArgumentException) {
                            return true;
                        }
                    }
                }

                return false;
            }
        });
    }

    private static final Function<String, String> CORRESPONDING_EVENTS = new Function<String, String>() {
        @Override
        public String apply(String s) throws Exception {
            if (s.equals("destroy")) {
                throw new OutsideLifecycleException("");
            }

            throw new IllegalArgumentException("Cannot handle: " + s);
        }
    };
}
