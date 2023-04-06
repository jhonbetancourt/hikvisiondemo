package com.infomedia.hikvisiondemo.controller;

import com.infomedia.hikvisiondemo.dto.MessageResponse;
import com.infomedia.hikvisiondemo.dto.PersonDto;
import com.infomedia.hikvisiondemo.service.DemoService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Hidden
@RestController
@RequestMapping("/demo")
public class DemoRestController {

    @Autowired
    private DemoService demoService;

    @SneakyThrows
    @PostMapping("/register")
    public MessageResponse postRegister(@Valid @RequestBody PersonDto personDto) {

        demoService.registerPerson(personDto);

        return new MessageResponse("Registro exitoso");
    }
}
