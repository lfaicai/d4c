package org.faicai.d4c.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.faicai.d4c.mapper.SpringAiChatMemoryMapper;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.tool.ResourceTool;
import org.faicai.d4c.utils.SecurityUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final ResourceTool resourceTool;
    private final DataBaseConnectConfigService dataBaseConnectConfigService;
    private final SpringAiChatMemoryMapper springAiChatMemoryMapper;

    private final static String SYSTEM_PROMPT = """
            你是一位精通 SQL 的专家。请根据用户的自然语言问题，生成对应的 SQL 查询语句。
            在生成 SQL 前，你可以使用提供的工具查询数据库表结构（例如：`get_table_schema(表名)`），以获取必要的列名、数据类型和表关系。
            确保生成的 SQL 符合标准 SQL 语法，并且针对问题的需求进行优化。
            
            # 输出要求
            - **格式**：纯 Markdown 格式，便于阅读。
            
            # 大模型调用工具可能使用到的参数
            - 当前数据库连接ID：%s
            - 数据库名称：%s
            - 数据库类型：%s
            - 当前用户id: %s
            - 当前时间：%s
            """;

    private static final Map<String, Boolean> GENERATE_STATUS = new ConcurrentHashMap<>();

    public Flux<String> chat(String prompt, String sessionId, Long databaseConnectId, String databaseName) {
        DataBaseConnectConfig dataBaseConnectConfig = dataBaseConnectConfigService.getById(databaseConnectId);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        assert dataBaseConnectConfig != null;
        return this.chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .system(SYSTEM_PROMPT.formatted(dataBaseConnectConfig.getId(), databaseName, dataBaseConnectConfig.getDbType(), currentUserId, LocalDateTime.now()))
                .user(prompt)
                .tools(resourceTool)
                .stream()
                .chatResponse()
                .doFirst(() -> {  //输出开始，标记正在输出
                    GENERATE_STATUS.put(sessionId, true);
                })
                .doOnComplete(() -> { //输出结束，清除标记
                    GENERATE_STATUS.remove(sessionId);
                })// 输出过程中，判断是否正在输出，如果正在输出，则继续输出，否则结束输出
                .takeWhile(s -> Optional.ofNullable(GENERATE_STATUS.get(sessionId)).orElse(false))
                .mapNotNull(chatResponse -> {
                    // 封装响应对象
                    return chatResponse.getResult().getOutput().getText();
                }).concatWith(Flux.just("[DONE]"));
    }

    public void stop(String sessionId) {
        log.info("停止对话：{}", sessionId);
        // 移除标记
        GENERATE_STATUS.remove(sessionId);
    }

    public Boolean updateChatId(String oldChatId, String newChatId) {
        return springAiChatMemoryMapper.updateConversationIdByOldConversationId(oldChatId, newChatId) > 0;
    }
}
