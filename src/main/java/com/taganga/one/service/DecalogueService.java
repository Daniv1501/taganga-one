package com.taganga.one.service;

import com.taganga.one.dto.response.DecalogueRuleResponse;

import java.util.List;

public interface DecalogueService {

    List<DecalogueRuleResponse> findAll();
}
