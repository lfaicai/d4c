package org.faicai.d4c.controller;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.service.ChatService;
import org.faicai.d4c.utils.R;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatService chatService;


    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam("prompt") String prompt, @RequestParam("chatId") String chatId,
                             @RequestParam("databaseConnectId") Long databaseConnectId,
                             @RequestParam("databaseName") String databaseName) {
        return chatService.chat(prompt, chatId, databaseConnectId, databaseName);
    }

    @PutMapping("/stop/{chatId}")
    public R<Boolean> stop(@PathVariable("chatId") String chatId) {
        this.chatService.stop(chatId);
        return R.ok(true);
    }


    @PutMapping("/updateChatId/{oldChatId}/{newChatId}")
    public R<Boolean> updateChatId(@PathVariable("oldChatId") String oldChatId, @PathVariable("newChatId") String newChatId){
        return R.ok(this.chatService.updateChatId(oldChatId, newChatId));
    }

}
