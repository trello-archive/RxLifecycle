package com.trello.rxlifecycle.kotlin

import android.view.View
import com.trello.rxlifecycle.ActivityLifecycleProvider
import com.trello.rxlifecycle.FragmentLifecycleProvider
import com.trello.rxlifecycle.RxLifecycle
import rx.Observable

fun <T> Observable<T>.bind(activity: ActivityLifecycleProvider): Observable<T> = this.compose<T>(activity.bindToLifecycle<T>())

fun <T> Observable<T>.bind(fragment: FragmentLifecycleProvider): Observable<T> = this.compose<T>(fragment.bindToLifecycle<T>())

fun <T> Observable<T>.bind(view: View): Observable<T> = this.compose<T>(RxLifecycle.bindView(view))