package com.infomedia.hikvisiondemo.util.hikcentral.openapi.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddResponse {
    private boolean success;
    private String msg;
    private String id;
    private int code;

    public AddResponse(String openapiResponseJson, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(openapiResponseJson);
        msg = jsonNode.get("msg").asText();
        code = jsonNode.get("code").asInt();
        success = code==0;
        if(success){
            id = jsonNode.get("data").asText();
        }
    }

    public AddResponse(String openapiResponseJson, ObjectMapper objectMapper, String idParamName) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(openapiResponseJson);
        msg = jsonNode.get("msg").asText();
        success = jsonNode.get("code").asInt()==0;
        if(success){
            id = jsonNode.get("data").get(idParamName).asText();
        }
    }
}
