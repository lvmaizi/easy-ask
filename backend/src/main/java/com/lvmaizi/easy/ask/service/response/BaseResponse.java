package com.lvmaizi.easy.ask.service.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {

    private String code =  "200";

    private String message = "success";

    private T data;
}
