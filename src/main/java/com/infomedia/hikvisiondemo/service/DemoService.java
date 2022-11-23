package com.infomedia.hikvisiondemo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infomedia.hikvisiondemo.App;
import com.infomedia.hikvisiondemo.dto.HikcentralDataDto;
import com.infomedia.hikvisiondemo.dto.PersonDto;
import com.infomedia.hikvisiondemo.util.ApiKeyFilter;
import com.infomedia.hikvisiondemo.util.JsonFile;
import com.infomedia.hikvisiondemo.util.Waiter;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.request.AddPerson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class DemoService {

    @Autowired
    private HikcentralService hikcentralService;

    private List<String> codigos;

    private List<UUID> apiKeys;

    @PostConstruct
    private void init() {
        ApplicationHome applicationHome = new ApplicationHome(App.class);
        JsonFile codigosFile = new JsonFile(new File(applicationHome.getDir(), "codigos.json"));
        try {
            codigos = codigosFile.read(new TypeReference<List<String>>(){});
            log.info("Codigos: "+codigos);
        }catch (IOException e){
            log.info(e.getMessage());
            String defaultCodigo = "AAA111";
            codigos = List.of(defaultCodigo);
            log.info("Usando codigo por defecto: "+defaultCodigo);
        }

        JsonFile apiKeysFile = new JsonFile(new File(applicationHome.getDir(), "apikeys.json"));
        try {
            apiKeys = apiKeysFile.read(new TypeReference<List<UUID>>(){});
            log.info("Api Keys: "+apiKeys);
        }catch (IOException e){
            log.info(e.getMessage());
            UUID defaultApiKey = UUID.fromString("4f8e6244-54ca-4203-bb37-2eee751bc87e");
            apiKeys = List.of(defaultApiKey);
            log.info("Usando Api Key por defecto: "+defaultApiKey);
        }

    }

    @Bean
    private ApiKeyFilter.Validator apiKeyValidator(){
        return apiKey -> {
            UUID uuid = UUID.fromString(apiKey);
            if(apiKeys.contains(uuid)){
                return new ApiKeyFilter.ValidationResult(true, apiKey);
            }
            return new ApiKeyFilter.ValidationResult(false, null);
        };
    }

    public void login(String codigo){
        log.info("Login: "+codigo);
        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(codigo, null, new HashSet<>());
        log.info("Login: "+authToken.isAuthenticated());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public boolean isAuthenticated(){
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof AnonymousAuthenticationToken);
    }

    public void logout(HttpServletRequest request){
        String codigo;
        try {
            codigo = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Logout: "+codigo);
        }catch (Exception ignore){}
        HttpSession session= request.getSession(false);
        SecurityContextHolder.clearContext();
        if(session != null) {
            session.invalidate();
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
        if(personDto.getImageBase64().contains(",")){
            addPerson.setFaces(List.of(new AddPerson.Face(personDto.getImageBase64().split(",")[1])));
        }else{
            addPerson.setFaces(List.of(new AddPerson.Face(personDto.getImageBase64())));
        }

        String personId = hikcentralService.registerPerson(addPerson, personDto.getPrivId());

        log.info("Registered person id: "+personId);

        return personId;
    }

    public boolean codigoIsValid(String codigo){
        log.info("Codigo ingresado: "+codigo);
        return codigos.contains(codigo);
    }
}
