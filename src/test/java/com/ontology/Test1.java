package com.ontology;

import com.github.ontio.common.Helper;
import com.ontology.utils.Base64ConvertUtil;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Test1 {

    @Autowired
    private SDKUtil sdkUtil;


    @Test
    public void testBlockHeight() throws Exception {
        int blockHeight = sdkUtil.getBlockHeight();
        log.info("{}",blockHeight);
    }

    @Test
    public void testHex() throws Exception {
        String s = "0f774d321616d223";
        byte[] bytes = Helper.hexToBytes(s);
        log.info("{}",bytes);
        String result = new String(bytes,"utf-8");
        log.info("{}",result);
//        Base64ConvertUtil.decode();
    }

    @Test
    public void testBase64() throws Exception {
        String s = "shanghai";
        String encode = Base64ConvertUtil.encode(s);
        log.info("encode:{}",encode);
    }

    @Test
    public void searchJson() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("dataId","qwert");
        map.put("name","suibianyige");
        map.put("keyword","关键字");
    }
}
