package org.scoula.chatbot.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import org.scoula.chatbot.session.ChatSession;
import org.scoula.chatbot.session.ChatSessionManager;
import org.scoula.chatgpt.dto.gpt.ChatMessage;
import org.scoula.chatgpt.service.ChatGptService;
import org.scoula.response.ApiResponse;
import org.scoula.response.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@Api(tags = "ChatBot API", description = "ChatGPT 기반 챗봇 세션 및 메시지 전송 API")
public class ChatBotApiController {

    private final ChatGptService chatGptService;
    private final ChatSessionManager sessionManager;

    @Value("${chatgpt.chatbot-prompt}")
    private String chatbotPrompt;

    public ChatBotApiController(ChatGptService chatGptService, ChatSessionManager sessionManager) {
        this.chatGptService = chatGptService;
        this.sessionManager = sessionManager;
    }

    @ApiOperation(
            value = "챗봇 세션 생성",
            notes = "새로운 챗봇 세션을 생성합니다. sessionId는 클라이언트가 지정합니다."
    )
    @PostMapping("/session")
    public ApiResponse<?> startSession(
            @ApiParam(value = "클라이언트가 지정한 세션 ID", required = true)
            @RequestParam String sessionId
    ) {
        ChatSession session = sessionManager.createSession(sessionId);
        return ApiResponse.success(ResponseCode.CHATBOT_SESSION_CREATED, sessionId);
    }

    @ApiOperation(
            value = "챗봇 메시지 전송",
            notes = "지정된 세션에 사용자의 메시지를 전송하고, GPT의 응답을 반환합니다."
    )
    @PostMapping("/message") public ApiResponse<?> sendMessage(
            @ApiParam(value = "세션 ID", required = true, example = "session123")
            @RequestParam String sessionId,

            @ApiParam(value = "사용자 메시지", required = true, example = "ISA 계좌가 뭐야?")
            @RequestParam String userMessage
    ) {
        ChatSession session = sessionManager.getSession(sessionId);
        if (session == null)
            return ApiResponse.fail(ResponseCode.CHATGPT_JSON_PARSING_FAILED);

        session.addMessage(new ChatMessage("user", userMessage));

        String content = chatbotPrompt + "\n\n" + userMessage;
        List<ChatMessage> messages = session.getMessages();
        messages.add(0, new ChatMessage("system", chatbotPrompt)); // 프롬프트는 최상단에 1회만

        String response = chatGptService.chat(messages);

        session.addMessage(new ChatMessage("assistant", response));
        return ApiResponse.success(ResponseCode.CHATBOT_RESPONSE_SUCCESS, response);
    }

    @ApiOperation(
            value = "챗봇 세션 종료",
            notes = "지정된 세션 ID의 챗봇 세션을 종료합니다."
    )
    @DeleteMapping("/session")
    public ApiResponse<?> endSession(
            @ApiParam(value = "세션 ID", required = true) @RequestParam String sessionId
    ) {
        sessionManager.removeSession(sessionId);
        return ApiResponse.success(ResponseCode.CHATBOT_SESSION_TERMINATED);
    }
}
