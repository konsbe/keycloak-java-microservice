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

    public String createObject(Object userData) {
        ObjectMapper postData = new ObjectMapper();
        String jsonBody;
        try {
            jsonBody = postData.writeValueAsString(userData);
        } catch (Exception e) {
            jsonBody = e.toString();
        }
        return jsonBody;

    }

    public JsonNode createJSONObject(String data) {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            jsonNode = null;
        }
        return jsonNode;
    }

}
