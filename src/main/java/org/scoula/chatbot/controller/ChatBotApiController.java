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
    @PostMapping("/message")
    public ApiResponse<?> sendMessage(@RequestParam String sessionId, @RequestParam String userMessage) {
        ChatSession session = sessionManager.getSession(sessionId);
        if(session == null) return ApiResponse.fail(ResponseCode.CHATGPT_JSON_PARSING_FAILED);
        session.addMessage(new ChatMessage("user", userMessage));

        String response = chatGptService.summarize(session.getMessages());
        session.addMessage(new ChatMessage("assistant", response));
        return ApiResponse.success(ResponseCode.CHATBOT_RESPONSE_SUCCESS, response);
    }

    @DeleteMapping("/session")
    public ApiResponse<?> endSession(@RequestParam String sessionId) {
        sessionManager.removeSession(sessionId);
        return ApiResponse.success(ResponseCode.CHATBOT_SESSION_TERMINATED);
    }
}
