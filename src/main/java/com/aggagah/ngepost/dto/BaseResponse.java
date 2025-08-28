package com.aggagah.ngepost.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    private String status;
    private String message;
    private Object data;

    public static BaseResponse success(String message, Object data) {
        return new BaseResponse("success", message, data);
    }

    public static BaseResponse error(String message, Object data) {
        return new BaseResponse("error", message, data);
    }
}
