package com.stkj.cbgfacepass.model;

import mcv.facepass.types.FacePassImage;

/**
 * 人脸识别结果
 */
public class CBGFacePassRecognizeResult {

    private String faceToken;
    private String fullName;
    private boolean isFacePassSuccess;
    private int recognitionState;
    private FacePassImage image;


    public CBGFacePassRecognizeResult(String faceToken, boolean isFacePassSuccess, int recognitionState, FacePassImage image) {
        this.faceToken = faceToken;
        this.fullName = fullName;
        this.isFacePassSuccess = isFacePassSuccess;
        this.recognitionState = recognitionState;
        this.image = image;
    }

    public CBGFacePassRecognizeResult(String faceToken, boolean isFacePassSuccess, int recognitionState) {
        this.faceToken = faceToken;
        this.isFacePassSuccess = isFacePassSuccess;
        this.recognitionState = recognitionState;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

    public boolean isFacePassSuccess() {
        return isFacePassSuccess;
    }

    public void setFacePassSuccess(boolean facePassSuccess) {
        isFacePassSuccess = facePassSuccess;
    }

    public int getRecognitionState() {
        return recognitionState;
    }

    public void setRecognitionState(int recognitionState) {
        this.recognitionState = recognitionState;
    }

    public FacePassImage getImage() {
        return image;
    }

    public void setImage(FacePassImage image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "CBGFacePassRecognizeResult{" +
                "faceToken='" + faceToken + '\'' +
                ", isFacePassSuccess=" + isFacePassSuccess +
                ", recognitionState=" + recognitionState +
                '}';
    }
}
