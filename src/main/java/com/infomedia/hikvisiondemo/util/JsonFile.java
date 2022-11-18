package com.infomedia.hikvisiondemo.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonFile {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.findAndRegisterModules();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private final File file;

    public JsonFile(File file){
        this.file = file;
    }

    public void save(Object object) throws IOException {
        String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        FileUtils.writeStringToFile(file, jsonString, StandardCharsets.UTF_8);
    }

    public <T> T read(Class<T> oClass) throws IOException {
        String jsonString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        return objectMapper.readValue(jsonString, oClass);
    }

    public <T> T read(TypeReference<T> typeReference) throws IOException {
        String jsonString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        return objectMapper.readValue(jsonString, typeReference);
    }
}
