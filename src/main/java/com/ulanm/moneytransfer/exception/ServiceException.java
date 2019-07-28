package com.ulanm.moneytransfer.exception;

public class ServiceException extends Exception {

    private String exceptionMessage;

    private int statusCode;

    private String statusMessage;

    public ServiceException() {
        statusCode = 500;
        statusMessage = "Unknown Server Error.";
    }

    public ServiceException(String message) {
        this();
        exceptionMessage = message;
    }

    public ServiceException withStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ServiceException withStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
