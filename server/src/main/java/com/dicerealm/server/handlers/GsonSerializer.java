package com.dicerealm.server.handlers;

import com.dicerealm.core.JsonSerializationStrategy;
import com.google.gson.Gson;

public class GsonSerializer implements JsonSerializationStrategy {

	private Gson gson = new Gson();

	@Override
	public String serialize(Object object) {
		return gson.toJson(object);
	}

	@Override
	public <T> T deserialize(String json, Class<T> object) {
		return gson.fromJson(json, object);
	}

}
