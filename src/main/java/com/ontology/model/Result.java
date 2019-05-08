package com.ontology.model;

import lombok.Data;

@Data
public class Result {
    public String action;
    public int code;
    public String msg;
    public Object result;
    public String version;

    public Result() {
    }

    public Result(String action, int code, String msg, Object result) {
        this.action = action;
        this.code = code;
        this.msg = msg;
        this.result = result;
        this.version = "v1";
    }

}
