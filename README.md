# RxLifecycle

The utilities provided here allow for automatic completion of sequences based on `Activity` or `Fragment`
lifecycle events. This capability is useful in Android, where incomplete subscriptions can cause memory leaks.

## Usage

You must provide an `Observable<ActivityEvent>` or `Observable<FragmentEvent>` that gives
RxLifecycle the information needed to complete the sequence at the correct time.

You can then end the sequence explicitly when an event occurs:

```java
myObservable
    .compose(RxLifecycle.bindUntilActivityEvent(lifecycle, ActivityEvent.DESTROY))
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

## Components

Where do the sequences of `ActivityEvent` or `FragmentEvent` come from? You can either write it yourself, or you can
include rxlifecycle-components, which comes with pre-built `Activity` and `Fragment` implementations with lifecycles.

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

In addition, these components come with `lifecycle()`, which allows you to do your own logic based on the `Activity`
or `Fragment` lifecycle.

## Unsubscription

RxLifecycle does not actually unsubscribe the sequence. It terminates it by emitting `onCompleted()`, which ends the
sequence. This differs from `Subscription.unsubscribe()`, which causes the sequence to simply end.

In most cases this works out fine (since `onCompleted()` is not often used). In cases where you do not want
`onCompleted()` called during early termination, then it is suggested that you manually handle the `Subscription`
yourself and call `unsubscribe()` when appropriate.

(To understand why RxLifecycle has this behavior, [read this](https://github.com/trello/RxLifecycle/pull/14))

## Installation

```gradle
compile 'com.trello:rxlifecycle:0.3.1'
compile 'com.trello:rxlifecycle-components:0.3.1'
```

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