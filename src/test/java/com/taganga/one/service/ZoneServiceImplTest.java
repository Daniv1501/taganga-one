package com.taganga.one.service;

import com.taganga.one.domain.GeoPoint;
import com.taganga.one.domain.Zone;
import com.taganga.one.domain.ZoneGeometry;
import com.taganga.one.domain.ZoneType;
import com.taganga.one.dto.response.CoordinateResponse;
import com.taganga.one.dto.response.ZoneResponse;
import com.taganga.one.mapper.ZoneMapper;
import com.taganga.one.repository.ZoneRepository;
import com.taganga.one.service.impl.ZoneServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZoneServiceImplTest {

    @Mock
    private ZoneRepository repository;

    @Mock
    private ZoneMapper mapper;

    @InjectMocks
    private ZoneServiceImpl service;

    @Test
    void shouldReturnZonesInDisplayOrder() {
        Zone zone = new Zone("CAPILLA", ZoneType.SACRED, "Capilla", "Templo", "Silencio",
                ZoneGeometry.POINT, 1, List.of(new GeoPoint(11.26, -74.18)));
        ZoneResponse response = new ZoneResponse("CAPILLA", ZoneType.SACRED, "Capilla", "Templo",
                "Silencio", ZoneGeometry.POINT, List.of(new CoordinateResponse(11.26, -74.18)));

        when(repository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(zone));
        when(mapper.toResponse(zone)).thenReturn(response);

        assertThat(service.findAll()).containsExactly(response);
    }
}
