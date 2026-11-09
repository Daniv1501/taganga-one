package com.taganga.one.service.impl;

import com.taganga.one.dto.response.DecalogueRuleResponse;
import com.taganga.one.mapper.DecalogueMapper;
import com.taganga.one.repository.DecalogueRuleRepository;
import com.taganga.one.service.DecalogueService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DecalogueServiceImpl implements DecalogueService {

    private final DecalogueRuleRepository repository;

    private final DecalogueMapper mapper;

    public DecalogueServiceImpl(DecalogueRuleRepository repository, DecalogueMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<DecalogueRuleResponse> findAll() {
        return repository.findAllByOrderByRuleOrderAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
