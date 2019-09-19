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
    private Integer id;

    private String txHash;

    private String sourceHash;

    private String callbackUrl;

    private Date createTime;

    private String businessType;

    private String businessId;

    private Integer txOnchainState;

    private Integer sourceOnchainState;

    private Integer callbackState;
}