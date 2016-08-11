# RxLifecycle

The utilities provided here allow for automatic completion of sequences based on `Activity` or `Fragment`
lifecycle events. This capability is useful in Android, where incomplete subscriptions can cause memory leaks.

## Usage

You must provide an `Observable<ActivityEvent>` or `Observable<FragmentEvent>` that gives
RxLifecycle the information needed to complete the sequence at the correct time.

You can then end the sequence explicitly when an event occurs:

```java
myObservable
    .compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.DESTROY))
    .subscribe();
```

Alternatively, you can let RxLifecycle determine the appropriate time to end the sequence:

```java
myObservable
    .compose(RxLifecycle.bindActivity(lifecycle))
    .subscribe();
```

It assumes you want to end the sequence in the opposing lifecycle event - e.g., if subscribing during `START`, it will
terminate on `STOP`. If you subscribe after `PAUSE`, it will terminate at the next destruction event (e.g.,
`PAUSE` will terminate in `STOP`).

## Single and Completable

RxLifecycle supports both `Single` and `Completable` via the `LifecycleTransformer`. You can
convert any returned `LifecycleTransformer` into a `Single.Transformer` or `CompletableTransformer`
via the `forSingle()` and `forCompletable()` methods:

```java
mySingle
    .compose(RxLifecycle.bindActivity(lifecycle).forSingle())
    .subscribe();
```

## Providers

Where do the sequences of `ActivityEvent` or `FragmentEvent` come from? Generally, they are provided by
an appropriate `LifecycleProvider<T>`. But where are those implemented?

You have a few options for that:

1. Use rxlifecycle-components and subclass the provided `RxActivity`, `RxFragment`, etc. classes.
1. Use [Navi](https://github.com/trello/navi/) + rxlifecycle-navi to generate providers.
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
    private final ActivityLifecycleProvider provider
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

If you want some Kotlin goodness, you can use built-in extensions:

```java
myObservable
    .bindToLifecycle(myView)
    .subscribe { }

myObservable
    .bindUntilEvent(myRxActivity, STOP)
    .subscribe { }
```

## Unsubscription

RxLifecycle does not actually unsubscribe the sequence. Instead it terminates the sequence. The way in which
it does so varies based on the type:

- `Observable` - emits `onCompleted()`
- `Single` and `Completable` - emits `onError(CancellationException)`

If a sequence requires the `Subscription.unsubscribe()` behavior, then it is suggested that you manually handle
the `Subscription` yourself and call `unsubscribe()` when appropriate.

## Installation

```gradle
compile 'com.trello:rxlifecycle:0.6.1'

// If you want pre-written Activities and Fragments you can subclass as providers
compile 'com.trello:rxlifecycle-components:0.6.1'

// If you want to use Navi for providers
compile 'com.trello:rxlifecycle-navi:0.6.1'

// If you want to use Kotlin syntax
compile 'com.trello:rxlifecycle-kotlin:0.6.1'
```

## Related Libraries

- [Android-Lint-Checks](https://github.com/vokal/Android-Lint-Checks) - Contains an RxLifecycle Lint check.

## License

    Copyright (C) 2015 Trello

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.