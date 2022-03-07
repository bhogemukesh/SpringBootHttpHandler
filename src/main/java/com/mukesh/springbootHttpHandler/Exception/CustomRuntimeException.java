package com.mukesh.springbootHttpHandler.Exception;

/**
 * @author Mukesh Bhoge
 **/
public class CustomRuntimeException extends RuntimeException {

    private String serviceName;
    private RuntimeException runtimeException;
    private Exception exception;
    private String message;

    public CustomRuntimeException(String serviceName, RuntimeException runtimeException){
        this.serviceName = serviceName;
        this.runtimeException = runtimeException;
    }

    public CustomRuntimeException(String serviceName, Exception exception){
        this.serviceName = serviceName;
        this.exception = exception;
    }

    public CustomRuntimeException(String serviceName, String message) {
        this.serviceName = serviceName;
        this.message =  message;
    }
}
