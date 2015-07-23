# RxLifecycle

The utilities provided here allow for automatic unsubscription from sequences based on `Activity` or `Fragment`
lifecycle events. This capability is useful in Android, where incomplete subscriptions can cause memory leaks.

## Usage

You must provide an `Observable<ActivityEvent>` or `Observable<FragmentEvent>` that gives
RxLifecycle the information needed to unsubscribe at the correct time.

You can then unsubscribe explicitly when an event occurs:

```java
myObservable
    .compose(RxLifecycle.bindUntilActivityEvent(lifecycle, ActivityEvent.DESTROY))
    .subscribe();
```

Alternatively, you can let RxLifecycle determine the appropriate time to unsubscribe:

```java
myObservable
    .compose(RxLifecycle.bindActivity(lifecycle))
    .subscribe();
```

It assumes you want to unsubscribe in the opposing lifecycle event - e.g., if subscribing during `START`, it will
unsubscribe on `STOP`. If you subscribe after `PAUSE`, it will unsubscribe at the next destruction event (e.g.,
`PAUSE` will unsubscribe in `STOP`).

Warning: Due to the way the unsubscription works, it is only 100% safe if you call RxLifecycle immediately before
calling `subscribe()`. Otherwise, some operators may ignore the unsubscription requests.

## Components

Where do the sequences of `ActivityEvent` or `FragmentEvent` come from? You can either write it yourself, or you can
include rxlifecycle-components, which comes with pre-built `Activity` and `Fragment` implementations with lifecycles.

If you use rxlifecycle-components, just extend the appropriate class, then use the built-in `bindToLifecycle()` (or `bindUntilEvent()`) methods:

```java
public class MyActivity extends RxActivity {
    @Override
    public void onResume() {
        myObservable
            .compose(bindToLifecycle())
            .subscribe();
    }
}
```

In addition, these components come with `lifecycle()`, which allows you to do your own logic based on the `Activity`
or `Fragment` lifecycle.

## Installation

```gradle
compile 'com.trello:rxlifecycle:0.1.0'
compile 'com.trello:rxlifecycle-components:0.1.0'
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