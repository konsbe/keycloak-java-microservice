package com.scittech.city.keycloakmicroservice.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse {
    private final boolean success;
    private final Map<String, Object> data;
    int status;
    String error;
    String message;
    String path;

    private ApiResponse(boolean success, Map<String, Object> data) {
        this.success = success;
        this.data = data;
    }


    public static ApiResponse success(Map<String, Object> data) {
        return new ApiResponse(true, data);
    }

    public static ApiResponse error(int status, String error, String message, String path) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("timestamp", "2023-09-11T13:05:26.134+00:00");
        errorData.put("status", status);
        errorData.put("error", error);
        errorData.put("message", message);
        errorData.put("path", path);

        return new ApiResponse(false, errorData);
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<String, Object> getData() {
        return data;
    }


}
