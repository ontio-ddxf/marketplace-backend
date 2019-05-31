package com.ontology.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Helper;
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
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class DataTokenReceiver {
    @Autowired
    private ConfigParam configParam;

    @KafkaListener(topics = {"topic-dtoken"},groupId = "group-dtoken")
    public void receiveMessage(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        log.info("dToken合约：{}", Thread.currentThread().getName());
        try {
            String value = (String) record.value();
            JSONObject event = JSONObject.parseObject(value);

            JSONArray notifys = event.getJSONArray("Notify");

            for (int k = 0; k < notifys.size(); k++) {
                JSONObject notify = notifys.getJSONObject(k);
                if (configParam.CONTRACT_HASH_DTOKEN.equals(notify.getString("ContractAddress"))) {
                    // dToken合约地址匹配，解析结果
                    JSONArray states = (JSONArray) notify.get("States");
                    String method = new String(Helper.hexToBytes(states.getString(0)), "utf-8");
                    log.info(method);
                    String txHash = event.getString("TxHash");
                    if ("createTokenWithController".equals(method)) {
                        // createTokenWithController推送的事件
                        String dataId = new String(Helper.hexToBytes(states.getString(2)), "utf-8");
                        long tokenId = Long.parseLong(Helper.reverse(states.getString(4)), 16);
                        // 根据dataId查找到data更新tokenId
                        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                        MatchQueryBuilder queryToken = QueryBuilders.matchQuery("dataId", dataId);
                        boolQuery.must(queryToken);
                        List<Map<String, Object>> dataList = ElasticsearchUtil.searchListData(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, boolQuery, null, null, null, null);
                        if (CollectionUtils.isEmpty(dataList)) {
                            return;
                        }
                        String id = (String) dataList.get(0).get("id");
                        Map<String, Object> addTokenId = new HashMap<>();
                        addTokenId.put("tokenId",tokenId);
                        ElasticsearchUtil.updateDataById(addTokenId,Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET,id);
                    }
                }
            }
            ack.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
