package com.infomedia.hikvisiondemo.controller;

import com.infomedia.hikvisiondemo.dto.PersonDto;
import com.infomedia.hikvisiondemo.dto.HikcentralDataDto;
import com.infomedia.hikvisiondemo.service.DemoService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("login")
    public String getLogin() {
        return "demo-login";
    }

    @PostMapping("login")
    public String postLogin(@RequestParam String codigo, Model model) {
        if(demoService.codigoIsValid(codigo)){
            return "redirect:/demo/register";
        }else{
            model.addAttribute("codigoIsInvalid", true);
            model.addAttribute("codigo", codigo);
            return "demo-login";
        }
    }

    @SneakyThrows
    @GetMapping("/register")
    public String getRegister(Model model) {

        HikcentralDataDto hikcentralDataDto = demoService.getHikcentralData();

        PersonDto personDto = new PersonDto();

        model.addAttribute("orgs", hikcentralDataDto.getOrganizations());
        model.addAttribute("privs", hikcentralDataDto.getPrivilegeGroups());
        model.addAttribute("personDto", personDto);

        return "demo";
    }

    @SneakyThrows
    @PostMapping("/register")
    public String postRegister(@ModelAttribute("personDto") PersonDto personDto) {

        demoService.registerPerson(personDto);

        return "demo-registrado";
    }
}
