package com.taganga.one.mapper;

import com.taganga.one.domain.Zone;
import com.taganga.one.dto.response.ZoneResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    ZoneResponse toResponse(Zone zone);
}
