package com.infomedia.hikvisiondemo.util.hikcentral.openapi.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListResponse<T> {
    private boolean success;
    private String msg;
    private List<T> dataList;
    private int code;

    public ListResponse (String openapiResponseJson, ObjectMapper objectMapper, TypeReference<List<T>> type) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(openapiResponseJson);
        msg = jsonNode.get("msg").asText();
        code = jsonNode.get("code").asInt();
        success = code==0;
        if(success){
            dataList = objectMapper.readValue(jsonNode.get("data").get("list").toString(), type);
        }
    }
}
