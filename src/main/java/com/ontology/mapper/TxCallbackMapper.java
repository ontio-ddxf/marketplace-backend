package com.ontology.mapper;

import com.ontology.entity.TxCallback;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface TxCallbackMapper extends Mapper<TxCallback> {
}