package com.xkcn.gallery.model;

import com.xkcn.gallery.view.navigator.ItemNavigator;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

/**
 * Created by khoinguyen on 9/9/16.
 */
public abstract class NavigationItem implements Serializable {
	private int id;
	private String type;
	private String title;
	private String data;
	private boolean isDefault;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public <T extends ItemNavigator> T navigator() {
		try {
			Method m = getClass().getDeclaredMethod("navigator");
			TypeVariable<Method>[] typeParams = m.getTypeParameters();
			Class clazz = typeParams[0].getClass();
			Constructor ctor = clazz.getConstructor();
			return (T) ctor.newInstance();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean aDefault) {
		isDefault = aDefault;
	}
}
