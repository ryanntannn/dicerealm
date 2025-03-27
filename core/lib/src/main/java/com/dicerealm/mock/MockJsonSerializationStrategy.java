package com.dicerealm.mock;

import com.dicerealm.core.strategy.JsonSerializationStrategy;

public class MockJsonSerializationStrategy implements JsonSerializationStrategy {

	private Object out;

	public void setOut(Object out) {
		this.out = out;
	}

	@Override
	public String serialize(Object object) {
		return  object.toString();
	}

	@Override
	public <T> T deserialize(String json, Class<T> clazz) {
		return (T) out;
	}

	@Override
	public String makeJsonSchema(Class<?> schema) {
		return (String) out;
	}
	
}
