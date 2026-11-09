package com.taganga.one.controller;

import com.taganga.one.dto.request.AskAssistantRequest;
import com.taganga.one.dto.response.AssistantAnswerResponse;
import com.taganga.one.dto.response.FaqResponse;
import com.taganga.one.exception.GlobalExceptionHandler;
import com.taganga.one.service.AssistantService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssistantController.class)
@Import(GlobalExceptionHandler.class)
class AssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssistantService service;

    @Test
    void shouldListFaqs() throws Exception {
        when(service.findFaqs()).thenReturn(List.of(
                new FaqResponse("llegar", "¿Cómo llego a Playa Grande?", "<p>Hay dos formas</p>")));

        mockMvc.perform(get("/api/assistant/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("llegar"))
                .andExpect(jsonPath("$[0].question").value("¿Cómo llego a Playa Grande?"))
                .andExpect(jsonPath("$[0].answer").value("<p>Hay dos formas</p>"));
    }

    @Test
    void shouldAnswerQuestion() throws Exception {
        when(service.ask(any(AskAssistantRequest.class)))
                .thenReturn(new AssistantAnswerResponse("<p>¡Hola, Ana!</p>"));

        mockMvc.perform(post("/api/assistant/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "question": "hola", "visitorName": "Ana" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("<p>¡Hola, Ana!</p>"));
    }

    @Test
    void shouldRejectBlankQuestion() throws Exception {
        mockMvc.perform(post("/api/assistant/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "question": "   " }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.question").value("Escribe tu pregunta."));

        verify(service, never()).ask(any());
    }

    @Test
    void shouldRejectQuestionLongerThan300Characters() throws Exception {
        String question = "a".repeat(301);

        mockMvc.perform(post("/api/assistant/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"question\": \"" + question + "\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.question")
                        .value("La pregunta admite máximo 300 caracteres."));

        verify(service, never()).ask(any());
    }
}
