package org.faicai.d4c;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(
        exclude = {
                org.springframework.ai.model.openai.autoconfigure.OpenAiAudioSpeechAutoConfiguration.class,
                org.springframework.ai.model.openai.autoconfigure.OpenAiAudioTranscriptionAutoConfiguration.class,
                org.springframework.ai.model.openai.autoconfigure.OpenAiImageAutoConfiguration.class,
                org.springframework.ai.model.openai.autoconfigure.OpenAiModerationAutoConfiguration.class,
                org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration.class,
                org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
@MapperScan("org.faicai.d4c.mapper")
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration.class,

        })
})
public class D4CApplication {

    public static void main(String[] args) {
        SpringApplication.run(D4CApplication.class, args);
    }

}