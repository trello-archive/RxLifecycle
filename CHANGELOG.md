# Changelog

## 0.2.0

* [#14](https://github.com/trello/RxLifecycle/pull/14): Use takeUntil internally (instead of a faulty custom operator)

    While this fixes some intractable problems that could occur with the old system, it also includes a major behavior
    change: when the bind decides to stop subscribing, it calls `onCompleted` (whereas before it would just
    unsubscribe).

    When upgrading, you should check that your usages of `onCompleted` (either in `subscribe()`, `doOnCompleted()`,
    or `doOnTerminate()`) can handle the sequence ending due to the lifecycle bind.

    If you still need the old behavior in some spots, you should handle the `Subscription` yourself manually (and call
    `unsubscribe()` when appropriate).

* [#16](https://github.com/trello/RxLifecycle/pull/16): Lowered minSdkVersion to 14

## 0.1.0

Initial independent release (split from RxAndroid 0.25.0)

* Added support for AppCompatActivity via `RxAppCompatActivity`
* Components (e.g. `RxActivity`) now support `bindUntilEvent()` and `bindToLifecycle()`, which avoids having to deal with the lifecycle `Observable` directly.

### Differences from RxAndroid 0.25

* Renamed `LifecycleObservable` to `RxLifecycle`
* Switched from wrapping `Observables` to using `Transformer` + `compose()` (which allows for chaining)
* Split `LifecycleEvent` into two parts - `ActivityEvent` and `FragmentEvent`, in order to prevent easy mixups that could occur before
* Split `bindUntilLifecycleEvent()` into `bindUntilFragmentEvent()` and `bindUntilActivityEvent()`
* Renamed `bindFragmentLifecycle()` to `bindFragment()`
* Renamed `bindActivityLifecycle()` to `bindActivity()`