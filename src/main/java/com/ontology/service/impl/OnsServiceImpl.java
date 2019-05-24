package com.ontology.service.impl;


import com.ontology.entity.Ons;
import com.ontology.mapper.OnsMapper;
import com.ontology.service.OnsService;
import com.ontology.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OnsServiceImpl implements OnsService {

    @Autowired
    private OnsMapper onsMapper;

    @Override
    public Boolean findByOntid(String action, String ontid, String domain) {
        Ons ons = onsMapper.findByOntid(ontid);
        if (ons == null) {
            ons = new Ons();
            ons.setOntid(ontid);
            ons.setDomain(domain);
            ons.setCreateTime(new Date());
            ons.setUpdateTime(new Date());
            ons.setState(0);
            onsMapper.insert(ons);
            return Boolean.TRUE;
        } else {
            Long allowTime = ons.getUpdateTime().getTime() + (10 * 3600 * 1000);
            if (0 == ons.getState() && new Date().after(new Date(allowTime))) {
                ons.setUpdateTime(new Date());
                ons.setDomain(domain);
                onsMapper.updateByPrimaryKeySelective(ons);
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public String findOns(String action, String ontid) {
        Ons ons = onsMapper.findByOntid(ontid);
        if (ons == null) {
            return null;
        } else {
//            if (0 == ons.getState()) {
//                return null;
//            }
            String domain = ons.getDomain();
            // todo 查询域名对应
            return domain;
        }
    }
}
