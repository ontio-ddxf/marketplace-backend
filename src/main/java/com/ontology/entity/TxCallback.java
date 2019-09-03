package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tbl_tx_callback")
public class TxCallback {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String txHash;

    private String callback;

    private Date createTime;
}