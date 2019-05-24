package com.ontology.service;



public interface OnsService {

    Boolean findByOntid(String action, String ontid, String domain);

    String findOns(String action, String ontid);
}
