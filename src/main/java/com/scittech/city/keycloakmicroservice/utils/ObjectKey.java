package com.scittech.city.keycloakmicroservice.utils;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ObjectKey {

    public String getKey(String obj, String key) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        String objKey;
        try {
            jsonNode = objectMapper.readTree(obj);
            objKey = jsonNode.get(key).asText();
        } catch (JsonProcessingException e1) {
            objKey = null;
        }
        return objKey;
    }

}