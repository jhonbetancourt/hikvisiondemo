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
public class NormalResponse {
    private boolean success;
    private String msg;
    private int code;

    public NormalResponse(String openapiResponseJson, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(openapiResponseJson);
        msg = jsonNode.get("msg").asText();
        code = jsonNode.get("code").asInt();
        success = code==0;
    }
}
