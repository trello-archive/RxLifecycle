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

package com.trello.rxlifecycle2.sample

import android.os.Bundle
import android.util.Log
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class KotlinActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate()")

        setContentView(R.layout.activity_main)

        // Specifically bind this until onPause()
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose { Log.i(TAG, "Unsubscribing subscription from onCreate()") }
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe { num -> Log.i(TAG, "Started in onCreate(), running until onPause(): " + num!!) }
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart()")

        // Using automatic unsubscription, this should determine that the correct time to
        // unsubscribe is onStop (the opposite of onStart).
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose { Log.i(TAG, "Unsubscribing subscription from onStart()") }
                .bindToLifecycle(this)
                .subscribe { num -> Log.i(TAG, "Started in onStart(), running until in onStop(): " + num!!) }
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume()")

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose { Log.i(TAG, "Unsubscribing subscription from onResume()") }
                .bindUntilEvent(this, ActivityEvent.DESTROY)
                .subscribe { num -> Log.i(TAG, "Started in onResume(), running until in onDestroy(): " + num!!) }
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy()")
    }

    companion object {
        private val TAG = "RxLifecycle-Kotlin"
    }
}
