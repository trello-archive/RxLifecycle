# RxLifecycle

This library allows one to automatically complete sequences based on a second lifecycle stream.

This capability is useful in Android, where incomplete subscriptions can cause memory leaks.

## Usage

You must start with an `Observable<T>` representing a lifecycle stream. Then you use `RxLifecycle` to bind
a sequence to that lifecycle.

You can bind when the lifecycle emits anything:

```java
myObservable
    .compose(RxLifecycle.bind(lifecycle))
    .subscribe();
```

Or you can bind to when a specific lifecyle event occurs:

```java
myObservable
    .compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.DESTROY))
    .subscribe();
```

Alternatively, you can let RxLifecycle determine the appropriate time to end the sequence:

```java
myObservable
    .compose(RxLifecycleAndroid.bindActivity(lifecycle))
    .subscribe();
```

It assumes you want to end the sequence in the opposing lifecycle event - e.g., if subscribing during `START`, it will
terminate on `STOP`. If you subscribe after `PAUSE`, it will terminate at the next destruction event (e.g.,
`PAUSE` will terminate in `STOP`).

## Providers

Where do lifecycles come from? Generally, they are provided by an appropriate `LifecycleProvider<T>`. But where are
those implemented?

You have a few options for that:

1. Use rxlifecycle-components and subclass the provided `RxActivity`, `RxFragment`, etc. classes.
1. Use [Navi](https://github.com/trello/navi/) + rxlifecycle-navi to generate providers.
1. Use [Android's lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle.html) + rxlifecycle-android-lifecycle to generate providers.
1. Write the implementation yourself.

If you use rxlifecycle-components, just extend the appropriate class, then use the built-in `bindToLifecycle()` (or `bindUntilEvent()`) methods:

```java
public class MyActivity extends RxActivity {
    @Override
    public void onResume() {
        super.onResume();
        myObservable
            .compose(bindToLifecycle())
            .subscribe();
    }
}
```

If you use rxlifecycle-navi, then you just pass your `NaviComponent` to `NaviLifecycle` to generate a provider:

```java
public class MyActivity extends NaviActivity {
    private final LifecycleProvider<ActivityEvent> provider
        = NaviLifecycle.createActivityLifecycleProvider(this);

    @Override
    public void onResume() {
        super.onResume();
        myObservable
            .compose(provider.bindToLifecycle())
            .subscribe();
    }
}
```

If you use rxlifecycle-android-lifecycle, then you just pass your `LifecycleOwner` to `AndroidLifecycle` to generate a provider:

```java
public class MyActivity extends LifecycleActivity {
    private final LifecycleProvider<Lifecycle.Event> provider
        = AndroidLifecycle.createLifecycleProvider(this);

    @Override
    public void onResume() {
        super.onResume();
        myObservable
            .compose(provider.bindToLifecycle())
            .subscribe();
    }
}
```

## Unsubscription

RxLifecycle does not actually unsubscribe the sequence. Instead it terminates the sequence. The way in which
it does so varies based on the type:

- `Observable`, `Flowable` and `Maybe` - emits `onCompleted()`
- `Single` and `Completable` - emits `onError(CancellationException)`

If a sequence requires the `Subscription.unsubscribe()` behavior, then it is suggested that you manually handle
the `Subscription` yourself and call `unsubscribe()` when appropriate.

## Kotlin

The rxlifecycle-kotlin module provides built-in extensions to the base RxJava types:

```kotlin
myObservable
    .bindToLifecycle(myView)
    .subscribe { }

myObservable
    .bindUntilEvent(myRxActivity, STOP)
    .subscribe { }
```

There is an additional rxlifecycle-android-lifecycle-kotlin module to provider extensions to work
with `LivecycleOwner`'s.

```kotlin

myObservable
    .bindUntilEvent(myLifecycleActivity, ON_STOP)
    .subscribe { }
```

## Installation

```gradle
implementation 'com.trello.rxlifecycle2:rxlifecycle:2.2.2'

// If you want to bind to Android-specific lifecycles
implementation 'com.trello.rxlifecycle2:rxlifecycle-android:2.2.2'

// If you want pre-written Activities and Fragments you can subclass as providers
implementation 'com.trello.rxlifecycle2:rxlifecycle-components:2.2.2'

// If you want pre-written support preference Fragments you can subclass as providers
implementation 'com.trello.rxlifecycle2:rxlifecycle-components-preference:2.2.2'

// If you want to use Navi for providers
implementation 'com.trello.rxlifecycle2:rxlifecycle-navi:2.2.2'

// If you want to use Android Lifecycle for providers
implementation 'com.trello.rxlifecycle2:rxlifecycle-android-lifecycle:2.2.2'

// If you want to use Kotlin syntax
implementation 'com.trello.rxlifecycle2:rxlifecycle-kotlin:2.2.2'

// If you want to use Kotlin syntax with Android Lifecycle
implementation 'com.trello.rxlifecycle2:rxlifecycle-android-lifecycle-kotlin:2.2.2'
```

## License

    Copyright (C) 2016 Trello

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
