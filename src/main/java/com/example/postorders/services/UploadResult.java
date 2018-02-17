package com.example.postorders.services;

import lombok.Value;

@Value
public class UploadResult {

    private uploadStatus status;
    private String errorMessage;

    public enum uploadStatus{
        SUCCESS,FAILURE;
    }
}
