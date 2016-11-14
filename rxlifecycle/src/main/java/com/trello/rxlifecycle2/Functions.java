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

import io.reactivex.Completable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import java.util.concurrent.CancellationException;

final class Functions {

    static final Function<Throwable, Boolean> RESUME_FUNCTION = new Function<Throwable, Boolean>() {
        @Override
        public Boolean apply(Throwable throwable) throws Exception {
            if (throwable instanceof OutsideLifecycleException) {
                return true;
            }

            //noinspection ThrowableResultOfMethodCallIgnored
            Exceptions.propagate(throwable);
            return false;
        }
    };

    static final Predicate<Boolean> SHOULD_COMPLETE = new Predicate<Boolean>() {
        @Override
        public boolean test(Boolean shouldComplete) throws Exception {
            return shouldComplete;
        }
    };

    static final Function<Object, Completable> CANCEL_COMPLETABLE = new Function<Object, Completable>() {
        @Override
        public Completable apply(Object ignore) throws Exception {
            return Completable.error(new CancellationException());
        }
    };

    private Functions() {
        throw new AssertionError("No instances!");
    }
}
