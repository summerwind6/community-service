package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;
import com.community.entity.vo.PageVO;

import java.util.List;

public interface ApplicationFormService extends IService<ApplicationForm> {

    ApplicationForm submitApplication(ApplicationForm form);

    ApplicationForm getApplicationDetail(Long id);

    PageVO<ApplicationForm> getApplicationList(PageDTO pageDTO, String status);

    /** 初审：社区受理员  PENDING → PROCESSING 或 REJECTED */
    void reviewApplication(Long id, String result, String opinion, Long auditorId, String auditorName);

    /** 复审：街道审核员  PROCESSING → APPROVED 或 REJECTED */
    void approveApplication(Long id, String result, String opinion, Long auditorId, String auditorName);

    /** 查询申请单的审核记录 */
    List<AuditRecord> getAuditRecords(Long applicationId);

    /** 手机号查询申请进度 */
    List<ApplicationForm> getApplicationProgressByPhone(String phone);

    /** 查询申请是否已评价 */
    Boolean isApplicationEvaluated(Long applicationId);

    void updateStatus(Long id, String status);
}
