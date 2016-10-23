package com.xkcn.gallery.data.cloud.gson_deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.xkcn.gallery.data.cloud.DataContracts;
import com.xkcn.gallery.data.cloud.model.PhotoCollection;
import com.xkcn.gallery.presentation.navigator.CollectionNavigator;
import com.xkcn.gallery.presentation.navigator.Navigator;

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
		String navigatorType = jsonObject.getAsJsonPrimitive(DataContracts.NAVIGATOR_TYPE).getAsString();
		navigator = parseCollectionNavigator(navigatorType, jsonObject, context);

		return navigator;
	}

	private Navigator parseCollectionNavigator(String navigatorType, JsonObject jsonObject, JsonDeserializationContext context) {
		if (DataContracts.NAVIGATOR_TYPE_COLLECTION.equalsIgnoreCase(navigatorType)) {
			CollectionNavigator navigator = new CollectionNavigator();

			JsonObject photoColJsonObj = jsonObject.getAsJsonObject(DataContracts.NAVIGATOR_PHOTO_COLLECTION);
			if (!photoColJsonObj.isJsonNull()) {
				PhotoCollection photoCollection = context.deserialize(photoColJsonObj, PhotoCollection.class);
				navigator.setPhotoCollection(photoCollection);
			}

			return navigator;
		}

		return null;
	}
}
