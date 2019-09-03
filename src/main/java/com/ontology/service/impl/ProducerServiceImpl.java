package com.ontology.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ontology.service.ProducerService;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Constant;
import com.ontology.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerServiceImpl implements ProducerService {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private ConfigParam configParam;

    @Override
    public void parseAndSendOne(String value) {
        JSONObject data = JSONObject.parseObject(value);
        JSONObject events = JSONObject.parseObject(data.getString("events"));
        if (Helper.isEmptyOrNull(events)) {
            return;
        }
        JSONArray notifys = events.getJSONArray("Notify");

        for (int k = 0; k < notifys.size(); k++) {
            JSONObject notify = notifys.getJSONObject(k);
            String contractAddress = notify.getString("ContractAddress");
            if (configParam.CONTRACT_HASH_SOURCING.equals(contractAddress)) {
                kafkaTemplate.send(Constant.KAFKA_TOPIC_SOURCING, events.toJSONString());
                break;
            } else if (configParam.CONTRACT_HASH_MP.equals(contractAddress)) {
                kafkaTemplate.send(Constant.KAFKA_TOPIC_MP, events.toJSONString());
                break;
            }
        }
    }

}
