package com.taganga.one.service;

import com.taganga.one.domain.DecalogueRule;
import com.taganga.one.dto.response.DecalogueRuleResponse;
import com.taganga.one.mapper.DecalogueMapper;
import com.taganga.one.repository.DecalogueRuleRepository;
import com.taganga.one.service.impl.DecalogueServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecalogueServiceImplTest {

    @Mock
    private DecalogueRuleRepository repository;

    @Mock
    private DecalogueMapper mapper;

    @InjectMocks
    private DecalogueServiceImpl service;

    @Test
    void shouldReturnRulesInOrder() {
        DecalogueRule rule = new DecalogueRule(1, "Regístrate", "antes de ingresar.");
        DecalogueRuleResponse response =
                new DecalogueRuleResponse(1, "Regístrate", "antes de ingresar.");

        when(repository.findAllByOrderByRuleOrderAsc()).thenReturn(List.of(rule));
        when(mapper.toResponse(rule)).thenReturn(response);

        assertThat(service.findAll()).containsExactly(response);
    }
}
