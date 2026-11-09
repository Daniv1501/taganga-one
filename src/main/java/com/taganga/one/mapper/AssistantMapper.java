package com.taganga.one.mapper;

import com.taganga.one.domain.AssistantEntry;
import com.taganga.one.dto.response.FaqResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssistantMapper {

    FaqResponse toFaqResponse(AssistantEntry entry);
}
