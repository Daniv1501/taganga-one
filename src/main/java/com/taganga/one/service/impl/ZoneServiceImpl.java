package com.taganga.one.service.impl;

import com.taganga.one.dto.response.ZoneResponse;
import com.taganga.one.mapper.ZoneMapper;
import com.taganga.one.repository.ZoneRepository;
import com.taganga.one.service.ZoneService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository repository;

    private final ZoneMapper mapper;

    public ZoneServiceImpl(ZoneRepository repository, ZoneMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ZoneResponse> findAll() {
        return repository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
