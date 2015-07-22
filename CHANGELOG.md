# Changelog

## 0.1.0 (*in development*)

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