package com.yudu.rest.exception;

/**
 * @author rjm
 */
public class RequestBuilderException extends RuntimeException
{
    public RequestBuilderException(String message) {
        super(message);
    }

    public RequestBuilderException(Throwable e) {
        super(e);
    }

    public RequestBuilderException(String message, Throwable e) {
        super(message, e);
    }
}
