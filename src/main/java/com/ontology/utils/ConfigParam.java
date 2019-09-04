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

	/**
	 *  合约hash
	 */
	@Value("${contract.hash.sourcing}")
	public String CONTRACT_HASH_SOURCING;

	@Value("${contract.hash.dtoken}")
	public String CONTRACT_HASH_DTOKEN;

	@Value("${contract.hash.mp}")
	public String CONTRACT_HASH_MP;

}