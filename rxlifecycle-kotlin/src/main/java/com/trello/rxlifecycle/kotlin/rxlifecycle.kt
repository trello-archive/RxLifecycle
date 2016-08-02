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
import com.trello.rxlifecycle.*
import rx.Completable
import rx.Observable
import rx.Single

fun <T> Observable<T>.bindToLifecycle(activity: ActivityLifecycleProvider): Observable<T>
        = this.compose<T>(activity.bindToLifecycle<T>())

fun <T> Observable<T>.bindUntilEvent(activity: ActivityLifecycleProvider, event: ActivityEvent): Observable<T>
        = this.compose<T>(activity.bindUntilEvent(event))

fun <T> Observable<T>.bindToLifecycle(fragment: FragmentLifecycleProvider): Observable<T>
        = this.compose<T>(fragment.bindToLifecycle<T>())

fun <T> Observable<T>.bindUntilEvent(fragment: FragmentLifecycleProvider, event: FragmentEvent): Observable<T>
        = this.compose<T>(fragment.bindUntilEvent(event))

fun <T> Observable<T>.bindToLifecycle(view: View): Observable<T>
        = this.compose<T>(RxLifecycle.bindView(view))

fun Completable.bindToLifecycle(activity: ActivityLifecycleProvider): Completable
        = this.compose(activity.bindToLifecycle<Completable>().forCompletable())

fun Completable.bindUntilEvent(activity: ActivityLifecycleProvider, event: ActivityEvent): Completable
        = this.compose(activity.bindUntilEvent<Completable>(event).forCompletable())

fun Completable.bindToLifecycle(fragment: FragmentLifecycleProvider): Completable
        = this.compose(fragment.bindToLifecycle<Completable>().forCompletable())

fun Completable.bindUntilEvent(fragment: FragmentLifecycleProvider, event: FragmentEvent): Completable
        = this.compose(fragment.bindUntilEvent<Completable>(event).forCompletable())

fun Completable.bindToLifecycle(view: View): Completable
        = this.compose(RxLifecycle.bindView<Completable>(view).forCompletable())

fun <T> Single<T>.bindToLifecycle(activity: ActivityLifecycleProvider): Single<T>
        = this.compose(activity.bindToLifecycle<T>().forSingle())

fun <T> Single<T>.bindUntilEvent(activity: ActivityLifecycleProvider, event: ActivityEvent): Single<T>
        = this.compose(activity.bindUntilEvent<T>(event).forSingle())

fun <T> Single<T>.bindToLifecycle(fragment: FragmentLifecycleProvider): Single<T>
        = this.compose(fragment.bindToLifecycle<T>().forSingle())

fun <T> Single<T>.bindUntilEvent(fragment: FragmentLifecycleProvider, event: FragmentEvent): Single<T>
        = this.compose(fragment.bindUntilEvent<T>(event).forSingle())

fun <T> Single<T>.bindToLifecycle(view: View): Single<T>
        = this.compose(RxLifecycle.bindView<T>(view).forSingle())