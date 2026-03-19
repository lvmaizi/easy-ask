package com.lvmaizi.easy.ask.service.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientException extends RuntimeException {

    private String code;

    private String msg;

    public ClientException() {
    }

    public ClientException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ClientException(String message) {
        super(message);
        this.msg = message;
    }

    public ClientException(String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
    }

    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
