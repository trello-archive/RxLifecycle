# Changelog

## 0.7.0 (2016-08-24)

There are a couple major breaking changes in this release, so read carefully.

- [#143](https://github.com/trello/RxLifecycle/pull/143): Split Android parts of rxlifecycle into rxlifecycle-android

  Now the `rxlifecycle` dependency is a pure Java core. To use RxLifecycle with Android, you should also include
  `rxlifecycle-android` as well now.

  `RxLifecycle` methods that are for Android (`bindActivity()`, `bindFragment()` and `bindView()`) are now in
  `RxLifecycleAndroid` (found in the `rxlifecycle-android` artifact).

   Deprecated methods in `RxLifecycle` were removed as well.

- [#138](https://github.com/trello/RxLifecycle/pull/138) / [#139](https://github.com/trello/RxLifecycle/pull/139):
Replace `ActivityLifecycleProvider` and `FragmentLifecycleProvider` with `LifecycleProvider<T>`.

  This means that you need to replace `ActivityLifecycleProvider` with `LifecycleProvider<ActivityEvent>` and
  `FragmentLifecycleProvider` with `LifecycleProvider<FragmentEvent>`.

Other changes:

- [#140](https://github.com/trello/RxLifecycle/pull/140): Fixed type inference for LifecycleTransformer.forSingle()
- [#135](https://github.com/trello/RxLifecycle/pull/135): Add Kotlin extensions for Singles and Completables

## 0.6.1 (2016-05-11)

- [#118](https://github.com/trello/RxLifecycle/pull/118): Use `LifecycleTransformer` in the provider interfaces

## 0.6.0 (2016-05-06)

- Added `LifecycleTransformer`, which provides `Single` and `Completable` support.
- [#111](https://github.com/trello/RxLifecycle/pull/111): Added component support for `AppCompatDialogFragment`
- [#107](https://github.com/trello/RxLifecycle/pull/107): Fixed navi component package

## 0.5.0 (2016-02-27)

There are a few methods which have been **deprecated** and will be removed in a future release. Each one has a drop-in
replacement (and currently calling the deprecated version just routes to the new one):

* `bindUntilActivityEvent()` and `bindUntilFragmentEvent()` -> `bindUntilEvent()`
* `bindView()` -> `bind()`

The other changes mostly involve opening up the API for more customization:

* [#78](https://github.com/trello/RxLifecycle/pull/78): Added rxlifecycle-kotlin (adds some extensions)
* [#79](https://github.com/trello/RxLifecycle/pull/79): Made bindUntilEvent() public, deprecated more specific calls
* [#82](https://github.com/trello/RxLifecycle/pull/82): Opened up bind() methods for public consumption
* [#83](https://github.com/trello/RxLifecycle/pull/83), [#85](https://github.com/trello/RxLifecycle/pull/85): Added
annotations for better lint checking
* [#88](https://github.com/trello/RxLifecycle/pull/88): Use `.equals()` instead of == for event comparison (for
custom event support with the newly opened-up `bind()`)
* [#89](https://github.com/trello/RxLifecycle/pull/89): Make OutsideLifecycleException public (for anyone who wants
to write their own `bind()` implementations)

## 0.4.0 (2015-12-10)

* [#62](https://github.com/trello/RxLifecycle/pull/62), [#67](https://github.com/trello/RxLifecycle/pull/67): Added
[Navi](https://github.com/trello/navi/)-based implementation of providers.
* [#61](https://github.com/trello/RxLifecycle/pull/61): Moved `ActivityLifecycleProvider` and
`FragmentLifecycleProvider` into the core library so that multiple implementations can share them easily. The package
 name changed so you may need to re-import them in your code.
* [#60](https://github.com/trello/RxLifecycle/pull/60): Reverted generic parameters back to `Transformer<T, T>`

## 0.3.1 (2015-11-27)

* [#46](https://github.com/trello/RxLifecycle/pull/46): Updated generic parameters of `Transformer<T, T>` to support Kotlin type inference.

## 0.3.0

* [#12](https://github.com/trello/RxLifecycle/pull/12): Added `RxLifecycle.bindView()`. This allows binding a
`Subscription` until a `View` detaches itself from the window.
* [#30](https://github.com/trello/RxLifecycle/pull/30): Calling `bindActivity()` or `bindFragment()` outside of the
lifecycle causes the sequence to immediately complete (instead of throwing an error).
* [#31](https://github.com/trello/RxLifecycle/pull/31): Passing nulls to RxLifecycle now always immediately throws an
exception.

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