package com.infomedia.hikvisiondemo.controller;

import com.infomedia.hikvisiondemo.dto.MessageResponse;
import com.infomedia.hikvisiondemo.dto.PersonDto;
import com.infomedia.hikvisiondemo.service.DemoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Tag(name = "Register")
@SecurityRequirement(name = "X-API-KEY")
public class RegisterController {

    @Autowired
    private DemoService demoService;

    @SneakyThrows
    @PostMapping("demo/api/register")
    public ResponseEntity<MessageResponse> apiPostRegister(@Valid @RequestBody PersonDto personDto) {

        demoService.registerPerson(personDto);
        return ResponseEntity.ok(new MessageResponse("Registro exitoso"));
    }
}
