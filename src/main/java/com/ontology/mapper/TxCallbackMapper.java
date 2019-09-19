package com.ontology.mapper;

import com.ontology.entity.TxCallback;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Component
public interface TxCallbackMapper extends Mapper<TxCallback> {
    List<TxCallback> selectNeedCallback();
}