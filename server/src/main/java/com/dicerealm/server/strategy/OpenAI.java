package com.dicerealm.server.strategy;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;

import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;
import com.dicerealm.server.util.JsonSchemaHelper;

public class OpenAI implements LLMStrategy {

	private static final int MAX_TOKENS = 4096;

	OpenAiApi api = 
		OpenAiApi
			.builder()
			.apiKey(System.getenv("OPENAI_API_KEY"))
			.build();

	OpenAiChatOptions options = OpenAiChatOptions.builder()
		.model(ChatModel.GPT_4_O)
		.temperature(0.7)
		.topP(0.8)
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

	
	public <T> T promptSchema(String systemPrompt, String userPrompt, Class<T> schema) {
		// OpenAI's Structured Response API requires all fields to be required in the JSON schema
		String jsonSchema = JsonSchemaHelper.makeAllFieldsRequired(serializer.makeJsonSchema(schema));

		Message userMessage = new UserMessage(userPrompt);
		Message systemMessage = new SystemMessage(systemPrompt);

		Prompt promptObj = new Prompt(List.of(userMessage, systemMessage), OpenAiChatOptions.builder()
			.responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
			.maxTokens(MAX_TOKENS)
			.build()
		);

		ChatResponse response = chatModel.call(promptObj);
		String content = response.getResult().getOutput().getText();
		
		return serializer.deserialize(content, schema);
	}
}
