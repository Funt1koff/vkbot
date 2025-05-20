package com.funtikov.dto.gpt;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GptPromptTask {
    Long userId;
    String prompt;
}
