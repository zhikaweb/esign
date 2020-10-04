package org.eapo.service.esign.service.store;

import org.eapo.service.esign.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DocumentServiceRedisImpl implements DocumentService {

    @Value("${esigner.redis.store.corresp.pool}")
    private String correspPool;

    @Autowired
    private RedisTemplate redisTemplate;

    private HashOperations hashOperations;

    @PostConstruct
    public void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public String save(Document document) {
        hashOperations.put(correspPool, document.getId(), document);
        return document.getId();
    }

    @Override
    public Document get(String id) {
        return (Document) hashOperations.get(correspPool, id);
    }
}
