package com.taganga.one.service;

import com.taganga.one.dto.request.AskAssistantRequest;
import com.taganga.one.dto.response.AssistantAnswerResponse;
import com.taganga.one.dto.response.FaqResponse;

import java.util.List;

public interface AssistantService {

    List<FaqResponse> findFaqs();

    AssistantAnswerResponse ask(AskAssistantRequest request);
}
