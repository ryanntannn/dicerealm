package com.dicerealm.server.handlers;

import java.util.stream.Stream;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;

import com.dicerealm.core.JsonSerializationStrategy;
import com.dicerealm.core.LLMStrategy;

public class OpenAI implements LLMStrategy {

	private static final int MAX_TOKENS = 4096;

	OpenAiApi api = 
		OpenAiApi
			.builder()
			.apiKey(System.getenv("OPENAI_API_KEY"))
			.build();

	OpenAiChatOptions options = OpenAiChatOptions.builder()
		.model(ChatModel.GPT_3_5_TURBO)
		.temperature(0.4)
		.maxTokens(200)
		.build();

	OpenAiChatModel chatModel = 
		OpenAiChatModel.builder()
			.openAiApi(api)
			.defaultOptions(options)
			.build();

	private JsonSerializationStrategy serializer;

	public OpenAI(JsonSerializationStrategy serializer) {
		this.serializer = serializer;
	}

	@Override
	public Stream<String> prompt(String prompt) {
			return this.chatModel.stream(prompt)
				.onErrorReturn("An error occurred while processing the request.")
				.toStream();
	}

	@Override
	public String promptStr(String prompt) {
		return this.chatModel
			.call(prompt);
	}

	
	public <T> T promptSchema(String prompt, Class<T> schema) {
		// OpenAI's Structured Response API requires all fields to be required in the JSON schema
		String jsonSchema = JsonSchemaHelper.makeAllFieldsRequired(serializer.makeJsonSchema(schema));

		Prompt promptObj = new Prompt(prompt, OpenAiChatOptions.builder()
			.model(ChatModel.GPT_4_O)
			.responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
			.maxTokens(MAX_TOKENS)
			.build()
		);

		ChatResponse response = chatModel.call(promptObj);
		String content = response.getResult().getOutput().getText();
		
		return serializer.deserialize(content, schema);
	}
}
