package com.infomedia.hikvisiondemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IdMessageResponse {
    @Schema(description = "mesaje de respuesta", example = "respuesta")
    private String message;
    @Schema(description = "id del objeto", example = "1")
    private String id;
}
