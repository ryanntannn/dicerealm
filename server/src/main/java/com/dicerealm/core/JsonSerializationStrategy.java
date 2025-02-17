package com.dicerealm.core;

public interface JsonSerializationStrategy {
	public abstract String serialize(Object object);
	public abstract <T> T deserialize(String json, Class<T> object);
	public abstract String makeJsonSchema(Class<?> schema);
}
