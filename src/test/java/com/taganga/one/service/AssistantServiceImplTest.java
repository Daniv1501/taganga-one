package com.taganga.one.service;

import com.taganga.one.domain.AssistantEntry;
import com.taganga.one.domain.AssistantEntryType;
import com.taganga.one.dto.request.AskAssistantRequest;
import com.taganga.one.dto.response.FaqResponse;
import com.taganga.one.mapper.AssistantMapper;
import com.taganga.one.repository.AssistantEntryRepository;
import com.taganga.one.service.impl.AssistantServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssistantServiceImplTest {

    private static final AssistantEntry SACRED = new AssistantEntry(
            "sagrados", AssistantEntryType.FAQ, "¿Sitios sagrados?", "<p>sagrados</p>", 1,
            List.of("sagrad", "cerro"));

    private static final AssistantEntry FISHING = new AssistantEntry(
            "pesca", AssistantEntryType.FAQ, "¿Pesca?", "<p>pesca</p>", 2,
            List.of("bahía", "cerro", "lancha"));

    private static final AssistantEntry GREETING = new AssistantEntry(
            "saludo", AssistantEntryType.EXTRA, null, "<p>¡Hola{visitor}!</p>", 3,
            List.of("hola"));

    private static final AssistantEntry THANKS = new AssistantEntry(
            "gracias", AssistantEntryType.EXTRA, null, "<p>Con gusto</p>", 4,
            List.of("gracias", "hola"));

    @Mock
    private AssistantEntryRepository repository;

    @Mock
    private AssistantMapper mapper;

    @InjectMocks
    private AssistantServiceImpl service;

    @BeforeEach
    void setUp() {
        when(repository.findByTypeOrderByDisplayOrderAsc(AssistantEntryType.FAQ))
                .thenReturn(List.of(SACRED, FISHING));
        when(repository.findByTypeOrderByDisplayOrderAsc(AssistantEntryType.EXTRA))
                .thenReturn(List.of(GREETING, THANKS));
    }

    private String ask(String question, String name) {
        return service.ask(new AskAssistantRequest(question, name)).answer();
    }

    @Test
    void shouldReturnFaqWithMostKeywordMatches() {
        assertThat(ask("¿qué lancha va al cerro?", null)).isEqualTo("<p>pesca</p>");
    }

    @Test
    void shouldPreferFirstFaqOnTie() {
        assertThat(ask("hablame del cerro", null)).isEqualTo("<p>sagrados</p>");
    }

    @Test
    void shouldIgnoreAccentsAndCase() {
        assertThat(ask("LA BAHIA", null)).isEqualTo("<p>pesca</p>");
        assertThat(ask("Sitio SAGRÁDO", null)).isEqualTo("<p>sagrados</p>");
    }

    @Test
    void shouldPreferFaqOverExtra() {
        assertThat(ask("hola, ¿y el cerro?", "Ana")).isEqualTo("<p>sagrados</p>");
    }

    @Test
    void shouldUseFirstMatchingExtraWithVisitorName() {
        assertThat(ask("hola", "Ana")).isEqualTo("<p>¡Hola, Ana!</p>");
    }

    @Test
    void shouldGreetWithoutNameWhenAbsent() {
        assertThat(ask("hola", null)).isEqualTo("<p>¡Hola!</p>");
        assertThat(ask("hola", "  ")).isEqualTo("<p>¡Hola!</p>");
    }

    @Test
    void shouldEscapeVisitorNameBecauseAnswerIsRenderedAsHtml() {
        assertThat(ask("hola", "<img src=x onerror=alert(1)>"))
                .doesNotContain("<img")
                .contains("&lt;img");
    }

    @Test
    void shouldReturnFallbackWhenNothingMatches() {
        assertThat(ask("xyz", null)).contains("Aún no tengo información precisa");
    }

    @Test
    void shouldListFaqsInOrder() {
        FaqResponse first = new FaqResponse("sagrados", "¿Sitios sagrados?", "<p>sagrados</p>");
        FaqResponse second = new FaqResponse("pesca", "¿Pesca?", "<p>pesca</p>");
        when(mapper.toFaqResponse(SACRED)).thenReturn(first);
        when(mapper.toFaqResponse(FISHING)).thenReturn(second);

        assertThat(service.findFaqs()).containsExactly(first, second);
    }
}
