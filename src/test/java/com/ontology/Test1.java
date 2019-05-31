package com.ontology;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.ontology.exception.OntIdException;
import com.ontology.utils.Base64ConvertUtil;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

//    @Test
//    public void ownerOf() throws Exception {
//        String domain = "on.ont";
//        String ontid = "did:ont:AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm";
//        Map<String,Object> arg = new HashMap<>();
//        arg.put("name","fulldomain");
//        arg.put("value","String:"+domain);
//        List<Map<String,Object>> argList = new ArrayList<>();
//        argList.add(arg);
//        String params = com.ontology.utils.Helper.getParams(ontid, "fb12993d6f13a2ec911f3bbfe534be90e4deeca4", "ownerOf", argList, "AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm");
//        String ownerOntid = null;
//        try {
//            JSONObject jsonObject = (JSONObject) sdkUtil.invokeContract(params, sdkUtil.getAccount("{\"address\":\"AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm\",\"salt\":\"TzXgOZ3yA4K+qWVHlvNzCA==\",\"label\":\"238d8ae4\",\"publicKey\":\"02f7dbbb5164563540e2e5aab11237f40b893e3bbb1844100b6f2986460731319f\",\"type\":\"I\",\"parameters\":{\"curve\":\"P-256\"},\"scrypt\":{\"dkLen\":64,\"n\":16384,\"p\":8,\"r\":8},\"key\":\"I4jw1Q3m61DM+0YT0GIMNiHQN3sY5/NjDpV6feH8YHkgb7+Fmy7E8JUAJ2lOPOWO\",\"algorithm\":\"ECDSA\"}","12345678"), true);
//            String owner = jsonObject.getString("Result");
//            ownerOntid  = new String(com.github.ontio.common.Helper.hexToBytes(owner));
//            log.info("{}",ownerOntid);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new OntIdException(e.getMessage());
//        }
//        if (ontid.equals(ownerOntid)) {
//            log.info("success");
//        } else {
//            log.info("failed");
//        }
//    }

    @Test
    public void payer() throws Exception {
        Account account = sdkUtil.getAccount("{\"address\":\"AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm\",\"salt\":\"TzXgOZ3yA4K+qWVHlvNzCA==\",\"label\":\"238d8ae4\",\"publicKey\":\"02f7dbbb5164563540e2e5aab11237f40b893e3bbb1844100b6f2986460731319f\",\"type\":\"I\",\"parameters\":{\"curve\":\"P-256\"},\"scrypt\":{\"dkLen\":64,\"n\":16384,\"p\":8,\"r\":8},\"key\":\"I4jw1Q3m61DM+0YT0GIMNiHQN3sY5/NjDpV6feH8YHkgb7+Fmy7E8JUAJ2lOPOWO\",\"algorithm\":\"ECDSA\"}", "12345678");
        String wif = account.exportWif();
        log.info("wif:{}",wif);
        String encode = Base64ConvertUtil.encode(wif);
        log.info("encode:{}",encode);
    }
}
