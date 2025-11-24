/*
 * Copyright (c) 2025 AMAK Inc. All rights reserved.
 */

package tech.amak.portbuddy.server.web.dto;

/**
 * Response with public exposure details.
 */
public record ExposeResponse(
    String source,
    String publicUrl,
    String publicHost,
    Integer publicPort
) {
}
