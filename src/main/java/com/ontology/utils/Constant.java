package com.ontology.utils;

/**
 * 常量
 */
public class Constant {

    /**
     * ES索引和类型
     */
    public static final String ES_INDEX_SOURCE = "ont_sourcing";
    public static final String ES_TYPE_SOURCE = "sourcing";
    public static final String ES_INDEX_ORDER = "order_index";
    public static final String ES_TYPE_ORDER = "order";

    /**
     * KAFKA topic
     */
    public static final String KAFKA_TOPIC_EVENT = "topic-block-event";

    public static final String KAFKA_TOPIC_SOURCING = "topic-sourcing-event";

    public static final String KAFKA_TOPIC_MP = "topic-medata-event";

    /**
     * KAFKA group
     */
    public static final String KAFKA_GROUP_EVENT = "group-event";

    public static final String KAFKA_GROUP_SOURCING = "group-sourcing";

    public static final String KAFKA_GROUP_MP = "group-marketplace";


    public static final String HTTPHEADER_LANGUAGE = "Lang";


    public static final String HTTP_HEADER_SECURE = "Secure-Key";

    /**
     * sdk手续费
     */
    public static final long GAS_PRICE = 500;
    public static final long GAS_Limit = 20000;


    public static final String ONTID_PREFIX = "did:ont:%s";
}
