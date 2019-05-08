package com.ontology;

import com.github.ontio.common.Helper;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

@Slf4j
public class Test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "7465737431";
        byte[] bytes = Helper.hexToBytes(s);
        log.info("{}",bytes);
        String result = new String(bytes,"utf-8");
        log.info("{}",result);
        String a = new String(Helper.hexToBytes("3664666335316561306436353431383862306663383261663335613263366139"),"utf-8");
        log.info("{}",a);
    }
}
