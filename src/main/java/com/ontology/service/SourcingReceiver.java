package com.ontology.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Helper;
import com.ontology.entity.TxCallback;
import com.ontology.mapper.TxCallbackMapper;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import com.ontology.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SourcingReceiver {
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private TxCallbackMapper txCallbackMapper;

    @KafkaListener(topics = {Constant.KAFKA_TOPIC_SOURCING},groupId = Constant.KAFKA_GROUP_SOURCING)
    public void receiveMessage(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        log.info("sourcing解析：{}", Thread.currentThread().getName());
        try {
            String value = (String) record.value();
            JSONObject event = JSONObject.parseObject(value);

            JSONArray notifys = event.getJSONArray("Notify");
            Integer height = event.getInteger("height");
            String sourceHash = event.getString("TxHash");
            Integer state = event.getInteger("State");

            for (int k = 0; k < notifys.size(); k++) {
                JSONObject notify = notifys.getJSONObject(k);
                if (configParam.CONTRACT_HASH_SOURCING.equals(notify.getString("ContractAddress"))) {
                    // 智能合约地址匹配，解析结果
                    Object statesObj = notify.get("States");

                    JSONArray states = (JSONArray) statesObj;
                    String method = new String(Helper.hexToBytes(states.getString(0)));
                    log.info(method);

                    if ("putRecord".equals(method)) {
                        // put推送的事件
                        String key = new String(Helper.hexToBytes(states.getString(1)), "utf-8");
                        String valueStr = new String(Helper.hexToBytes(states.getString(2)), "utf-8");

                        HashMap<String, Object> map = JSONObject.parseObject(valueStr, HashMap.class);
                        map.put("key", key);
                        map.put("txHash", sourceHash);
                        map.put("height", height);
                        map.put("value", valueStr);
                        ElasticsearchUtil.addData(map, Constant.ES_INDEX_SOURCE, Constant.ES_TYPE_SOURCE);

                        // 保存交易状态
                        TxCallback txCallback = new TxCallback();
                        txCallback.setSourceHash(sourceHash);
                        txCallback = txCallbackMapper.selectOne(txCallback);
                        if (txCallback != null) {
                            txCallback.setSourceOnchainState(state);
                            txCallbackMapper.updateByPrimaryKeySelective(txCallback);
                        }
                    }
                }
                ack.acknowledge();
            }
        } catch (Exception e) {
            log.error("catch exception:",e);
        }
    }
}