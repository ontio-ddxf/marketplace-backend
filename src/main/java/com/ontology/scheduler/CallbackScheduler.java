package com.ontology.scheduler;

import com.alibaba.fastjson.JSON;
import com.ontology.entity.TxCallback;
import com.ontology.mapper.TxCallbackMapper;
import com.ontology.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lijie
 * @version 1.0
 * @date 2019/7/4
 */
@Slf4j
@Component
@EnableScheduling
public class CallbackScheduler extends BaseScheduler {

    private static final String authType = "authId";
    private static final String orderType = "orderId";
    private static final String tokenId = "tokenId";
    @Autowired
    private TxCallbackMapper txCallbackMapper;
    @Autowired
    private HttpClientUtils httpClientUtils;


//    @Scheduled(initialDelay = 6 * 1000, fixedDelay = 6 * 1000)
    private void callbackTxState() {
        log.info("callbackTxState begin...");
        List<TxCallback> txCallbacks = txCallbackMapper.selectNeedCallback();

        // 查询出的都是满足回调的
        for (TxCallback txCallback : txCallbacks) {
            String callback = txCallback.getCallbackUrl();
            String txHash = txCallback.getTxHash();
            String sourceHash = txCallback.getSourceHash();
            Integer sourceState = txCallback.getSourceOnchainState();
            Integer txOnchainState = txCallback.getTxOnchainState();

            Map<String, Object> params = new HashMap<>();

            if (sourceState != null && txOnchainState != null) {
                // 两笔交易已确认，可以回调
                params.put("sourceHash", sourceHash);
                params.put("sourceState", sourceState);
                params.put("txHash", txHash);
                params.put("txState", txOnchainState);

                // 判断是否需要回调业务id
                if (authType.equals(txCallback.getBusinessType())) {
                    params.put(authType, txCallback.getBusinessId());
                } else if (orderType.equals(txCallback.getBusinessType())) {
                    // 注释代码为返回tokenId的情况
//                    String[] split = txCallback.getBusinessId().split(",");
//                    params.put(orderType, split[0]);
//                    params.put(tokenId, split[1]);
                    params.put(orderType, txCallback.getBusinessId());
                }
            } else if (StringUtils.isEmpty(txHash)) {
                // 只有存证，直接回调
                params.put("sourceHash", sourceHash);
                params.put("sourceState", sourceState);

            } else if (StringUtils.isEmpty(sourceHash)) {
                // 只有交易，直接回调
                params.put("txHash", txHash);
                params.put("txState", txOnchainState);
            }

            try {
                httpClientUtils.httpClientPost(callback, JSON.toJSONString(params), new HashMap<>());
                txCallback.setCallbackState(1);
            } catch (Exception e) {
                log.error("http post error:{}", e);
                txCallback.setCallbackState(0);
            }
            txCallbackMapper.updateByPrimaryKeySelective(txCallback);
        }

    }

//    @Scheduled(initialDelay = 5 * 1000, fixedDelay = 5 * 1000)
    private void reCallback() {
        log.info("reCallback begin...");
        TxCallback record = new TxCallback();
        record.setCallbackState(0);
        List<TxCallback> callbackList = txCallbackMapper.select(record);

        // 查询出的都是满足回调的
        for (TxCallback txCallback : callbackList) {
            String callback = txCallback.getCallbackUrl();
            String txHash = txCallback.getTxHash();
            String sourceHash = txCallback.getSourceHash();
            Integer sourceState = txCallback.getSourceOnchainState();
            Integer txOnchainState = txCallback.getTxOnchainState();

            Map<String, Object> params = new HashMap<>();

            if (sourceState != null && txOnchainState != null) {
                // 两笔交易已确认，可以回调
                params.put("sourceHash", sourceHash);
                params.put("sourceState", sourceState);
                params.put("txHash", txHash);
                params.put("txState", txOnchainState);

                // 判断是否需要回调业务id
                if (authType.equals(txCallback.getBusinessType())) {
                    params.put(authType, txCallback.getBusinessId());
                } else if (orderType.equals(txCallback.getBusinessType())) {
                    String[] split = txCallback.getBusinessId().split(",");
                    params.put(orderType, split[0]);
                    params.put(tokenId, split[1]);
                }
            } else if (StringUtils.isEmpty(txHash)) {
                // 只有存证，直接回调
                params.put("sourceHash", sourceHash);
                params.put("sourceState", sourceState);

            } else if (StringUtils.isEmpty(sourceHash)) {
                // 只有交易，直接回调
                params.put("txHash", txHash);
                params.put("txState", txOnchainState);
            }

            try {
                httpClientUtils.httpClientPost(callback, JSON.toJSONString(params), new HashMap<>());
                txCallback.setCallbackState(1);
            } catch (Exception e) {
                log.error("http post error:{}", e);
                txCallback.setCallbackState(0);
            }
            txCallbackMapper.updateByPrimaryKeySelective(txCallback);
        }

    }

}
