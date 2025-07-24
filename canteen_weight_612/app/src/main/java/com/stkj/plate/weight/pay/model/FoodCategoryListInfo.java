package com.stkj.plate.weight.pay.model;


import java.util.List;

public class FoodCategoryListInfo {

    private List<CategoryBean> records;
    private int totalCount;
    private int totalPage;
    private int pageIndex;
    private int pageSize;


    public FoodCategoryListInfo() {
    }


    public List<CategoryBean> getRecords() {
        return records;
    }

    public void setRecords(List<CategoryBean> records) {
        this.records = records;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
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