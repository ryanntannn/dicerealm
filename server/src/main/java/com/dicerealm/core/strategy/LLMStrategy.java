package com.dicerealm.core.strategy;

import java.util.stream.Stream;

public interface LLMStrategy {
	public abstract Stream<String> prompt(String prompt);
	public abstract String promptStr(String prompt);
	public abstract <T> T promptSchema(String systemPrompt, String userPrompt, Class<T> schema);
}
