package com.taganga.one.mapper;

import com.taganga.one.domain.DecalogueRule;
import com.taganga.one.dto.response.DecalogueRuleResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DecalogueMapper {

    DecalogueRuleResponse toResponse(DecalogueRule rule);
}
