package org.scoula.chatgpt.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionResponse(
        String id,
        String object,
        long created,
        String model,
        List<ChatCompletionChoice> choices,
        Usage usage,
        @JsonProperty("service_tier") String serviceTier,
        @JsonProperty("system_fingerprint") String systemFingerprint

) {}
