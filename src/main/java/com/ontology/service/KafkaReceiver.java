//package com.ontology.service;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.github.ontio.common.Address;
//import com.github.ontio.common.Helper;
//import com.ontology.utils.ConfigParam;
//import com.ontology.entity.Order;
//import com.ontology.entity.OrderData;
//import com.ontology.mapper.OrderDataMapper;
//import com.ontology.mapper.OrderMapper;
//import com.ontology.utils.Constant;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//@Slf4j
//public class KafkaReceiver {
//    @Autowired
//    private OrderMapper orderMapper;
//    @Autowired
//    private OrderDataMapper orderDataMapper;
//    @Autowired
//    private ConfigParam configParam;
//
//    @KafkaListener(topics = {"topic-ddxf"})
//    public void receiveMessage(ConsumerRecord<?, ?> record, Acknowledgment ack) {
//        log.info("ddxf入库：{}", Thread.currentThread().getName());
//        try {
//            String value = (String) record.value();
//            JSONObject event = JSONObject.parseObject(value);
//
//            JSONArray notifys = event.getJSONArray("Notify");
//
//            for (int k = 0; k < notifys.size(); k++) {
//                JSONObject notify = notifys.getJSONObject(k);
//                if (configParam.CONTRACT_HASH.equals(notify.getString("ContractAddress"))) {
//                    // 智能合约地址匹配，解析结果
//                    Object statesObj = notify.get("States");
//                    if (statesObj instanceof String) {
//                        // sendEncMessage方法推送的数据；忽略，最后和event一起存
//                        continue;
//                    }
//                    JSONArray states = (JSONArray) statesObj;
//                    String method = new String(com.github.ontio.common.Helper.hexToBytes(states.getString(0)), "utf-8");
//                    Order order = new Order();
//                    log.info(method);
//                    String txHash = event.getString("TxHash");
//                    if ("sendToken".equals(method)) {
//                        // sendToken推送的事件
//                        String exchangeId = states.getString(1);
//                        String demander = String.format(Constant.ONTID_PREFIX,Address.parse(states.getString(2)).toBase58());
//                        String provider = String.format(Constant.ONTID_PREFIX,Address.parse(states.getString(3)).toBase58());
//                        String tokenAddress = states.getString(4);
//                        JSONArray dataList = states.getJSONArray(5);
//                        JSONArray priceList = states.getJSONArray(6);
//                        String waitSendMsgTime = states.getString(7);
//
//                        order.setOrderId(exchangeId);
//                        order.setState("boughtOnchain");
//                        order.setBuyDate(new Date());
//                        order.setBuyerOntid(demander);
//                        order.setBuyEvent(event.toJSONString());
//                        order.setBuyTx(txHash);
//                        order.setSellerOntid(provider);
//
//                        List<OrderData> ods = new ArrayList<>();
//                        for (int n = 0; n < dataList.size(); n++) {
//                            OrderData od = new OrderData();
//                            od.setId(UUID.randomUUID().toString());
//                            od.setOrderId(order.getOrderId());
//                            od.setDataId(new String(Helper.hexToBytes(dataList.getString(n)),"utf-8"));
//                            ods.add(od);
//                        }
//                        order.setOrderData(ods);
//
//                    } else if ("sendEncMessage".equals(method)) {
//                        // sendEncMessage推送的事件
//                        String exchangeId = states.getString(1);
//                        String demander = String.format(Constant.ONTID_PREFIX,Address.parse(states.getString(2)).toBase58());
//                        String provider = String.format(Constant.ONTID_PREFIX,Address.parse(states.getString(3)).toBase58());
//
//                        order.setOrderId(exchangeId);
//                        order.setBuyerOntid(demander);
//                        order.setSellerOntid(provider);
//                        order.setState("deliveredOnchain");
//                        order.setSellDate(new Date());
//                        order.setSellEvent(event.toJSONString());
//                        order.setSellTx(txHash);
//
//                    } else if ("receiveEncMessage".equals(method)) {
//                        // receiveEncMessage推送的事件
//                        String exchangeId = states.getString(1);
//
//                        order.setOrderId(exchangeId);
//                        order.setState("buyerRecvMsgOnchain");
//                        order.setRecvMsgDate(new Date());
//                        order.setRecvMsgEvent(event.toJSONString());
//                        order.setRecvMsgTx(txHash);
//
//                    } else if ("receiveToken".equals(method)) {
//                        // receiveToken推送的事件
//                        String exchangeId = states.getString(1);
////                        String address = states.getString(3);
////                        log.info(address);
////                        log.info("event:{}",event);
////                        String demander = String.format(Constant.ONTID_PREFIX,Address.parse(states.getString(3)).toBase58());
////                        String provider = String.format(Constant.ONTID_PREFIX,Address.parse(states.getString(4)).toBase58());
//
//                        order.setOrderId(exchangeId);
////                        order.setBuyerOntid(demander);
////                        order.setSellerOntid(provider);
//                        order.setState("sellerRecvTokenOnchain");
//                        order.setRecvTokenDate(new Date());
//                        order.setRecvTokenEvent(event.toJSONString());
//                        order.setRecvTokenTx(txHash);
//
//                    } else if ("cancelExchange".equals(method)) {
//                        // cancelExchange推送的事件
//                        String exchangeId = states.getString(1);
//
//                        order.setOrderId(exchangeId);
//                        order.setState("buyerCancelOnchain");
//                        order.setCancelDate(new Date());
//                        order.setCancelEvent(event.toJSONString());
//                        order.setCancelTx(txHash);
//                    }
//                    // 只根据orderId查询
//                    Order one = orderMapper.selectByPrimaryKey(order.getOrderId());
//                    if (one == null) {
//                        // insert
//                        orderMapper.insertSelective(order);
//                    } else {
//                        // update
//                        orderMapper.updateByPrimaryKeySelective(order);
//                    }
//                    if ("boughtOnchain".equals(order.getState())) {
//                        List<OrderData> orderData = order.getOrderData();
//                        orderDataMapper.insertList(orderData);
//                    }
//                }
//            }
//            ack.acknowledge();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
