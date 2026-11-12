package com.taganga.one.service.impl;

import com.taganga.one.domain.Visitor;
import com.taganga.one.dto.request.CreateVisitorRequest;
import com.taganga.one.dto.request.VisitorLoginRequest;
import com.taganga.one.dto.response.VisitorResponse;
import com.taganga.one.exception.BusinessRuleException;
import com.taganga.one.exception.InvalidCredentialsException;
import com.taganga.one.mapper.VisitorMapper;
import com.taganga.one.repository.VisitorRepository;
import com.taganga.one.service.VisitorService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class VisitorServiceImpl implements VisitorService {

    static final String LEGAL_BASIS = "Ley 1581 de 2012 / Decreto 1377 de 2013";

    static final String INVALID_CREDENTIALS = "Correo o contraseña incorrectos.";

    static final String EMAIL_TAKEN =
            "Ese correo ya está registrado con otra contraseña. Ingresa con tu contraseña.";

    private final VisitorRepository repository;

    private final VisitorMapper mapper;

    private final PasswordEncoder passwordEncoder;

    private final Clock clock;

    public VisitorServiceImpl(
            VisitorRepository repository,
            VisitorMapper mapper,
            PasswordEncoder passwordEncoder,
            Clock clock) {

        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Override
    @Transactional
    public VisitorResponse register(CreateVisitorRequest request) {

        // Regla 1: la visita no puede ser en una fecha pasada.
        if (request.visitDate().isBefore(LocalDate.now(clock))) {
            throw new BusinessRuleException(
                "La fecha de visita no puede ser anterior a hoy."
            );
        }

        String email = normalizeEmail(request.email());
        String fullName = request.fullName().trim();
        String origin = request.origin().trim();

        // Regla 2: un correo tiene un solo registro. Solo quien conoce la contraseña
        // puede reemplazarlo; si no coincide, el registro se rechaza.
        Visitor visitor = repository.findByEmail(email)
                .map(existing -> {
                    if (!passwordEncoder.matches(request.password(), existing.getPasswordHash())) {
                        throw new BusinessRuleException(EMAIL_TAKEN);
                    }
                    existing.replaceRegistration(
                            nextVisitorCode(), fullName, origin,
                            request.groupSize(), request.visitDate(), request.reason());
                    return existing;
                })
                .orElseGet(() -> new Visitor(
                        nextVisitorCode(), fullName, email,
                        passwordEncoder.encode(request.password()), origin,
                        request.groupSize(), request.visitDate(), request.reason()));

        // Regla 3: toda autorización de datos queda registrada como prueba (Ley 1581).
        visitor.recordConsent(LEGAL_BASIS, LocalDateTime.now(clock));

        return mapper.toResponse(repository.save(visitor));
    }

    @Override
    public VisitorResponse login(VisitorLoginRequest request) {

        return repository.findByEmail(normalizeEmail(request.email()))
                .filter(visitor -> passwordEncoder.matches(
                        request.password(), visitor.getPasswordHash()))
                .map(mapper::toResponse)
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));
    }

    private String nextVisitorCode() {
        return "TG-" + repository.nextVisitorCodeNumber();
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
