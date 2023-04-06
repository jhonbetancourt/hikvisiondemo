package com.infomedia.hikvisiondemo.controller;

import com.infomedia.hikvisiondemo.dto.PersonDto;
import com.infomedia.hikvisiondemo.dto.HikcentralDataDto;
import com.infomedia.hikvisiondemo.service.DemoService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("login")
    public String getLogin() {
        if(demoService.isAuthenticated()){
            return "redirect:/demo/register";
        }else{
            return "demo-login";
        }
    }

    @PostMapping("login")
    public String postLogin(@RequestParam String codigo, Model model) {
        if(demoService.codigoIsValid(codigo)){
            demoService.login(codigo);
            return "redirect:/demo/register";
        }else{
            model.addAttribute("codigoIsInvalid", true);
            model.addAttribute("codigo", codigo);
            return "demo-login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        demoService.logout(request);
        return "redirect:/demo/login";
    }

    @SneakyThrows
    @GetMapping("/register")
    public String getRegister(Model model) {

        HikcentralDataDto hikcentralDataDto = demoService.getHikcentralData();

        PersonDto personDto = new PersonDto();

        model.addAttribute("orgs", hikcentralDataDto.getOrganizations());
        model.addAttribute("privs", hikcentralDataDto.getPrivilegeGroups());
        model.addAttribute("fcgs", hikcentralDataDto.getFaceComparisonGroups());
        model.addAttribute("personDto", personDto);

        return "demo";
    }

    @GetMapping("swagger")
    public RedirectView swagger(){
        return new RedirectView("/swagger-ui/index.html");
    }
}
