package com.ontology.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderDataResp {
    @ApiModelProperty(name="dataId",value = "dataId")
    private String dataId;

}
