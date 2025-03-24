package com.dicerealm.mock;

import java.util.stream.Stream;

import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;

/**
 * A mock implementation of the LLMStrategy interface that returns a fixed response. Use this when testing to save on API calls.
 */
public class MockLLMStrategy implements LLMStrategy {

		private String response = "{\"displayText\": \"mock response\", \"actionChoices\":[]}";
		private JsonSerializationStrategy jsonSerializationStrategy;
		private String latestPrompt = null;

		public MockLLMStrategy(JsonSerializationStrategy jsonSerializationStrategy) {
			this.jsonSerializationStrategy = jsonSerializationStrategy;
		}

		public void setResponse(String response) {
			this.response = response;
		}

		public String getLatestPrompt() {
			return latestPrompt;
		}

		@Override
		public Stream<String> prompt(String prompt) {
			latestPrompt = prompt;
			return Stream.of(response);
		}

		@Override
		public String promptStr(String prompt) {
			latestPrompt = prompt;
			return response;
		}

		@Override
		public <T> T promptSchema(String systemPrompt, String userPrompt, Class<T> schema) {
			latestPrompt = systemPrompt;
			return jsonSerializationStrategy.deserialize(response, schema);
		}
}
