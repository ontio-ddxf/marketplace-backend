package com.ontology.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ontology.entity.Order;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.OrderMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.service.ToolsService;
import com.ontology.utils.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ToolsServiceImpl implements ToolsService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SecureConfig secureConfig;

    @Override
    public PageInfo<Order> queryList(String action, Integer provider, String ontid, int pageNum, int pageSize) {
        String queryType = null;
        if (0==provider) {
            // 需求方
            queryType = "buyer_ontid";
        } else if (1==provider) {
            // 提供方
            queryType = "seller_ontid";
        } else {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.getOrderList(queryType,ontid);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        return pageInfo;
    }

    @Override
    public List<String> getData(String action, String orderId, String ontid) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        if (!order.getBuyerOntid().equals(ontid)) {
            throw new OntIdException(action, ErrorInfo.NO_PERMISSION.descCN(), ErrorInfo.NO_PERMISSION.descEN(), ErrorInfo.NO_PERMISSION.code());
        }
        if (StringUtils.isEmpty(order.getRecvMsgTx())) {
            throw new OntIdException(action, ErrorInfo.NO_PERMISSION.descCN(), ErrorInfo.NO_PERMISSION.descEN(), ErrorInfo.NO_PERMISSION.code());
        }
        String event = order.getSellEvent();
        if (StringUtils.isEmpty(event)) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        JSONObject jsonObject = JSONObject.parseObject(event);
        JSONArray notify = jsonObject.getJSONArray("Notify");
        String data = "";
        for (int i = 0;i<notify.size();i++) {
            JSONObject obj = notify.getJSONObject(i);
            if (secureConfig.getContractHash().equals(obj.getString("ContractAddress"))) {
                data = obj.getString("States");
                try {
                    data = new String(com.github.ontio.common.Helper.hexToBytes(data),"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        String[] dataArray = data.split("#");
        List<String> dataList = Arrays.asList(dataArray);
        return dataList;
    }

}
