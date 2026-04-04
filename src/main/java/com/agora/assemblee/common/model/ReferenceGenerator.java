package com.agora.assemblee.common.model;

import java.time.Instant;
import java.util.UUID;

public final class ReferenceGenerator {
    private ReferenceGenerator() {}

    public static String generate(String prefix) {
        String clean = prefix == null ? "REF" : prefix.replaceAll("[^A-Za-z]", "").toUpperCase();
        clean = clean.length() > 6 ? clean.substring(0, 6) : clean;
        return clean + "-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
