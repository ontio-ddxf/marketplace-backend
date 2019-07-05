package com.ontology.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.ontology.mapper.OrderDataMapper;
import com.ontology.mapper.OrderMapper;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class Marketplace2Receiver {
    @Autowired
    private ConfigParam configParam;

    @KafkaListener(topics = {"topic-marketplace"},groupId = "group-marketplace")
    public void receiveMessage(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        log.info("marketplace解析：{}", Thread.currentThread().getName());
        try {
            String value = (String) record.value();
            JSONObject event = JSONObject.parseObject(value);

            JSONArray notifys = event.getJSONArray("Notify");

            for (int k = 0; k < notifys.size(); k++) {
                JSONObject notify = notifys.getJSONObject(k);
                if (configParam.CONTRACT_HASH_MP.equals(notify.getString("ContractAddress"))) {
                    // 智能合约地址匹配，解析结果
                    Object statesObj = notify.get("States");

                    JSONArray states = (JSONArray) statesObj;
                    String method = new String(Helper.hexToBytes(states.getString(0)));
                    log.info(method);

                    if ("authOrder".equals(method)) {
                        // makeOrder推送的事件
                        String authId = states.getString(2);
                        String dataId = new String(Helper.hexToBytes(states.getString(6)), "utf-8");
                        log.info("authId:{}",authId);
                        log.info("dataId:{}",dataId);

                        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                        MatchQueryBuilder querydata = QueryBuilders.matchQuery("dataId", dataId);
                        boolQuery.must(querydata);
                        List<Map<String, Object>> list = ElasticsearchUtil.searchListData(Constant.ES_INDEX_DATASET, Constant.ES_INDEX_DATASET, boolQuery, null, null, null, null);
                        Map<String, Object> order = list.get(0);
                        String id = (String) order.get("id");
                        order.put("authId",authId);
                        order.put("state","2");
                        ElasticsearchUtil.updateDataById(order,Constant.ES_INDEX_DATASET, Constant.ES_INDEX_DATASET,id);
                    } else if ("takeOrder".equals(method)) {
                        // takeOrder推送的事件，同时解析mintToken
                        String demanderAddress=null;
                        String dataId=null;
                        long tokenId = 0;
                        for (int m = 0; m < notifys.size(); m++) {
                            JSONObject dTokenNotify = notifys.getJSONObject(m);
                            if (configParam.CONTRACT_HASH_DTOKEN.equals(dTokenNotify.getString("ContractAddress"))) {
                                JSONArray dTokenStates = (JSONArray) dTokenNotify.get("States");
                                String dTokenMethod = new String(Helper.hexToBytes(dTokenStates.getString(0)));
                                log.info(dTokenMethod);
                                if ("mintToken".equals(dTokenMethod)) {
                                    demanderAddress = Address.parse(dTokenStates.getString(2)).toBase58();
                                    dataId = new String(Helper.hexToBytes(dTokenStates.getString(4)));
                                    tokenId = (Long.parseLong(Helper.reverse(dTokenStates.getString(6)), 16));
                                    long endTokenId = (Long.parseLong(Helper.reverse(dTokenStates.getString(8)), 16));
                                    long amount = (Long.parseLong(Helper.reverse(dTokenStates.getString(14)), 16));
                                    break;
                                }
                            }
                        }
                        String orderId = states.getString(2);
                        String authId = new String(Helper.hexToBytes(states.getString(4)));

                        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                        MatchQueryBuilder queryAuthId = QueryBuilders.matchQuery("authId", authId);
                        MatchQueryBuilder queryAddr = QueryBuilders.matchQuery("demanderAddress", demanderAddress);
                        MatchQueryBuilder queryDataId = QueryBuilders.matchQuery("dataId", dataId);
                        MatchQueryBuilder queryOrderId = QueryBuilders.matchQuery("orderId.keyword", "");
                        boolQuery.must(queryAuthId);
                        boolQuery.must(queryAddr);
                        boolQuery.must(queryDataId);
                        boolQuery.must(queryOrderId);
                        List<Map<String, Object>> list = ElasticsearchUtil.searchListData(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, boolQuery, null, null, null, null);
                        Map<String, Object> order = list.get(0);
                        String id = (String) order.get("id");
                        order.put("orderId",orderId);
                        order.put("tokenId",tokenId);
                        order.put("state","2");
                        ElasticsearchUtil.updateDataById(order,Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER,id);

                    } else if ("confirm".equals(method)) {
                        // confirm推送的事件
                        String orderId = states.getString(2);

                        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                        MatchQueryBuilder queryOrder = QueryBuilders.matchQuery("orderId", orderId);
                        boolQuery.must(queryOrder);
                        List<Map<String, Object>> list = ElasticsearchUtil.searchListData(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, boolQuery, null, null, null, null);
                        Map<String, Object> order = list.get(0);
                        String id = (String) order.get("id");
                        order.put("state","3");
                        order.put("confirmTime",JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss").replace("\"", ""));
                        ElasticsearchUtil.updateDataById(order,Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER,id);
                    } else if ("applyArbitrage".equals(method)) {
                        // applyArbitrage推送的事件
                        String orderId = states.getString(2);

                        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                        MatchQueryBuilder queryOrder = QueryBuilders.matchQuery("orderId", orderId);
                        boolQuery.must(queryOrder);
                        List<Map<String, Object>> list = ElasticsearchUtil.searchListData(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, boolQuery, null, null, null, null);
                        Map<String, Object> order = list.get(0);
                        String id = (String) order.get("id");
                        order.put("state","4");
                        ElasticsearchUtil.updateDataById(order,Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER,id);

                    } else if ("arbitrage".equals(method)) {
                        // arbitrage推送的事件

                    }
                }
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("catch exception:",e);
        }
    }
}