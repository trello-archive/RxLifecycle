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
import rx.Observable

fun <T> Observable<T>.bindToLifecycle(activity: ActivityLifecycleProvider): Observable<T> = this.compose<T>(activity.bindToLifecycle<T>())

fun <T> Observable<T>.bindUntilEvent(activity: ActivityLifecycleProvider, event: ActivityEvent): Observable<T> = this.compose<T>(activity.bindUntilEvent(event))

fun <T> Observable<T>.bindToLifecycle(fragment: FragmentLifecycleProvider): Observable<T> = this.compose<T>(fragment.bindToLifecycle<T>())

fun <T> Observable<T>.bindUntilEvent(fragment: FragmentLifecycleProvider, event: FragmentEvent): Observable<T> = this.compose<T>(fragment.bindUntilEvent(event))

fun <T> Observable<T>.bindToLifecycle(view: View): Observable<T> = this.compose<T>(RxLifecycle.bindView(view))