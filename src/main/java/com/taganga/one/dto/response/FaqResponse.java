package com.taganga.one.dto.response;

public record FaqResponse(
        String code,
        String question,
        String answer
) {
}
