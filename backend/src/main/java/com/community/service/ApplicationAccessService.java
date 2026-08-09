package com.community.service;

import com.community.entity.po.ApplicationForm;
import com.community.entity.vo.UserVO;

public interface ApplicationAccessService {
    void assertCanView(ApplicationForm form, UserVO user);

    void assertCanReview(ApplicationForm form, UserVO user);

    void assertCanApprove(ApplicationForm form, UserVO user);
}
