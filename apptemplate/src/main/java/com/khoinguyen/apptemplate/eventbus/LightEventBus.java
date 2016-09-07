package com.khoinguyen.apptemplate.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by khoinguyen on 5/2/16.
 * <p/>
 * Light version of EventBus, doesnt have advanced features: Threading, Sticky, Event's super class
 */
public class LightEventBus implements IEventBus {
	private static Map<Class<?>, List<SubscriberMethod>> methodCache = new HashMap<>();
	private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> mapSubscriptionByEventType = new HashMap<>();
	private final Map<Object, List<Class<?>>> typesBySubscriber = new HashMap<>();

	@Override
	public void register(Object subscriber) {
		if (subscriber == null) {
			return;
		}

		List<SubscriberMethod> subscriberMethods = findEventMethods(subscriber.getClass());
		for (SubscriberMethod method : subscriberMethods) {
			Subscription subscription = new Subscription(subscriber, method);
			CopyOnWriteArrayList<Subscription> subscriptionsByType = mapSubscriptionByEventType.get(method.eventType);
			if (subscriptionsByType == null) {
				subscriptionsByType = new CopyOnWriteArrayList<>();
				mapSubscriptionByEventType.put(method.eventType, subscriptionsByType);
			}

			subscriptionsByType.add(subscription);

			List<Class<?>> subscribedEvents = typesBySubscriber.get(subscriber);
			if (subscribedEvents == null) {
				subscribedEvents = new ArrayList<>();
				typesBySubscriber.put(subscriber, subscribedEvents);
			}
			subscribedEvents.add(method.eventType);
		}
	}

	@Override
	public void unregister(Object subscriber) {
		List<Class<?>> subscribedTypes = typesBySubscriber.get(subscriber);
		if (subscribedTypes != null) {
			for (Class<?> type : subscribedTypes) {
				CopyOnWriteArrayList<Subscription> subscriptionsByType = mapSubscriptionByEventType.get(type);
				int subscriptionSize = subscriptionsByType.size();
				for (int i = 0; i < subscriptionSize; ++i) {
					Subscription subscription = subscriptionsByType.get(i);
					if (subscription.subscriber == subscriber) {
						subscriptionsByType.remove(subscription);
						--i;
						--subscriptionSize;
					}
				}
			}
		}

		typesBySubscriber.remove(subscriber);
	}

	private List<SubscriberMethod> findEventMethods(Class<?> subscriberClass) {
		List<SubscriberMethod> subscriberMethods = methodCache.get(subscriberClass);
		if (subscriberMethods != null) {
			return subscriberMethods;
		}

		subscriberMethods = new ArrayList<>();
		methodCache.put(subscriberClass, subscriberMethods);

		Method[] methods;
		try {
			methods = subscriberClass.getDeclaredMethods();
		} catch (Throwable th) {
			methods = subscriberClass.getMethods();
		}

		for (Method method : methods) {
			int modifiers = method.getModifiers();
			if ((modifiers & Modifier.PUBLIC) == 0) {
				continue;
			}

			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != 1) {
				continue;
			}

			Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
			if (subscribeAnnotation == null) {
				continue;
			}

			Class<?> eventType = parameterTypes[0];
			subscriberMethods.add(new SubscriberMethod(method, eventType));
		}

		return subscriberMethods;
	}

	@Override
	public void post(Object event) {
		Class<?> eventType = event.getClass();
		CopyOnWriteArrayList<Subscription> subscriptionsByType = mapSubscriptionByEventType.get(eventType);
		for (Subscription subscription : subscriptionsByType) {
			subscription.invoke(event);
		}
	}

	public static class SubscriberMethod {
		private Method method;
		private Class<?> eventType;

		public SubscriberMethod(Method method, Class<?> eventType) {
			this.method = method;
			this.eventType = eventType;
		}
	}

	public static class Subscription {
		private Object subscriber;
		private SubscriberMethod subscriberMethod;
		private boolean active;

		public Subscription(Object subscriber, SubscriberMethod subscriberMethod) {
			this.subscriber = subscriber;
			this.subscriberMethod = subscriberMethod;
			this.active = true;
		}

		public void invoke(Object event) {
			if (!active) {
				return;
			}

			try {
				subscriberMethod.method.invoke(subscriber, event);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
