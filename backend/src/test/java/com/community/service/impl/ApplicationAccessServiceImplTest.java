package com.community.service.impl;

import com.community.common.BusinessException;
import com.community.entity.enums.ApplicationStatus;
import com.community.entity.po.ApplicationForm;
import com.community.entity.vo.UserVO;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationAccessServiceImplTest {
    private final ApplicationAccessServiceImpl accessService = new ApplicationAccessServiceImpl();

    @Test
    void clerkCanReviewButCannotApproveOwnApplication() {
        ApplicationForm form = form(ApplicationStatus.PROCESSING, 7L);
        UserVO clerk = user(7L, "CLERK");
        assertDoesNotThrow(() -> accessService.assertCanReview(form, clerk));
        assertThrows(BusinessException.class, () -> accessService.assertCanApprove(form, clerk));
    }

    @Test
    void reviewerCanOnlySeeProcessingApplication() {
        UserVO reviewer = user(8L, "REVIEWER");
        assertDoesNotThrow(() -> accessService.assertCanView(form(ApplicationStatus.PROCESSING, 7L), reviewer));
        assertThrows(BusinessException.class,
                () -> accessService.assertCanView(form(ApplicationStatus.PENDING, null), reviewer));
    }

    private ApplicationForm form(ApplicationStatus status, Long handlerId) {
        ApplicationForm form = new ApplicationForm();
        form.setStatus(status.name());
        form.setHandlerId(handlerId);
        return form;
    }

    private UserVO user(Long id, String role) {
        UserVO user = new UserVO();
        user.setId(id);
        user.setRoles(Set.of(role));
        return user;
    }
}
