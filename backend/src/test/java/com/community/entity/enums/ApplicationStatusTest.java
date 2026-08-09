package com.community.entity.enums;

import com.community.common.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationStatusTest {
    @Test
    void acceptsOnlyInitialReviewTargets() {
        assertEquals(ApplicationStatus.PROCESSING,
                ApplicationStatus.auditResult("PROCESSING", Set.of(ApplicationStatus.PROCESSING, ApplicationStatus.REJECTED)));
        assertEquals(ApplicationStatus.REJECTED,
                ApplicationStatus.auditResult("REJECTED", Set.of(ApplicationStatus.PROCESSING, ApplicationStatus.REJECTED)));
    }

    @Test
    void rejectsLegacyOrUnexpectedReviewResult() {
        assertThrows(BusinessException.class,
                () -> ApplicationStatus.auditResult("APPROVED", Set.of(ApplicationStatus.PROCESSING, ApplicationStatus.REJECTED)));
    }
}
