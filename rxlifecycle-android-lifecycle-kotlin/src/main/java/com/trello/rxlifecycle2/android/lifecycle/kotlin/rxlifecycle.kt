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

package com.trello.rxlifecycle2.android.lifecycle.kotlin

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import io.reactivex.*

fun <T> Observable<T>.bindToLifecycle(owner: LifecycleOwner): Observable<T>
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindToLifecycle())

fun <T> Observable<T>.bindUntilEvent(owner: LifecycleOwner, event: Lifecycle.Event): Observable<T>
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindUntilEvent(event))

fun <T> Flowable<T>.bindToLifecycle(owner: LifecycleOwner): Flowable<T>
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindToLifecycle())

fun <T> Flowable<T>.bindUntilEvent(owner: LifecycleOwner, event: Lifecycle.Event): Flowable<T>
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindUntilEvent(event))

fun <T> Single<T>.bindToLifecycle(owner: LifecycleOwner): Single<T>
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindToLifecycle())

fun <T> Single<T>.bindUntilEvent(owner: LifecycleOwner, event: Lifecycle.Event): Single<T>
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindUntilEvent(event))

fun <T> Maybe<T>.bindToLifecycle(owner: LifecycleOwner): Maybe<T>
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindToLifecycle())

fun <T> Maybe<T>.bindUntilEvent(owner: LifecycleOwner, event: Lifecycle.Event): Maybe<T>
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindUntilEvent(event))

fun <T> Completable.bindToLifecycle(owner: LifecycleOwner): Completable
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindToLifecycle<Completable>())

fun <T> Completable.bindUntilEvent(owner: LifecycleOwner, event: Lifecycle.Event): Completable
        = this.compose(AndroidLifecycle.createLifecycleProvider(owner).bindUntilEvent<Completable>(event))
