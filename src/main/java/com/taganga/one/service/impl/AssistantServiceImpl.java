package com.taganga.one.service.impl;

import com.taganga.one.domain.AssistantEntry;
import com.taganga.one.domain.AssistantEntryType;
import com.taganga.one.dto.request.AskAssistantRequest;
import com.taganga.one.dto.response.AssistantAnswerResponse;
import com.taganga.one.dto.response.FaqResponse;
import com.taganga.one.mapper.AssistantMapper;
import com.taganga.one.repository.AssistantEntryRepository;
import com.taganga.one.service.AssistantService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/**
 * Asistente simulado: el mismo motor de palabras clave del prototipo, sin IA real.
 */
@Service
@Transactional(readOnly = true)
public class AssistantServiceImpl implements AssistantService {

    private static final String VISITOR_PLACEHOLDER = "{visitor}";

    private static final String FALLBACK_ANSWER = """
            <p>Aún no tengo información precisa sobre eso. Puedo ayudarte con:</p>
            <ul><li>Sitios sagrados y religiosos</li><li>Normas de protección ambiental</li>
            <li>Zonas de pesca artesanal</li><li>Historia comunitaria</li><li>Tus datos personales</li></ul>
            <p>También puedes consultar a la autoridad comunitaria en la playa principal.</p>""";

    private final AssistantEntryRepository repository;

    private final AssistantMapper mapper;

    public AssistantServiceImpl(AssistantEntryRepository repository, AssistantMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<FaqResponse> findFaqs() {
        return repository.findByTypeOrderByDisplayOrderAsc(AssistantEntryType.FAQ)
                .stream()
                .map(mapper::toFaqResponse)
                .toList();
    }

    @Override
    public AssistantAnswerResponse ask(AskAssistantRequest request) {
        return new AssistantAnswerResponse(
                personalize(findAnswer(normalize(request.question())), request.visitorName())
        );
    }

    private String findAnswer(String question) {

        // 1. Gana la pregunta frecuente con más palabras clave; en empate, la primera.
        String best = null;
        long bestScore = 0;
        for (AssistantEntry entry : repository.findByTypeOrderByDisplayOrderAsc(AssistantEntryType.FAQ)) {
            long score = entry.getKeywords().stream()
                    .filter(keyword -> question.contains(normalize(keyword)))
                    .count();
            if (score > bestScore) {
                bestScore = score;
                best = entry.getAnswer();
            }
        }
        if (best != null) {
            return best;
        }

        // 2. Si ninguna coincidió, la primera respuesta adicional con alguna palabra clave.
        for (AssistantEntry entry : repository.findByTypeOrderByDisplayOrderAsc(AssistantEntryType.EXTRA)) {
            boolean matches = entry.getKeywords().stream()
                    .anyMatch(keyword -> question.contains(normalize(keyword)));
            if (matches) {
                return entry.getAnswer();
            }
        }

        // 3. Sin coincidencias: se ofrecen los temas disponibles.
        return FALLBACK_ANSWER;
    }

    // La respuesta se muestra como HTML: el nombre, que escribe el usuario, se escapa.
    private String personalize(String answer, String visitorName) {
        String greeting = visitorName == null || visitorName.isBlank()
                ? ""
                : ", " + HtmlUtils.htmlEscape(visitorName.trim());
        return answer.replace(VISITOR_PLACEHOLDER, greeting);
    }

    // Minúsculas y sin tildes, para que "Bahía" y "bahia" coincidan.
    private String normalize(String text) {
        return Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
