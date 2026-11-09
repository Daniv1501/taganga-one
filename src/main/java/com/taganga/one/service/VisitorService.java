package com.taganga.one.service;

import com.taganga.one.dto.request.CreateVisitorRequest;
import com.taganga.one.dto.request.VisitorLoginRequest;
import com.taganga.one.dto.response.VisitorResponse;

public interface VisitorService {

    VisitorResponse register(CreateVisitorRequest request);

    VisitorResponse login(VisitorLoginRequest request);
}
