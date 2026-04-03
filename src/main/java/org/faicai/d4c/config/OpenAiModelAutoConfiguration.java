package org.faicai.d4c.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = ModelParentProperties.class)
public class OpenAiModelAutoConfiguration {


	/**
	 * 聊天模型
	 * @return OpenAiChatModel
	 */
	@Bean
	public OpenAiChatModel openAiChatModel(ModelParentProperties modelParentProperties) {
		ModelParentProperties.Properties chat = modelParentProperties.getChat();
		OpenAiApi openAiApi = OpenAiApi.builder()
				.baseUrl(chat.getBaseUrl())
				.apiKey(chat.getSimpleApiKey())
				.completionsPath(chat.getPath())
				.build();
		OpenAiChatOptions options = OpenAiChatOptions.builder().build();
		options.setModel(chat.getModel());
		return OpenAiChatModel.builder().defaultOptions(options).openAiApi(openAiApi).build();
	}



}
