package com.trello.rxlifecycle;

import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * This emits an event when the given #{@code View} is detached from the window for the first time.
 */
final class OnSubscribeViewDetachedFromWindowFirst implements Observable.OnSubscribe<ViewEvent> {
    private final View view;

    public OnSubscribeViewDetachedFromWindowFirst(View view) {
        this.view = view;
    }

    @Override
    public void call(final Subscriber<? super ViewEvent> subscriber) {
        final SubscriptionAdapter adapter = new SubscriptionAdapter(subscriber, view);
        subscriber.add(adapter);
        view.addOnAttachStateChangeListener(adapter);
    }

    private static class SubscriptionAdapter implements View.OnAttachStateChangeListener,
            Subscription {
        private Subscriber<? super ViewEvent> subscriber;
        private View view;

        public SubscriptionAdapter(Subscriber<? super ViewEvent> subscriber, View view) {
            this.subscriber = subscriber;
            this.view = view;
        }

        @Override
        public void onViewAttachedToWindow(View v) {
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if (!isUnsubscribed()) {
                Subscriber<? super ViewEvent> originalSubscriber = subscriber;
                unsubscribe();
                originalSubscriber.onNext(ViewEvent.DETACH);
                originalSubscriber.onCompleted();
            }
        }

        @Override
        public void unsubscribe() {
            if (!isUnsubscribed()) {
                view.removeOnAttachStateChangeListener(this);
                view = null;
                subscriber = null;
            }
        }

        @Override
        public boolean isUnsubscribed() {
            return view == null;
        }
    }
}
