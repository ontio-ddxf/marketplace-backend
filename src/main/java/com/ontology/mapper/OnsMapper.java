package com.ontology.mapper;

import com.ontology.entity.Ons;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;


@Component
public interface OnsMapper extends Mapper<Ons> {

    Ons findByOntid(String ontid);

}
