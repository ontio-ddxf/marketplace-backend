package com.ontology.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OnsReq {
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;
    @ApiModelProperty(name="domin",value = "domin")
    private String domain;

}
