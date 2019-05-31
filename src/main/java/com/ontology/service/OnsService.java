package com.ontology.service;



public interface OnsService {

    Boolean registerOns(String action, String ontid, String domain);

    String loginOns(String action, String ontid);
}
