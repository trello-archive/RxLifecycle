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

package com.trello.rxlifecycle.kotlin

import android.view.View
import com.trello.rxlifecycle.LifecycleProvider
import com.trello.rxlifecycle.android.RxLifecycleAndroid
import rx.Completable
import rx.Observable
import rx.Single

fun <T, E> Observable<T>.bindToLifecycle(provider: LifecycleProvider<E>): Observable<T>
        = this.compose<T>(provider.bindToLifecycle<T>())

fun <T, E> Observable<T>.bindUntilEvent(provider: LifecycleProvider<E>, event: E): Observable<T>
        = this.compose<T>(provider.bindUntilEvent(event))

fun <T> Observable<T>.bindToLifecycle(view: View): Observable<T>
        = this.compose<T>(RxLifecycleAndroid.bindView(view))

fun <E> Completable.bindToLifecycle(provider: LifecycleProvider<E>): Completable
        = this.compose(provider.bindToLifecycle<Completable>().forCompletable())

fun <E> Completable.bindUntilEvent(provider: LifecycleProvider<E>, event: E): Completable
        = this.compose(provider.bindUntilEvent<Completable>(event).forCompletable())

fun Completable.bindToLifecycle(view: View): Completable
        = this.compose(RxLifecycleAndroid.bindView<Completable>(view).forCompletable())

fun <T, E> Single<T>.bindToLifecycle(provider: LifecycleProvider<E>): Single<T>
        = this.compose(provider.bindToLifecycle<T>().forSingle<T>())

fun <T, E> Single<T>.bindUntilEvent(provider: LifecycleProvider<E>, event: E): Single<T>
        = this.compose(provider.bindUntilEvent<T>(event).forSingle<T>())

fun <T> Single<T>.bindToLifecycle(view: View): Single<T>
        = this.compose(RxLifecycleAndroid.bindView<T>(view).forSingle<T>())