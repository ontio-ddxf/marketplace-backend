package com.ontology;

import com.github.ontio.common.Helper;
import com.ontology.utils.Base64ConvertUtil;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Test {

    @Autowired
    private SDKUtil sdkUtil;

    public static void main(String[] args) throws Exception {
//        String s = "7465737431";
//        byte[] bytes = Helper.hexToBytes(s);
//        log.info("{}",bytes);
//        String result = new String(bytes,"utf-8");
//        log.info("{}",result);
        String a = new String(Helper.hexToBytes("6232356a6147467062673d3d23"),"utf-8");
////        log.info("{}",a);0f774d321616d223
//        SDKUtil sdkUtil = new SDKUtil();
//        int blockHeight = sdkUtil.getBlockHeight();
//        log.info("{}",blockHeight);
        log.info("encode:{}",a);
        String decode = Base64ConvertUtil.decode("b25jaGFpbg==");
        log.info("decode:{}",decode);
    }

    @org.junit.Test
    public void testBlockHeight() throws Exception {
        int blockHeight = sdkUtil.getBlockHeight();
        log.info("{}",blockHeight);
    }

    @org.junit.Test
    public void testHex() throws Exception {
        String s = "0f774d321616d223";
        byte[] bytes = Helper.hexToBytes(s);
        log.info("{}",bytes);
        String result = new String(bytes,"utf-8");
        log.info("{}",result);
//        Base64ConvertUtil.decode();
    }

    @org.junit.Test
    public void testBase64() throws Exception {
        String s = "shanghai";
        String encode = Base64ConvertUtil.encode(s);
        log.info("encode:{}",encode);
//        String hexString = Helper.toHexString(encode.getBytes());
//        log.info("hexString:{}",hexString);
//        String decode = Base64ConvertUtil.decode(encode);
//        log.info("decode:{}",decode);
    }
}
