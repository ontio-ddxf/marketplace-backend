package com.ontology.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {

	/**
	 *  SDK参数
	 */
	@Value("${service.restfulUrl}")
	public String RESTFUL_URL;

	@Value("${payer.addr}")
	public String PAYER_ADDRESS;

	@Value("${contract.hash.ons}")
	public String CONTRACT_HASH_ONS;

	@Value("${contract.hash.dtoken}")
	public String CONTRACT_HASH_DTOKEN;

	@Value("${contract.hash.marketplace}")
	public String CONTRACT_HASH_MP;

}