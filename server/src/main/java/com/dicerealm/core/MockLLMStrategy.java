package com.dicerealm.core;

import java.util.stream.Stream;

import org.springframework.ai.converter.BeanOutputConverter;

/**
 * A mock implementation of the LLMStrategy interface that returns a fixed response. Use this when testing to save on API calls.
 */
public class MockLLMStrategy implements LLMStrategy {

	private String mockResponse;

	public MockLLMStrategy(String mockResponse) {
		this.mockResponse = mockResponse;
	}

	public MockLLMStrategy() {
		this.mockResponse = "<mock response>";
	}

	public void setMockResponse(String mockResponse) {
		this.mockResponse = mockResponse;
	}

	@Override
	public Stream<String> prompt(String prompt) {
		return mockResponse.lines();
	}

	@Override
	public String promptStr(String prompt) {
		return mockResponse;
	}

	@Override
	public <T> T promptSchema(String prompt, Class<T> schema) {
		BeanOutputConverter<T> outputConverter = new BeanOutputConverter<T>(schema);
		return outputConverter.convert(mockResponse);
	}
}
