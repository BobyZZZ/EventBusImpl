package com.bb.eventbus;

/**
 * 将订阅对象和方法包裹类绑定起来
 */
public class Subscription {
    Object subscriber;//订阅的实例对象
    SubscriberMethod subscriberMethod;//订阅方法包裹类

    public Subscription(Object subscriber, SubscriberMethod subscriberMethod) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }
}
