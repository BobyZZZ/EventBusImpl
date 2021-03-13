package com.bb.eventbus;

import java.lang.reflect.Method;

public class SubscriberMethod {
    Method method;//订阅方法
    Class eventType;//订阅方法的参数类型

    public SubscriberMethod(Method method, Class eventType) {
        this.method = method;
        this.eventType = eventType;
    }
}
