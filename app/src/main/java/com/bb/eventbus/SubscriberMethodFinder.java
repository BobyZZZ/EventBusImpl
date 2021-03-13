package com.bb.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriberMethodFinder {
    public ConcurrentHashMap<Class, List<Subscription>> mSubscriptionsByEventType;//订阅方法的参数类型为key，参数类型为key的方法集合为value
    public ConcurrentHashMap<Object, List<Class>> mSubscriberEventTypeMap;//订阅对象为key，订阅方法的参数类型集合为value

    public SubscriberMethodFinder() {
        mSubscriptionsByEventType = new ConcurrentHashMap<>();
        mSubscriberEventTypeMap = new ConcurrentHashMap<>();
    }

    /**
     * 订阅
     * @param subscriber
     */
    public void register(Object subscriber) {
        //遍历获取所有方法
        List<SubscriberMethod> subscriberMethods = findSubscriberMethods(subscriber.getClass());

        //SubscriberMethod和实例对象绑定==>Subscription
        for (SubscriberMethod subscriberMethod : subscriberMethods) {
            subscribe(subscriber, subscriberMethod);
        }
    }

    /**
     * 取消订阅
     * @param subscriber
     */
    public void unRegister(Object subscriber) {
        List<Class> eventTypes = mSubscriberEventTypeMap.get(subscriber);
        if (eventTypes != null && !eventTypes.isEmpty()) {
            //遍历获取所有类型，根据类型获取方法
            for (Class eventType : eventTypes) {
                List<Subscription> subscriptions = mSubscriptionsByEventType.get(eventType);
                if (subscriptions != null) {
                    Iterator<Subscription> iterator = subscriptions.iterator();
                    while (iterator.hasNext()) {
                        Subscription subscription = iterator.next();
                        if (subscription.subscriber == subscriber) {
                            iterator.remove();
                        }
                    }

                    if (subscriptions.isEmpty()) {
                        mSubscriptionsByEventType.remove(eventType);
                    }
                }
            }
        }
        mSubscriberEventTypeMap.remove(subscriber);
    }

    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        //记录订阅对象的所有订阅方法
        List<Subscription> subscriptionsByEventType = mSubscriptionsByEventType.get(subscriberMethod.eventType);
        if (subscriptionsByEventType == null) {
            subscriptionsByEventType = new ArrayList<>();
            mSubscriptionsByEventType.put(subscriberMethod.eventType, subscriptionsByEventType);
        }
        Subscription subscription = new Subscription(subscriber,subscriberMethod);
        subscriptionsByEventType.add(subscription);//添加到集合,待post事件的时候取出来调用

        //记录订阅对象的事件类型
        List<Class> eventTypes = mSubscriberEventTypeMap.get(subscriber);
        if (eventTypes == null) {
            eventTypes = new ArrayList<>();
            mSubscriberEventTypeMap.put(subscriber,eventTypes);
        }
        eventTypes.add(subscriberMethod.eventType);//添加到集合，unsubscribe的时候使用
    }

    /**
     * @param subscriberClass   实例类Class对象
     * @return  加了Subscriber注解的方法集合
     */
    private List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        //获取所有方法
        ArrayList<SubscriberMethod> annotationMethods = new ArrayList<>();
        Method[] allDeclaredMethods = subscriberClass.getDeclaredMethods();
        for (Method method : allDeclaredMethods) {
            EventBus.Subscribe annotation = method.getAnnotation(EventBus.Subscribe.class);//获取加了Subscriber注解的方法
            if (annotation != null) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    SubscriberMethod subscriberMethod = new SubscriberMethod(method, parameterTypes[0]);
                    annotationMethods.add(subscriberMethod);
                }
            }
        }
        return annotationMethods;
    }
}
