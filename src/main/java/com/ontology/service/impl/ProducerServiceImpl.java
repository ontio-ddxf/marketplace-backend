package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ontology.entity.TxCallback;
import com.ontology.mapper.TxCallbackMapper;
import com.ontology.service.ProducerService;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Constant;
import com.ontology.utils.Helper;
import com.ontology.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ProducerServiceImpl implements ProducerService {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private TxCallbackMapper txCallbackMapper;
    @Override
    public void parseAndSendOne(String value) {
        JSONObject data = JSONObject.parseObject(value);
        JSONObject events = JSONObject.parseObject(data.getString("events"));
        Integer height = data.getInteger("height");
        events.put("height", height);
        if (Helper.isEmptyOrNull(events)) {
            return;
        }
        JSONArray notifys = events.getJSONArray("Notify");
        String txHash = events.getString("TxHash");
        Integer state = events.getInteger("State");
        boolean findContract = false;

        for (int k = 0; k < notifys.size(); k++) {
            JSONObject notify = notifys.getJSONObject(k);
            String contractAddress = notify.getString("ContractAddress");
            if (configParam.CONTRACT_HASH_SOURCING.equals(contractAddress)) {
                findContract = true;
                kafkaTemplate.send(Constant.KAFKA_TOPIC_SOURCING, events.toJSONString());
                break;
            } else if (configParam.CONTRACT_HASH_MEDATA.equals(contractAddress)) {
                findContract = true;
                kafkaTemplate.send(Constant.KAFKA_TOPIC_MP, events.toJSONString());
                break;
            }
        }
        // 未找到需要解析的合约，通用修改状态
        if (!findContract) {
            TxCallback txCallback = new TxCallback();
            txCallback.setTxHash(txHash);
            txCallback = txCallbackMapper.selectOne(txCallback);
            if (txCallback != null) {
                txCallback.setTxOnchainState(state);
                txCallbackMapper.updateByPrimaryKeySelective(txCallback);
            }
        }
    }

}
