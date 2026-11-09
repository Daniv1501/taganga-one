package com.taganga.one.controller;

import com.taganga.one.dto.response.DecalogueRuleResponse;
import com.taganga.one.exception.GlobalExceptionHandler;
import com.taganga.one.service.DecalogueService;

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

@WebMvcTest(DecalogueController.class)
@Import(GlobalExceptionHandler.class)
class DecalogueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DecalogueService service;

    @Test
    void shouldListRules() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new DecalogueRuleResponse(1, "Regístrate", "antes de ingresar.")));

        mockMvc.perform(get("/api/decalogue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleOrder").value(1))
                .andExpect(jsonPath("$[0].highlight").value("Regístrate"))
                .andExpect(jsonPath("$[0].body").value("antes de ingresar."));
    }
}
