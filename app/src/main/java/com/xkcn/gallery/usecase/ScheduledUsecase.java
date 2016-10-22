package com.xkcn.gallery.usecase;

import java.util.concurrent.Executor;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by khoinguyen on 9/29/16.
 */
public abstract class ScheduledUsecase {
	private Executor workerExecutor;
	private Executor callbackExecutor;

	public ScheduledUsecase(Executor workerExecutor, Executor callbackExecutor) {
		this.workerExecutor = workerExecutor;
		this.callbackExecutor = callbackExecutor;
	}

	protected <T> Observable.Transformer<T,T> createSchedulerTransformer() {
		return o -> o
			.subscribeOn(Schedulers.from(workerExecutor))
			.observeOn(Schedulers.from(callbackExecutor));
	}
}
