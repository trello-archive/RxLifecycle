/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trello.rxlifecycle2.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.ParametersAreNonnullByDefault;

import io.reactivex.Flowable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.HalfSerializer;

@ParametersAreNonnullByDefault
public final class FlowableCancelWhen<T> extends Flowable<T> {
    private final Publisher<T> upstream;
    private final ObservableSource<?> observable;

    public FlowableCancelWhen(Publisher<T> upstream, ObservableSource<?> observable) {
        this.upstream = upstream;
        this.observable = observable;
    }

    @Override
    protected void subscribeActual(final Subscriber<? super T> downstream) {
        final MainSubscriber mainSubscriber = new MainSubscriber(downstream);

        downstream.onSubscribe(mainSubscriber);

        observable.subscribe(mainSubscriber.other);
        upstream.subscribe(mainSubscriber);
    }

    final class MainSubscriber extends AtomicInteger implements Subscriber<T>, Subscription {
        private static final long serialVersionUID = 919611990130321642L;
        final Subscriber<? super T> actual;
        final OtherObserver other;
        final AtomicReference<Subscription> s;
        final AtomicLong requested;
        final AtomicThrowable error;

        MainSubscriber(Subscriber<? super T> actual) {
            this.actual = actual;
            this.requested = new AtomicLong();
            this.s = new AtomicReference<>();
            this.other = new OtherObserver();
            this.error = new AtomicThrowable();
        }

        @Override
        public void onSubscribe(Subscription s) {
            SubscriptionHelper.deferredSetOnce(this.s, requested, s);
        }

        @Override
        public void onNext(T o) {
            HalfSerializer.onNext(actual, o, this, error);
        }

        @Override
        public void onError(Throwable t) {
            DisposableHelper.dispose(other);
            HalfSerializer.onError(actual, t, this, error);
        }

        @Override
        public void onComplete() {
            DisposableHelper.dispose(other);
            HalfSerializer.onComplete(actual, this, error);
        }

        @Override
        public void request(long n) {
            SubscriptionHelper.deferredRequest(s, requested, n);
        }

        @Override
        public void cancel() {
            SubscriptionHelper.cancel(s);
            DisposableHelper.dispose(other);
        }

        final class OtherObserver extends AtomicReference<Disposable> implements Observer<Object> {
            private static final long serialVersionUID = -6684536082750051972L;

            @Override
            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            @Override
            public void onNext(Object o) {
                DisposableHelper.dispose(this);
                SubscriptionHelper.cancel(s);
            }

            @Override
            public void onError(Throwable t) {
                DisposableHelper.dispose(this);
                SubscriptionHelper.cancel(s);
                HalfSerializer.onError(actual, t, MainSubscriber.this, error);
            }

            @Override
            public void onComplete() {
                DisposableHelper.dispose(this);
                SubscriptionHelper.cancel(s);
            }
        }
    }
}
