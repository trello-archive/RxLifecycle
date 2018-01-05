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

import javax.annotation.ParametersAreNonnullByDefault;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.ArrayCompositeDisposable;

@ParametersAreNonnullByDefault
public final class CompletableDisposeWhen extends Completable {
    private final CompletableSource upstream;
    private final ObservableSource<?> observable;

    public CompletableDisposeWhen(CompletableSource upstream, ObservableSource<?> observable) {
        this.upstream = upstream;
        this.observable = observable;
    }

    @Override
    protected void subscribeActual(final CompletableObserver downstream) {
        final ArrayCompositeDisposable frc = new ArrayCompositeDisposable(2);

        downstream.onSubscribe(frc);

        observable.subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                frc.setResource(0, d);
            }

            @Override
            public void onNext(Object o) {
                frc.dispose();
            }

            @Override
            public void onError(Throwable t) {
                frc.dispose();
                downstream.onError(t);
            }

            @Override
            public void onComplete() {
                frc.dispose();
            }
        });

        upstream.subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                frc.setResource(1, d);
            }

            @Override
            public void onComplete() {
                frc.dispose();
                downstream.onComplete();
            }

            @Override
            public void onError(Throwable t) {
                frc.dispose();
                downstream.onError(t);
            }
        });
    }
}
