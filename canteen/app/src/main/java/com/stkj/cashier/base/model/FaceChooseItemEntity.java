package com.stkj.cashier.base.model;

public class FaceChooseItemEntity {

    private String username;
    private String phone;
    private String headUrl;
    private String faceToken;
    private boolean checked;


    public FaceChooseItemEntity(String username, String phone, String headUrl, String faceToken, boolean checked) {
        this.username = username;
        this.phone = phone;
        this.headUrl = headUrl;
        this.faceToken = faceToken;
        this.checked = checked;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

}
