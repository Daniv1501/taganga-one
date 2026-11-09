package com.taganga.one.mapper;

import com.taganga.one.domain.Visitor;
import com.taganga.one.dto.response.VisitorResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisitorMapper {

    VisitorResponse toResponse(Visitor visitor);
}
