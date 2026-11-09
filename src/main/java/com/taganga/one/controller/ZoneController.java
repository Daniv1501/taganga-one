package com.taganga.one.controller;

import com.taganga.one.dto.response.ZoneResponse;
import com.taganga.one.service.ZoneService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService service;

    public ZoneController(ZoneService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ZoneResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
