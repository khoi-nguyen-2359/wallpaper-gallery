package com.khoinguyen.apptemplate.eventbus;

/**
 * Created by khoinguyen on 5/18/16.
 */
public interface IEventBus {
	void register(Object subscriber);

	void unregister(Object subscriber);

	void post(Object event);
}
