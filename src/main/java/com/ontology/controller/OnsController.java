package com.ontology.controller;

import com.ontology.controller.dto.OnsReq;
import com.ontology.model.Result;
import com.ontology.service.OnsService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "域名接口")
@RestController
@CrossOrigin
public class OnsController {

    @Autowired
    private OnsService onsService;


    @ApiOperation(value="ontid注册域名", notes="ontid注册域名" ,httpMethod="POST")
    @PostMapping("/api/v1/data-dealer/ons/register")
    public Result register(@RequestBody OnsReq req) {
        String action = "register";
        String ontid = req.getOntid();
        String domain = req.getDomain();
        Boolean allowance = onsService.findByOntid(action, ontid, domain);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), allowance);
    }

    @ApiOperation(value="ontid登录后检查是否存在域名", notes="ontid登录后检查是否存在域名" ,httpMethod="GET")
    @GetMapping(value = "/api/v1/data-dealer/ons/login/{ontid}")
    public Result login(@PathVariable String ontid) {
        String action = "login";

        String ons = onsService.findOns(action, ontid);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ons);
    }
}
