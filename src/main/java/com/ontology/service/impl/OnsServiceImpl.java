package com.ontology.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.ontology.entity.Ons;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.OnsMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.service.OnsService;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OnsServiceImpl implements OnsService {

    @Autowired
    private OnsMapper onsMapper;
    @Autowired
    private SDKUtil sdkUtil;
    @Autowired
    private SecureConfig secureConfig;
    @Autowired
    private ConfigParam configParam;


    @Override
    public Boolean registerOns(String action, String ontid, String domain) {
        Ons ons = onsMapper.findByOntid(ontid);
        if (ons == null) {
            ons = new Ons();
            ons.setOntid(ontid);
            ons.setDomain(domain);
            ons.setCreateTime(new Date());
            ons.setUpdateTime(new Date());
            ons.setState(0);
            onsMapper.insert(ons);
            Map<String,Object> arg0 = new HashMap<>();
            arg0.put("name","fulldomain");
            arg0.put("value","String:"+domain);
            Map<String,Object> arg1 = new HashMap<>();
            log.info("ontid:{}",ontid);
            arg1.put("name","registerdid");
            arg1.put("value","String:"+ontid);
            Map<String,Object> arg2 = new HashMap<>();
            arg2.put("name","idx");
            arg2.put("value",1);
            Map<String,Object> arg3 = new HashMap<>();
            arg3.put("name","validto");
            arg3.put("value",-1);
            List<Map<String,Object>> argList = new ArrayList<>();
            argList.add(arg0);
            argList.add(arg1);
            argList.add(arg2);
            argList.add(arg3);
            String params = Helper.getParams(ontid, configParam.CONTRACT_HASH_ONS, "registerDomain", argList, configParam.PAYER_ADDRESS);
            try {
                Object jsonObject = sdkUtil.invokeContract(params, secureConfig.getPayer(), false);
                log.info("register:{}",jsonObject);
                return Boolean.TRUE;
            } catch (Exception e) {
                log.info("error.............{}",e);
                e.printStackTrace();
            }
        } else {
//            Long allowTime = ons.getUpdateTime().getTime() + (10 * 3600 * 1000);
//            if (0 == ons.getState() && new Date().after(new Date(allowTime))) {
//                ons.setUpdateTime(new Date());
//                ons.setDomain(domain);
//                onsMapper.updateByPrimaryKeySelective(ons);
//                return Boolean.TRUE;
            }
//        }
        log.info("already exist");
        return Boolean.FALSE;
    }

    @Override
    public String loginOns(String action, String ontid) {
        Ons ons = onsMapper.findByOntid(ontid);
        if (ons == null) {
            log.info("null");
            return null;
        } else {
            String domain = ons.getDomain();
            log.info("domain:{}",domain);
            Map<String,Object> arg = new HashMap<>();
            arg.put("name","fulldomain");
            arg.put("value","String:"+domain);
            List<Map<String,Object>> argList = new ArrayList<>();
            argList.add(arg);
            String params = Helper.getParams(ontid, configParam.CONTRACT_HASH_ONS, "ownerOf", argList, configParam.PAYER_ADDRESS);
            String ownerOntid;
            try {
                JSONObject jsonObject = (JSONObject) sdkUtil.invokeContract(params, secureConfig.getPayer(), true);
                log.info("login result:{}",jsonObject);
                String owner = jsonObject.getString("Result");
                ownerOntid  = new String(com.github.ontio.common.Helper.hexToBytes(owner));
            } catch (Exception e) {
                log.info("error.................{}",e);
                e.printStackTrace();
                throw new OntIdException(e.getMessage());
            }
            if (ontid.equals(ownerOntid)) {
                return domain;
            }
            return null;
        }
    }

}
