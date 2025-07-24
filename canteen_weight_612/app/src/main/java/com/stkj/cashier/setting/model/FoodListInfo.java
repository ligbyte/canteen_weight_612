package com.stkj.cashier.setting.model;

import java.util.List;

public class FoodListInfo {

    private List<FoodBean> Results;
    private int totalCount;
    private int totalPage;
    private int pageIndex;
    private int pageSize;
    private String syncNo;


    public FoodListInfo() {
    }

    public List<FoodBean> getResults() {
        return Results;
    }

    public void setResults(List<FoodBean> results) {
        Results = results;
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

    public String getSyncNo() {
        return syncNo;
    }

    public void setSyncNo(String syncNo) {
        this.syncNo = syncNo;
    }
}