package com.ateam.core;

import java.util.stream.Stream;

public abstract class LLMStrategy {
	public abstract Stream<String> prompt(String prompt);
	public abstract String promptStr(String prompt);
}
