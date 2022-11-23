package com.infomedia.hikvisiondemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class PersonDto {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @NotBlank
    private String telefono;
    @NotBlank
    private String email;
    @NotBlank
    private String imageBase64;

    @NotBlank
    private String orgId;
    @NotBlank
    private String privId;
}
