package com.stkj.cashier.setting.model;


public class FoodSyncCallback {

    private int totalCount;
    private int totalPage;
    private int pageIndex;
    private int pageSize;
    private String syncNo;


    public FoodSyncCallback() {
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