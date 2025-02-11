package com.ateam.server.handlers;

import java.util.stream.Stream;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import com.ateam.core.LLMStrategy;


public class OpenAI extends LLMStrategy {
	OpenAiApi api = 
		OpenAiApi
			.builder()
			.apiKey(System.getenv("OPENAI_API_KEY"))
			.build();

	OpenAiChatOptions options = OpenAiChatOptions.builder()
		.model("gpt-3.5-turbo")
		.temperature(0.4)
		.maxTokens(200)
		.build();

	OpenAiChatModel chatModel = 
		OpenAiChatModel.builder()
			.openAiApi(api)
			.defaultOptions(options)
			.build();

	@Override
	public Stream<String> prompt(String prompt) {
			return this.chatModel.stream(prompt)
				.onErrorReturn("An error occurred while processing the request.")
				.toStream();
	}
}
