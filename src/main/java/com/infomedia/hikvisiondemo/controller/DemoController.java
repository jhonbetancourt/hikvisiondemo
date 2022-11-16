package com.infomedia.hikvisiondemo.controller;

import com.infomedia.hikvisiondemo.dto.FormPerson;
import com.infomedia.hikvisiondemo.service.HikcentralService;
import com.infomedia.hikvisiondemo.util.Waiter;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.request.AddPerson;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private HikcentralService hikcentralService;


    @SneakyThrows
    @RequestMapping
    public String demo(Model model) {

        Waiter waiter = new Waiter(
                () -> model.addAttribute("orgs", hikcentralService.getOrganizations())
                , () -> model.addAttribute("privs", hikcentralService.getPrivilegeGroups()));
        waiter.setCancelOnException(true);

        waiter.start();

        if(waiter.getLastException()!=null){
            throw waiter.getLastException();
        }

        FormPerson formPerson = new FormPerson();

        model.addAttribute("formPerson", formPerson);

        return "demo";
    }

    @SneakyThrows
    @PostMapping("/register")
    public String register(@ModelAttribute("formPerson") FormPerson person) {
        log.info("Register person: "+person.getNombre()+" "+person.getApellido());

        LocalDate fecha = LocalDate.now();

        AddPerson addPerson = new AddPerson();
        addPerson.setOrgIndexCode(person.getOrgId());
        addPerson.setPersonGivenName(person.getNombre());
        addPerson.setPersonFamilyName(person.getApellido());
        addPerson.setEmail(person.getEmail());
        addPerson.setPhoneNo(person.getTelefono());
        addPerson.setBeginTime(fecha.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()));
        addPerson.setEndTime(fecha.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()));
        addPerson.setFaces(List.of(new AddPerson.Face(person.getImageBase64().split(",")[1])));

        String personId = hikcentralService.registerPerson(addPerson, person.getPrivId());

        log.info("Registered person id: "+personId);

        return "demo-registrado";
    }
}
