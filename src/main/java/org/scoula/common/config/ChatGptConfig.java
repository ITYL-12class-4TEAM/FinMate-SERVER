package org.scoula.common.config;

import org.scoula.chatgpt.util.ChatGptUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatGptConfig {

    @Bean
    public ChatGptUtil chatGptUtil() {
        return new ChatGptUtil();
    }
}
