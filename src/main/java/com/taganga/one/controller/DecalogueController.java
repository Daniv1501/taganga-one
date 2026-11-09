package com.taganga.one.controller;

import com.taganga.one.dto.response.DecalogueRuleResponse;
import com.taganga.one.service.DecalogueService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/decalogue")
public class DecalogueController {

    private final DecalogueService service;

    public DecalogueController(DecalogueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DecalogueRuleResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
