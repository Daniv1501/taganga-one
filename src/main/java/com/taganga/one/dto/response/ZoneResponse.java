package com.taganga.one.dto.response;

import com.taganga.one.domain.ZoneGeometry;
import com.taganga.one.domain.ZoneType;

import java.util.List;

public record ZoneResponse(
        String code,
        ZoneType type,
        String name,
        String description,
        String rule,
        ZoneGeometry geometry,
        List<CoordinateResponse> coordinates
) {
}
