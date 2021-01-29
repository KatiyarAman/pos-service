package com.ris.inventory.pos.model;

import java.io.Serializable;
import java.util.Date;

public class Error implements Serializable {

    public int errorCode;

    public String errorMessage = "";

    public Date timeStamp;

    public static Error setErrorResponse(int errorCode, String errorMessage) {
        Error error = new Error();
        error.setErrorCode(errorCode);
        error.setErrorMessage(errorMessage);
        error.setTimeStamp(new Date());
        return error;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
