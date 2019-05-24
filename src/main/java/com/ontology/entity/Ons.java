package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Table(name = "tbl_ons")
@Data
public class Ons {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String ontid;

    private String domain;

    private Date createTime;

    private Date updateTime;

    private Integer state;


}
