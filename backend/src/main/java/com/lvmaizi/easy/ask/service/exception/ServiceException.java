package com.lvmaizi.easy.ask.service.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -2104786983371575975L;

    private String code;

    private String msg;

    public ServiceException() {
    }

    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ServiceException(String message) {
        super(message);
        this.msg = message;
    }

    public ServiceException(String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}

