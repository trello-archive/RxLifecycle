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

package com.trello.rxlifecycle2.kotlin

import android.view.View
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.*
import io.reactivex.functions.Function

// RxLifecycle extensions

fun <T, E> Observable<T>.bind(lifecycle: Observable<E>): Observable<T>
    = this.compose<T>(RxLifecycle.bind(lifecycle))

fun <T, E> Observable<T>.bindUntilEvent(lifecycle: Observable<E>, event: E): Observable<T>
    = this.compose<T>(RxLifecycle.bindUntilEvent(lifecycle, event))

fun <T, E> Observable<T>.bind(lifecycle: Observable<E>, correspondingEvents: Function<E, E>): Observable<T>
    = this.compose<T>(RxLifecycle.bind(lifecycle, correspondingEvents))

fun <T, E> Flowable<T>.bind(lifecycle: Observable<E>): Flowable<T>
    = this.compose<T>(RxLifecycle.bind(lifecycle))

fun <T, E> Flowable<T>.bindUntilEvent(lifecycle: Observable<E>, event: E): Flowable<T>
    = this.compose<T>(RxLifecycle.bindUntilEvent(lifecycle, event))

fun <T, E> Flowable<T>.bind(lifecycle: Observable<E>, correspondingEvents: Function<E, E>): Flowable<T>
    = this.compose<T>(RxLifecycle.bind(lifecycle, correspondingEvents))

fun <T, E> Single<T>.bind(lifecycle: Observable<E>): Single<T>
    = this.compose<T>(RxLifecycle.bind(lifecycle))

fun <T, E> Single<T>.bindUntilEvent(lifecycle: Observable<E>, event: E): Single<T>
    = this.compose<T>(RxLifecycle.bindUntilEvent(lifecycle, event))

fun <T, E> Single<T>.bind(lifecycle: Observable<E>, correspondingEvents: Function<E, E>): Single<T>
    = this.compose<T>(RxLifecycle.bind(lifecycle, correspondingEvents))

fun <T, E> Maybe<T>.bind(lifecycle: Observable<E>): Maybe<T>
    = this.compose<T>(RxLifecycle.bind(lifecycle))

fun <T, E> Maybe<T>.bindUntilEvent(lifecycle: Observable<E>, event: E): Maybe<T>
    = this.compose<T>(RxLifecycle.bindUntilEvent(lifecycle, event))

fun <T, E> Maybe<T>.bind(lifecycle: Observable<E>, correspondingEvents: Function<E, E>): Maybe<T>
    = this.compose<T>(RxLifecycle.bind(lifecycle, correspondingEvents))

fun <E> Completable.bind(lifecycle: Observable<E>): Completable
    = this.compose(RxLifecycle.bind<Any, E>(lifecycle))

fun <E> Completable.bindUntilEvent(lifecycle: Observable<E>, event: E): Completable
    = this.compose(RxLifecycle.bindUntilEvent<Any, E>(lifecycle, event))

fun <E> Completable.bind(lifecycle: Observable<E>, correspondingEvents: Function<E, E>): Completable
    = this.compose(RxLifecycle.bind<Any, E>(lifecycle, correspondingEvents))

// RxLifecycleAndroid extensions

fun <T, E> Observable<T>.bindToLifecycle(provider: LifecycleProvider<E>): Observable<T>
    = this.compose<T>(provider.bindToLifecycle<T>())

fun <T, E> Observable<T>.bindUntilEvent(provider: LifecycleProvider<E>, event: E): Observable<T>
    = this.compose<T>(provider.bindUntilEvent(event))

fun <T> Observable<T>.bindToLifecycle(view: View): Observable<T>
    = this.compose<T>(RxLifecycleAndroid.bindView(view))

fun <T, E> Flowable<T>.bindToLifecycle(provider: LifecycleProvider<E>): Flowable<T>
    = this.compose<T>(provider.bindToLifecycle<T>())

fun <T, E> Flowable<T>.bindUntilEvent(provider: LifecycleProvider<E>, event: E): Flowable<T>
    = this.compose<T>(provider.bindUntilEvent(event))

fun <T> Flowable<T>.bindToLifecycle(view: View): Flowable<T>
    = this.compose<T>(RxLifecycleAndroid.bindView(view))

fun <T, E> Single<T>.bindToLifecycle(provider: LifecycleProvider<E>): Single<T>
    = this.compose(provider.bindToLifecycle<T>())

fun <T, E> Single<T>.bindUntilEvent(provider: LifecycleProvider<E>, event: E): Single<T>
    = this.compose(provider.bindUntilEvent<T>(event))

fun <T> Single<T>.bindToLifecycle(view: View): Single<T>
    = this.compose(RxLifecycleAndroid.bindView<T>(view))

fun <T, E> Maybe<T>.bindToLifecycle(provider: LifecycleProvider<E>): Maybe<T>
    = this.compose(provider.bindToLifecycle<T>())

fun <T, E> Maybe<T>.bindUntilEvent(provider: LifecycleProvider<E>, event: E): Maybe<T>
    = this.compose(provider.bindUntilEvent<T>(event))

fun <T> Maybe<T>.bindToLifecycle(view: View): Maybe<T>
    = this.compose(RxLifecycleAndroid.bindView<T>(view))

fun <E> Completable.bindToLifecycle(provider: LifecycleProvider<E>): Completable
    = this.compose(provider.bindToLifecycle<Completable>())

fun <E> Completable.bindUntilEvent(provider: LifecycleProvider<E>, event: E): Completable
    = this.compose(provider.bindUntilEvent<Completable>(event))

fun Completable.bindToLifecycle(view: View): Completable
    = this.compose(RxLifecycleAndroid.bindView<Completable>(view))
