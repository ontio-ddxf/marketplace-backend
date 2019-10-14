package com.ontology.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.ontology.entity.TxCallback;
import com.ontology.mapper.TxCallbackMapper;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Constant;
import com.ontology.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class MedataReceiver {
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private TxCallbackMapper txCallbackMapper;
    @Autowired
    private HttpClientUtils httpClientUtils;

    @KafkaListener(topics = {Constant.KAFKA_TOPIC_MP},groupId = Constant.KAFKA_GROUP_MP)
    public void receiveMessage(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        log.info("marketplace-auth解析：{}", Thread.currentThread().getName());
        try {
            String value = (String) record.value();
            JSONObject event = JSONObject.parseObject(value);

            JSONArray notifys = event.getJSONArray("Notify");
            String txHash = event.getString("TxHash");
            Integer state = event.getInteger("State");

            // 保存交易状态
            TxCallback txCallback = new TxCallback();
            txCallback.setTxHash(txHash);
            txCallback = txCallbackMapper.selectOne(txCallback);
            if (txCallback != null) {
                txCallback.setTxOnchainState(state);
            }

            for (int k = 0; k < notifys.size(); k++) {
                JSONObject notify = notifys.getJSONObject(k);
                if (configParam.CONTRACT_HASH_MEDATA.equals(notify.getString("ContractAddress"))) {
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

                        // 保存authId
                        if (txCallback != null) {
                            txCallback.setBusinessId(authId);
                        }

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
                        String authId = states.getString(4);

                        // 保存orderId
                        if (txCallback != null) {
//                            txCallback.setBusinessId(orderId+","+tokenId);
                            txCallback.setBusinessId(orderId);
                        }


                    } else if ("confirm".equals(method)) {
                        // confirm推送的事件

                    } else if ("applyArbitrage".equals(method)) {
                        // applyArbitrage推送的事件


                    } else if ("arbitrage".equals(method)) {
                        // arbitrage推送的事件

                    } else if ("cancelAuth".equals(method)) {
                        // cancelAuth推送的事件

                    }
                }
            }

            if (txCallback != null) {
                txCallbackMapper.updateByPrimaryKeySelective(txCallback);
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("catch exception:",e);
        }
    }
}