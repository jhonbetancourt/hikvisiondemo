package com.infomedia.hikvisiondemo.controller;

import com.infomedia.hikvisiondemo.service.HikcentralService;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.model.Organization;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.model.PrivilegeGroup;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Hikcentral")
@SecurityRequirement(name = "X-API-KEY")
@RequestMapping("/demo/api/hikcentral")
public class HikcentralController {

    @Autowired
    private HikcentralService hikcentralService;

    @SneakyThrows
    @GetMapping("privilegeGroups")
    public List<PrivilegeGroup> getPrivilegeGroups() {
        return hikcentralService.getPrivilegeGroups();
    }

    @SneakyThrows
    @GetMapping("organizations")
    public List<Organization> getOrganizations() {
        return hikcentralService.getOrganizations();
    }
}
