package com.ontology.service;

import com.github.pagehelper.PageInfo;
import com.ontology.entity.Order;

import java.util.List;

public interface ToolsService {

    PageInfo<Order> queryList(String action, Integer provider, String ontid, int pageNum, int pageSize);

    List<String> getData(String action, String orderId, String ontid);
}
