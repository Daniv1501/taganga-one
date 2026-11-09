package com.taganga.one.controller;

import com.taganga.one.dto.request.AskAssistantRequest;
import com.taganga.one.dto.response.AssistantAnswerResponse;
import com.taganga.one.dto.response.FaqResponse;
import com.taganga.one.service.AssistantService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService service;

    public AssistantController(AssistantService service) {
        this.service = service;
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<FaqResponse>> findFaqs() {
        return ResponseEntity.ok(service.findFaqs());
    }

    @PostMapping("/questions")
    public ResponseEntity<AssistantAnswerResponse> ask(
            @Valid @RequestBody AskAssistantRequest request) {

        return ResponseEntity.ok(service.ask(request));
    }
}
