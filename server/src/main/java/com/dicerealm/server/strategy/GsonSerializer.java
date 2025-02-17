package com.dicerealm.server.strategy;

import org.springframework.ai.converter.BeanOutputConverter;

import com.dicerealm.core.strategy.JsonSerializationStrategy;
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

	@Override
	public String makeJsonSchema(Class<?> schema) {
		// We use the BeanOutputConverter to generate the JSON schema instead of gson because gson does not support JSON schema generation
		BeanOutputConverter<?> outputConverter = new BeanOutputConverter<>(schema);
		return outputConverter.getJsonSchema();
	}
}
