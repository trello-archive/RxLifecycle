package com.trello.rxlifecycle.sample

import android.os.Bundle
import android.util.Log
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import rx.Observable
import java.util.concurrent.TimeUnit

class KotlinActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate()")

        setContentView(R.layout.activity_main)

        // Specifically bind this until onPause()
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe { Log.i(TAG, "Unsubscribing subscription from onCreate()") }
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe { num -> Log.i(TAG, "Started in onCreate(), running until onPause(): " + num!!) }
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart()")

        // Using automatic unsubscription, this should determine that the correct time to
        // unsubscribe is onStop (the opposite of onStart).
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe { Log.i(TAG, "Unsubscribing subscription from onStart()") }
                .bindToLifecycle(this)
                .subscribe { num -> Log.i(TAG, "Started in onStart(), running until in onStop(): " + num!!) }
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume()")

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe { Log.i(TAG, "Unsubscribing subscription from onResume()") }
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
        private val TAG = "RxLifecycleAndroid-Kotlin"
    }
}
