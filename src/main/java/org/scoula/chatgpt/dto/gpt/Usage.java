package org.scoula.chatgpt.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usage {

    @JsonProperty("prompt_tokens")
    private long promptTokens;

    @JsonProperty("completion_tokens")
    private long completionTokens;

    @JsonProperty("total_tokens")
    private long totalTokens;

    @JsonProperty("prompt_tokens_details")
    private Object promptTokensDetails;

    @JsonProperty("completion_tokens_details")
    private Object completionTokensDetails;
    public Usage() {
    }

    public Usage(long promptTokens,
                 long completionTokens,
                 long totalTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
    }
}