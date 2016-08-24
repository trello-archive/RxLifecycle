package com.trello.rxlifecycle.sample;

import android.os.Bundle;
import android.util.Log;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

public class MainActivity extends RxAppCompatActivity {

    private static final String TAG = "RxLifecycleAndroid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        // Specifically bind this until onPause()
        Observable.interval(1, TimeUnit.SECONDS)
            .doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    Log.i(TAG, "Unsubscribing subscription from onCreate()");
                }
            })
            .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long num) {
                    Log.i(TAG, "Started in onCreate(), running until onPause(): " + num);
                }
            });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart()");

        // Using automatic unsubscription, this should determine that the correct time to
        // unsubscribe is onStop (the opposite of onStart).
        Observable.interval(1, TimeUnit.SECONDS)
            .doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    Log.i(TAG, "Unsubscribing subscription from onStart()");
                }
            })
            .compose(this.<Long>bindToLifecycle())
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long num) {
                    Log.i(TAG, "Started in onStart(), running until in onStop(): " + num);
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");

        // `this.<Long>` is necessary if you're compiling on JDK7 or below.
        //
        // If you're using JDK8+, then you can safely remove it.
        Observable.interval(1, TimeUnit.SECONDS)
            .doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    Log.i(TAG, "Unsubscribing subscription from onResume()");
                }
            })
            .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long num) {
                    Log.i(TAG, "Started in onResume(), running until in onDestroy(): " + num);
                }
            });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy()");
    }
}
