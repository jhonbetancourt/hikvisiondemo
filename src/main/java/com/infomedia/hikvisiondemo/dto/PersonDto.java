package com.infomedia.hikvisiondemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class PersonDto {

    @NotBlank
    @Schema(example = "1112223333")
    private String identificacion;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @Size(min = 1)
    @Pattern(regexp = "[0-9]+", message = "ingresado no es valido")
    @Schema(example = "3005556666")
    private String telefono;
    @Size(min = 1)
    @Pattern(regexp = "\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", message = "ingresado no es valido")
    @Schema(example = "example@domain.com")
    private String email;
    @NotBlank
    private String imageBase64;
    @NotBlank
    private String orgId;
    @NotBlank
    private String privId;
    @NotBlank
    private String fcgId;

    @Override
    public String toString() {
        return "PersonDto{" +
                "identificacion='" + identificacion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", orgId='" + orgId + '\'' +
                ", privId='" + privId + '\'' +
                ", fcgId='" + fcgId + '\'' +
                '}';
    }
}
