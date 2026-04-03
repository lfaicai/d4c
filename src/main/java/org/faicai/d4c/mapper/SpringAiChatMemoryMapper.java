package org.faicai.d4c.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.faicai.d4c.pojo.entity.SpringAiChatMemory;

@Mapper
public interface SpringAiChatMemoryMapper extends BaseMapper<SpringAiChatMemory> {


    @Update(" update spring_ai_chat_memory set conversation_id = #{newConversationId} where conversation_id = #{oldConversationId} ")
    int updateConversationIdByOldConversationId(@Param("oldConversationId") String oldConversationId, @Param("newConversationId") String newConversationId);

}
