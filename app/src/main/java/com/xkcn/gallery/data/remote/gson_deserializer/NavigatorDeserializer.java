package com.xkcn.gallery.data.remote.gson_deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.xkcn.gallery.data.remote.DataContracts;
import com.xkcn.gallery.view.navigator.CollectionNavigator;
import com.xkcn.gallery.view.navigator.Navigator;

import java.lang.reflect.Type;

/**
 * Created by khoinguyen on 9/25/16.
 */

public class NavigatorDeserializer implements JsonDeserializer<Navigator> {
	@Override
	public Navigator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Navigator navigator = null;

		if (json == null || json.isJsonNull()) {
			return null;
		}

		JsonObject jsonObject = json.getAsJsonObject();
		String navigatorType = jsonObject.getAsJsonPrimitive("type").getAsString();
		navigator = parseCollectionNavigator(navigatorType);

		return navigator;
	}

	private Navigator parseCollectionNavigator(String navigatorType) {
		if (DataContracts.NAVIGATOR_TYPE_COLLECTION.equalsIgnoreCase(navigatorType)) {
			return new CollectionNavigator();
		}

		return null;
	}
}
