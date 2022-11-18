package com.infomedia.hikvisiondemo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infomedia.hikvisiondemo.App;
import com.infomedia.hikvisiondemo.dto.HikcentralDataDto;
import com.infomedia.hikvisiondemo.dto.PersonDto;
import com.infomedia.hikvisiondemo.util.JsonFile;
import com.infomedia.hikvisiondemo.util.Waiter;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.request.AddPerson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Log4j2
public class DemoService {

    @Autowired
    private HikcentralService hikcentralService;

    private List<String> codigos;

    @PostConstruct
    private void init() {
        ApplicationHome applicationHome = new ApplicationHome(App.class);
        JsonFile codigosFile = new JsonFile(new File(applicationHome.getDir(), "codigos.json"));
        try {
            codigos = codigosFile.read(new TypeReference<List<String>>(){});
            log.info("Codigos: "+codigos);
        }catch (IOException e){
            log.info(e.getMessage());
            codigos = List.of("AAA111");
            log.info("Usando codigo por defecto: AAA111");
        }

    }

    public HikcentralDataDto getHikcentralData() throws Exception {
        HikcentralDataDto hikcentralDataDto = new HikcentralDataDto();

        Waiter waiter = new Waiter(
                () -> hikcentralDataDto.setOrganizations(hikcentralService.getOrganizations())
                , () -> hikcentralDataDto.setPrivilegeGroups(hikcentralService.getPrivilegeGroups()));
        waiter.setCancelOnException(true);

        waiter.start();

        if(waiter.getLastException()!=null){
            throw waiter.getLastException();
        }

        return hikcentralDataDto;
    }

    public String registerPerson(PersonDto personDto) throws IOException {
        log.info("Register person: "+ personDto.getNombre()+" "+ personDto.getApellido());

        LocalDate fecha = LocalDate.now();

        AddPerson addPerson = new AddPerson();
        addPerson.setOrgIndexCode(personDto.getOrgId());
        addPerson.setPersonGivenName(personDto.getNombre());
        addPerson.setPersonFamilyName(personDto.getApellido());
        addPerson.setEmail(personDto.getEmail());
        addPerson.setPhoneNo(personDto.getTelefono());
        addPerson.setBeginTime(fecha.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()));
        addPerson.setEndTime(fecha.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()));
        addPerson.setFaces(List.of(new AddPerson.Face(personDto.getImageBase64().split(",")[1])));

        String personId = hikcentralService.registerPerson(addPerson, personDto.getPrivId());

        log.info("Registered person id: "+personId);

        return personId;
    }

    public boolean codigoIsValid(String codigo){
        log.info("Codigo ingresado: "+codigo);
        return codigos.contains(codigo);
    }
}
