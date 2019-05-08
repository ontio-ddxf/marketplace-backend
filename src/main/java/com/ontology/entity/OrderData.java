package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tbl_order_data_sync")
public class OrderData {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String orderId;

    private String dataId;

}