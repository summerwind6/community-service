package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Guide;
import com.community.entity.vo.PageVO;

import java.util.List;

public interface GuideService extends IService<Guide> {

    PageVO<Guide> getGuideList(PageDTO pageDTO, String category);

    List<Guide> getPublicGuides();

    Guide getGuideById(Long id);

    Guide addGuide(Guide guide);

    Guide updateGuide(Guide guide);

    void removeGuide(Long id);
}
