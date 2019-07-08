package com.ontology.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.ontology.entity.Order;
import com.ontology.entity.OrderData;
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

import java.util.*;

@Component
@Slf4j
public class MarketplaceReceiver {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDataMapper orderDataMapper;
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
                    String method = new String(Helper.hexToBytes(states.getString(0)), "utf-8");
                    log.info(method);

                    if ("makeOrder".equals(method)) {
                        // makeOrder推送的事件
                        String orderId = states.getString(2);
                        long tokenId = Long.parseLong(Helper.reverse(states.getString(4)), 16);
                        String price = String.valueOf(Long.parseLong(Helper.reverse(states.getString(6)), 16));
                        JSONArray objects = JSONArray.parseArray(states.getString(8));
                        List<String> ojList = new ArrayList<>();
                        for (Object o : objects) {
                            String oj = String.format(Constant.ONTID_PREFIX,Address.parse((String) o).toBase58());
                            ojList.add(oj);
                        }
                        log.info("tokenId:{}",tokenId);
                        log.info("price:{}",price);
                        log.info("ojList:{}",JSON.toJSONString(ojList));
                        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                        MatchQueryBuilder queryToken = QueryBuilders.matchQuery("tokenId", tokenId);
//                        MatchQueryBuilder queryAmount = QueryBuilders.matchQuery("amount", amount);
                        MatchQueryBuilder queryPrice = QueryBuilders.matchQuery("price", price);
                        MatchQueryBuilder queryJudger = QueryBuilders.matchQuery("judger", JSON.toJSONString(ojList));
                        MatchQueryBuilder queryOrderId = QueryBuilders.matchQuery("orderId.keyword", "");
                        MatchQueryBuilder queryAuthId = QueryBuilders.matchQuery("authId.keyword", "");
                        boolQuery.must(queryToken);
                        boolQuery.must(queryPrice);
                        boolQuery.must(queryJudger);
                        boolQuery.must(queryOrderId);
                        boolQuery.must(queryAuthId);
                        List<Map<String, Object>> list = ElasticsearchUtil.searchListData(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, boolQuery, null, null, null, null);
                        Map<String, Object> order = list.get(0);
                        String id = (String) order.get("id");
                        order.put("orderId",orderId);
                        order.put("state","1");
                        ElasticsearchUtil.updateDataById(order,Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER,id);
                    } else if ("takeOrder".equals(method)) {
                        // takeOrder推送的事件
//                        String orderId = states.getString(2);
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
