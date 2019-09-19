package com.ontology.exception;

/**
 * @author lijie
 * @version 1.0
 */
public class CallbackException extends RuntimeException {

    private long errCode;

    private String errDesEN;

    private String errDesCN;

    public CallbackException(long errCode, String errDesEN, String errDesCN, String version) {
        super(errDesEN);
        this.errCode = errCode;
        this.errDesEN = errDesEN;
        this.errDesCN = errDesCN;
    }

    public CallbackException(long errCode, String errDesEN, String errDesCN) {
        super(errDesEN);
        this.errCode = errCode;
        this.errDesEN = errDesEN;
        this.errDesCN = errDesCN;
    }


    public long getErrCode() {
        return this.errCode;
    }

    public String getErrDesEN() {
        return this.errDesEN;
    }

    public String getErrDesCN() {
        return this.errDesCN;
    }


    @Override
    public String toString() {
        return "CallbackException{" +
                "errCode=" + errCode +
                ", errDesEN='" + errDesEN + '\'' +
                ", errDesCN='" + errDesCN + '\'' +
                '}';
    }
}
