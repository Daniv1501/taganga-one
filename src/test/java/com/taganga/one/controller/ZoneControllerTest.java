package com.taganga.one.controller;

import com.taganga.one.domain.ZoneGeometry;
import com.taganga.one.domain.ZoneType;
import com.taganga.one.dto.response.CoordinateResponse;
import com.taganga.one.dto.response.ZoneResponse;
import com.taganga.one.exception.GlobalExceptionHandler;
import com.taganga.one.service.ZoneService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ZoneController.class)
@Import(GlobalExceptionHandler.class)
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ZoneService service;

    @Test
    void shouldListZones() throws Exception {
        when(service.findAll()).thenReturn(List.of(new ZoneResponse(
                "CAPILLA_SAN_PEDRO", ZoneType.SACRED, "Capilla de San Pedro (religioso)",
                "Templo del patrono de los pescadores.", "Visita en silencio.",
                ZoneGeometry.POINT, List.of(new CoordinateResponse(11.2672, -74.1882)))));

        mockMvc.perform(get("/api/zones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("CAPILLA_SAN_PEDRO"))
                .andExpect(jsonPath("$[0].type").value("SACRED"))
                .andExpect(jsonPath("$[0].geometry").value("POINT"))
                .andExpect(jsonPath("$[0].coordinates[0].latitude").value(11.2672))
                .andExpect(jsonPath("$[0].coordinates[0].longitude").value(-74.1882));
    }
}
