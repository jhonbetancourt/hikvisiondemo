package com.infomedia.hikvisiondemo.service;

import com.infomedia.hikvisiondemo.exception.HikcentralException;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.HikcentralOpenAPI;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.model.*;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.request.AddFaceComparisonGroupPerson;
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

    @Value("${hikcentral.openapi.faceCheckAcsDevIndexCode}")
    private String faceCheckAcsDevIndexCode;

    private HikcentralOpenAPI openAPI;

    @PostConstruct
    public void init() {
        openAPI = new HikcentralOpenAPI(url, key, secret);
    }

    public String registerPerson(AddPerson person, String privilegeGroupId, String faceComparisonGroupId) throws IOException {

        faceCheck(person);

        GetResponse<Person> getPersonResponse = openAPI.getPersonByCode(person.getPersonCode());
        if (getPersonResponse.isSuccess()) {
            openAPI.deletePerson(getPersonResponse.getData().getPersonId());
        }

        AddResponse addResponse = openAPI.addPerson(person);
        if (!addResponse.isSuccess()) {
            throw new HikcentralException(addResponse.getMsg());
        }

        try {
            registerPersonPrivilegeGroup(addResponse.getId(), privilegeGroupId);
        }catch (Exception e){
            openAPI.deletePerson(addResponse.getId());
            throw e;
        }

        try {
            registrarPersonFaceComparisonGroup(addResponse.getId(), faceComparisonGroupId);
        }catch (Exception e){
            openAPI.deletePerson(addResponse.getId());
            throw e;
        }

        NormalResponse fcgReapplicationResponse = openAPI
                .faceComparisonGroupReapplication(faceComparisonGroupId);
        if (!fcgReapplicationResponse.isSuccess()) {
            openAPI.deletePerson(addResponse.getId());
            throw new HikcentralException(fcgReapplicationResponse.getMsg());
        }

        NormalResponse reapplicationResponse = openAPI.personAccessLevelReapplication();
        if (!reapplicationResponse.isSuccess()) {
            openAPI.deletePerson(addResponse.getId());
            throw new HikcentralException(reapplicationResponse.getMsg());
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return addResponse.getId();
    }

    public void registerPersonPrivilegeGroup(String personIdStr, String privilegeGroupId) throws IOException, HikcentralException {
        PrivilegeGroupPersons privilegeGroupPersons = new PrivilegeGroupPersons();
        privilegeGroupPersons.setPrivilegeGroupId(privilegeGroupId);
        privilegeGroupPersons.setType(1);
        privilegeGroupPersons.setList(new ArrayList<>());
        PersonId personId = new PersonId(personIdStr);
        privilegeGroupPersons.getList().add(personId);

        NormalResponse privilegeResponse = openAPI.addPrivilegeGroupPersons(privilegeGroupPersons);
        if (!privilegeResponse.isSuccess()) {
            throw new HikcentralException(privilegeResponse.getMsg());
        }
    }

    private void registrarPersonFaceComparisonGroup(String idPerson, String faceComparisonGroupId) throws IOException {

        NormalResponse addPersonFcgResponse = openAPI.addFaceComparisonGroupPerson(
                new AddFaceComparisonGroupPerson(idPerson, faceComparisonGroupId));

        if (!addPersonFcgResponse.isSuccess()) {
            throw new HikcentralException(addPersonFcgResponse.getMsg());
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

    public List<FaceComparisonGroup> getFaceComparisonGroups() throws IOException {
        return openAPI.listFaceComparisonGroup().getDataList();
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

    public void faceCheck(AddPerson addPerson) throws IOException {
        for(AddPerson.Face f :addPerson.getFaces()){
            faceCheck(f.getFaceData());
        }
    }

    public void faceCheck(String faceDataBase64) throws IOException {
        NormalResponse faceCheckResponse = openAPI.faceCheck(faceDataBase64
                , faceCheckAcsDevIndexCode);

        if (!faceCheckResponse.isSuccess()) {
            if(faceCheckResponse.getCode()==128&&
                    faceCheckResponse.getMsg().contains("person data is invalid")){
                throw new HikcentralException("La imagen de la persona no es válida");
            }else{
                throw new HikcentralException(faceCheckResponse.getMsg());
            }
        }
    }
}
