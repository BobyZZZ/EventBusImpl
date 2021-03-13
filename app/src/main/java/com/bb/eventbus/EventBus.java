package com.bb.eventbus;

import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class EventBus {
    String TAG = "EventBus";
    private static EventBus mInstance;
    private final SubscriberMethodFinder mSubscriberMethodFinder;

    public static EventBus getInstance() {
        if (mInstance == null) {
            synchronized (EventBus.class) {
                mInstance = new EventBus();
            }
        }
        return mInstance;
    }

    private EventBus() {
        mSubscriberMethodFinder = new SubscriberMethodFinder();
    }

    public void register(Object subscriber) {
        mSubscriberMethodFinder.register(subscriber);
    }

    public void unRegister(Object subscriber) {
        mSubscriberMethodFinder.unRegister(subscriber);
    }

    /**
     * 发送事件
     * @param event
     */
    public void post(Object event) {
        Class<?> eventType = event.getClass();
        List<Subscription> subscriptions = mSubscriberMethodFinder.mSubscriptionsByEventType.get(eventType);
        if (subscriptions != null) {
            for (Subscription subscription : subscriptions) {
                try {
                    subscription.subscriberMethod.method.invoke(subscription.subscriber,event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Subscribe {
    }
}

