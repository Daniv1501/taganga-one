package com.taganga.one.service;

import com.taganga.one.dto.response.ZoneResponse;

import java.util.List;

public interface ZoneService {

    List<ZoneResponse> findAll();
}
