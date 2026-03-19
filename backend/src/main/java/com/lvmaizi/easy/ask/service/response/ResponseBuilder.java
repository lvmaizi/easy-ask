package com.lvmaizi.easy.ask.service.response;


public class ResponseBuilder {

    public static <T> BaseResponse<T> ok(T data) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setData(data);
        return baseResponse;
    }

    public static <T> BaseResponse<T> fail(String message) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setCode("500");
        baseResponse.setMessage(message);
        return baseResponse;
    }

    public static <T> BaseResponse<T> fail() {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setCode("500");
        baseResponse.setMessage("系统异常");
        return baseResponse;
    }

    public static <T> BaseResponse<T> invitationLogin() {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setCode("302");
        baseResponse.setMessage("Invitation code authentication failed");
        return baseResponse;
    }

    public static <T> BaseResponse<T> login() {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setCode("303");
        baseResponse.setMessage("Verification code authentication failed");
        return baseResponse;
    }
}
