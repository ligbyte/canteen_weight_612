package com.stkj.plate.weight.pay.model;

import java.util.List;

/**
 * 历史订单
 */
public class FoodConsumePageResponse {

    private List<FoodConsumeBean> records;
    private int totalCount;
    private int pages;
    private int pageIndex;
    private int pageSize;

    public FoodConsumePageResponse() {
    }

    public List<FoodConsumeBean> getRecords() {
        return records;
    }

    public void setRecords(List<FoodConsumeBean> records) {
        this.records = records;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}