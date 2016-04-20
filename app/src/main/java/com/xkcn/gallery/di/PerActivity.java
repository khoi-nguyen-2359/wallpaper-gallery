package com.xkcn.gallery.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by khoinguyen on 4/20/16.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {}
