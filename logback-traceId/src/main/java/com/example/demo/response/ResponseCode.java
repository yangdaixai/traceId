package com.example.demo.response;

public enum ResponseCode {
    /**
     * 200
     */
    OK(200, "OK"),
    /**
     * 400
     */
    BAD_REQUEST(400, "Bad Request"),
    /**
     * 403
     */
    NOT_AUTHORIZED(403, "Forbidden"),
    /**
     * 404
     */
    NOT_FOUND(404, "Not found"),
    /**
     * 500
     */
    INTERNAL_ERROR(500, "Internal Error");

    private final Integer code;
    private final String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
