package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ResidentInfo;
import com.community.entity.vo.PageVO;

public interface ResidentInfoService extends IService<ResidentInfo> {

    PageVO<ResidentInfo> getResidentList(PageDTO pageDTO);

    ResidentInfo getResidentById(Long id);

    ResidentInfo getResidentByPhone(String phone);

    ResidentInfo addResident(ResidentInfo resident);

    ResidentInfo updateResident(ResidentInfo resident);
}
