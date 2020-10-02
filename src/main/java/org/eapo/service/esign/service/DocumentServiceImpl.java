package org.eapo.service.esign.service;

import org.eapo.service.esign.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DocumentServiceImpl implements DocumentService {

    public static final String CORRESP_KEY = "CORRESP";

    @Autowired
    private RedisTemplate redisTemplate;

    private HashOperations hashOperations;

    @PostConstruct
    public void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public String save(Document document) {
        hashOperations.put(CORRESP_KEY, document.hashCode(), document);
        return document.getId();
    }

    @Override
    public Document get(String id) {
        return (Document) hashOperations.get(CORRESP_KEY,id);
    }
}
