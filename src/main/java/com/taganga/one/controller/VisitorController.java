package com.taganga.one.controller;

import com.taganga.one.dto.request.CreateVisitorRequest;
import com.taganga.one.dto.request.VisitorLoginRequest;
import com.taganga.one.dto.response.VisitorResponse;
import com.taganga.one.service.VisitorService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visitors")
public class VisitorController {

    private final VisitorService service;

    public VisitorController(VisitorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VisitorResponse> register(
            @Valid @RequestBody CreateVisitorRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.register(request));
    }

    // POST y no GET: la contraseña no debe quedar en URLs ni en logs.
    @PostMapping("/login")
    public ResponseEntity<VisitorResponse> login(
            @Valid @RequestBody VisitorLoginRequest request) {

        return ResponseEntity.ok(service.login(request));
    }
}
