package org.scoula.chatbot.controller;

import io.swagger.annotations.Api;
import org.scoula.chatbot.session.ChatSession;
import org.scoula.chatbot.session.ChatSessionManager;
import org.scoula.chatgpt.dto.gpt.ChatMessage;
import org.scoula.chatgpt.service.ChatGptService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotApiController {
    private final ChatGptService chatGptService;
    private final ChatSessionManager sessionManager;

    public ChatBotApiController(ChatGptService chatGptService, ChatSessionManager sessionManager) {
        this.chatGptService = chatGptService;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/session")
    public ApiResponse<?> startSession(@RequestParam String sessionId) {
        ChatSession session = sessionManager.createSession(sessionId);
        return ApiResponse.success(ResponseCode.CHATBOT_SESSION_CREATED, sessionId);
    }
}
