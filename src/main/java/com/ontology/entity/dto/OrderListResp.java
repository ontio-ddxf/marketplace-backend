package com.ontology.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ontology.entity.dto.OrderDataResp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderListResp {
    @ApiModelProperty(name="orderId",value = "orderId")
    private String orderId;
    @ApiModelProperty(name="dataDemander",value = "dataDemander")
    private String dataDemander;
    @ApiModelProperty(name="dataProvider",value = "dataProvider")
    private String dataProvider;
    @ApiModelProperty(name="dataIdList",value = "dataIdList")
    private List<OrderDataResp> dataIdList;
    @ApiModelProperty(name="buyDate",value = "buyDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date buyDate;
    @ApiModelProperty(name="state",value = "state")
    private String state;
    @ApiModelProperty(name="isRecvMsg",value = "isRecvMsg")
    private Integer isRecvMsg;
    @ApiModelProperty(name="isRecvToken",value = "isRecvToken")
    private Integer isRecvToken;
}
