package com.community.service.impl;

import com.community.common.BusinessException;
import com.community.entity.enums.ApplicationStatus;
import com.community.entity.po.ApplicationForm;
import com.community.entity.vo.UserVO;
import com.community.service.ApplicationAccessService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationAccessServiceImpl implements ApplicationAccessService {
    @Override
    public void assertCanView(ApplicationForm form, UserVO user) {
        if (form == null) {
            throw new BusinessException(404, "申请不存在");
        }
        if (isAdmin(user)) {
            return;
        }
        boolean clerkOwns = hasRole(user, "CLERK") && (ApplicationStatus.PENDING.name().equals(form.getStatus())
                || (form.getHandlerId() != null && form.getHandlerId().equals(user.getId())));
        boolean reviewerCanSee = hasRole(user, "REVIEWER") && ApplicationStatus.PROCESSING.name().equals(form.getStatus());
        if (!clerkOwns && !reviewerCanSee) {
            throw new BusinessException(403, "无权查看该申请");
        }
    }

    @Override
    public void assertCanReview(ApplicationForm form, UserVO user) {
        if (form == null) {
            throw new BusinessException(404, "申请不存在");
        }
        if (!isAdmin(user) && !hasRole(user, "CLERK")) {
            throw new BusinessException(403, "仅社区受理员可初审");
        }
    }

    @Override
    public void assertCanApprove(ApplicationForm form, UserVO user) {
        if (form == null) {
            throw new BusinessException(404, "申请不存在");
        }
        if (!isAdmin(user) && !hasRole(user, "REVIEWER")) {
            throw new BusinessException(403, "仅街道审核员可复审");
        }
        if (form.getHandlerId() != null && form.getHandlerId().equals(user.getId()) && !isAdmin(user)) {
            throw new BusinessException(403, "初审人不能复审同一申请");
        }
    }

    private boolean isAdmin(UserVO user) {
        return hasRole(user, "ADMIN");
    }

    private boolean hasRole(UserVO user, String role) {
        return user != null && user.getRoles() != null && user.getRoles().contains(role);
    }
}
