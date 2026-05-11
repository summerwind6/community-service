package com.community.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {

    private List<T> records;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Integer pages;

    public static <T> PageVO<T> of(List<T> records, long total, Integer pageNum, Integer pageSize) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setRecords(records);
        pageVO.setTotal(total);
        pageVO.setPageNum(pageNum);
        pageVO.setPageSize(pageSize);
        pageVO.setPages((int) Math.ceil((double) total / pageSize));
        return pageVO;
    }
}
