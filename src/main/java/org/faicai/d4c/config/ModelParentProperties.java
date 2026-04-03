package org.faicai.d4c.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


@Data
@Configuration
@ConfigurationProperties(ModelParentProperties.CONFIG_PREFIX)
public class ModelParentProperties {
    public static final String CONFIG_PREFIX = "ai-model";

    /**
     * 聊天模型参数
     */
    private Properties chat;

    /**
     * 向量模型参数
     */
    private Properties embedding;

    /**
     * 生成式模型参数_从文档内容中提取关键词并将其作为元数据
     */
    private Properties generative;



    @Getter
    @Validated
    static class Properties {
        @NotBlank
        private String apiKey;

        @NotBlank
        private String baseUrl;
        @NotBlank
        private String model;

        private String path;

        private SimpleApiKey simpleApiKey;


        public void setApiKey(String apiKey) {
            simpleApiKey = new SimpleApiKey(apiKey);
        }

        public void setBaseUrl(String baseUrl) {
            splitUrlAndeSet(baseUrl);
        }

        public void setModel(String model) {
            this.model = model;
        }

        private void splitUrlAndeSet(String url) {
            int protocolEnd = url.indexOf("://") + 3;
            if (protocolEnd < 3) {
                return;
            }
            // 从协议后查找第一个路径斜杠位置
            int pathStart = url.indexOf("/", protocolEnd);
            String baseUrl = (pathStart == -1) ? url : url.substring(0, pathStart);
            String path = (pathStart == -1) ? "" : url.substring(pathStart);
            this.baseUrl = baseUrl;
            this.path = path;
        }
    }
}