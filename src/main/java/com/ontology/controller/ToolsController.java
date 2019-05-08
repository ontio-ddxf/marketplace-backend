package com.ontology.controller;

import com.github.pagehelper.PageInfo;
import com.ontology.entity.Order;
import com.ontology.model.Result;
import com.ontology.service.ToolsService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "工具接口")
@RestController
@CrossOrigin
public class ToolsController {

    @Autowired
    private ToolsService toolsService;


    @ApiOperation(value="查询订单", notes="查询订单" ,httpMethod="GET")
    @RequestMapping(value = "/api/v1/data-dealer/tools/orders/{type}", method = RequestMethod.GET)
    public Result queryList(@PathVariable Integer type, String ontid, Integer pageNum, Integer pageSize) {
        String action = "queryList";

        PageInfo<Order> orderInfo = toolsService.queryList(action, type, ontid, pageNum, pageSize);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), orderInfo);
    }

    @ApiOperation(value="获取数据", notes="获取数据" ,httpMethod="GET")
    @RequestMapping(value = "/api/v1/data-dealer/tools/data", method = RequestMethod.GET)
    public Result queryList(String orderId, String ontid) {
        String action = "getData";

        List<String> dataList = toolsService.getData(action, orderId, ontid);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), dataList);
    }
}
