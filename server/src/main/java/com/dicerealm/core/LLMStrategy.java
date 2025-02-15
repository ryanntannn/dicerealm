package com.dicerealm.core;

import java.util.stream.Stream;

public abstract class LLMStrategy {
	public abstract Stream<String> prompt(String prompt);
	public abstract String promptStr(String prompt);
	public abstract <T> T promptSchema(String prompt, Class<T> schema);
}
