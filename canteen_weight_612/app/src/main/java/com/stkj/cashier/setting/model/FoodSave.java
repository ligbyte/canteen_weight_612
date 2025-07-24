package com.stkj.cashier.setting.model;


public class FoodSave {

    private int totalCount;
    private int totalPage;
    private int pageIndex;
    private int pageSize;
    private String syncNo;
    private String msg;
    private FoodBean Data;


    public FoodSave() {
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

    public FoodBean getData() {
        return Data;
    }

    public void setData(FoodBean data) {
        Data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}