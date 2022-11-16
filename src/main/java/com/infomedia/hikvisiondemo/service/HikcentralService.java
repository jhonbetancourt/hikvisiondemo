package com.infomedia.hikvisiondemo.service;

import com.infomedia.hikvisiondemo.exception.HikcentralException;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.HikcentralOpenAPI;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.model.*;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.request.AddPerson;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.request.PrivilegeGroupPersons;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.response.AddResponse;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.response.GetResponse;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.response.ListResponse;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.response.NormalResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class HikcentralService {

    @Value("${hikcentral.openapi.url}")
    private String url;

    @Value("${hikcentral.openapi.secret}")
    private String secret;

    @Value("${hikcentral.openapi.key}")
    private String key;

    @Value("${hikcentral.openapi.eventRcv.AllowedIps}")
    private List<String> eventRcvAllowedIps;

    @Value("${hikcentral.openapi.eventRcv.Url}")
    private String eventRcvUrl;

    private HikcentralOpenAPI openAPI;

    @PostConstruct
    public void init() {
        openAPI = new HikcentralOpenAPI(url, key, secret);
    }

    public String registerPerson(AddPerson person, String privilegeGroupId) throws IOException {
       /* AddPerson person = new AddPerson(null, visitante.getApellido(), visitante.getNombre(), null
                , orgIndexCode, visitante.getTelefono(), visitante.getEmail()
                , new String(Base64.getEncoder().encode(visitante.getFotoVisitante().getDatos()))
                , fecha.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault())
                , fecha.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()));*/

        AddResponse addResponse = openAPI.addPerson(person);
        if (!addResponse.isSuccess()) {
            throw new HikcentralException(addResponse.getMsg());
        }

        GetResponse<Person> personGetResponse = openAPI.getPerson(addResponse.getId());
        if (!personGetResponse.isSuccess()) {
            throw new HikcentralException(personGetResponse.getMsg());
        }

        try {
            registerPersonPrivilegeGroup(personGetResponse.getData(), privilegeGroupId);
        }catch (Exception e){
            openAPI.deletePerson(personGetResponse.getData().getPersonId());
            throw e;
        }

        return addResponse.getId();
    }

    public void registerPersonPrivilegeGroup(Person person, String privilegeGroupId) throws IOException, HikcentralException {
        PrivilegeGroupPersons privilegeGroupPersons = new PrivilegeGroupPersons();
        privilegeGroupPersons.setPrivilegeGroupId(privilegeGroupId);
        privilegeGroupPersons.setType(1);
        privilegeGroupPersons.setList(new ArrayList<>());
        PersonId personId = new PersonId(person.getPersonId());
        privilegeGroupPersons.getList().add(personId);

        NormalResponse privilegeResponse = openAPI.addPrivilegeGroupPersons(privilegeGroupPersons);
        if (!privilegeResponse.isSuccess()) {
            throw new HikcentralException(privilegeResponse.getMsg());
        }

        NormalResponse reapplicationResponse = openAPI.personAccessLevelReapplication();
        if (!reapplicationResponse.isSuccess()) {
            throw new HikcentralException(reapplicationResponse.getMsg());
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<PrivilegeGroup> getPrivilegeGroups() throws IOException {
        ListResponse<PrivilegeGroup> privilegeGroupListResponse =
                openAPI.listPrivilegeGroup(1);

        if (!privilegeGroupListResponse.isSuccess()) {
            throw new HikcentralException(privilegeGroupListResponse.getMsg());
        }

        return privilegeGroupListResponse.getDataList();
    }

    public List<Organization> getOrganizations() throws IOException {
        return openAPI.listOrganization().getDataList();
    }

    public List<Door> getDoors() throws IOException {
        ListResponse<Door> doorListResponse = openAPI.listDoor();
        if (!doorListResponse.isSuccess()) {
            throw new HikcentralException(doorListResponse.getMsg());
        }
        return doorListResponse.getDataList();
    }

    public Door getDoor(String doorId) throws IOException {
        GetResponse<Door> doorGetResponse = openAPI.getDoor(doorId);
        if (!doorGetResponse.isSuccess()) {
            throw new HikcentralException(doorGetResponse.getMsg());
        }
        return doorGetResponse.getData();
    }
}
