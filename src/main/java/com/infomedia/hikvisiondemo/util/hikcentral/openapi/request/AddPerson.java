package com.infomedia.hikvisiondemo.util.hikcentral.openapi.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddPerson {
    private String personCode;
    private String personFamilyName;
    private String personGivenName;
    private Integer gender;
    private String orgIndexCode;
    private String phoneNo;
    private String email;
    private List<Face> faces;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZZZZZ")
    private ZonedDateTime beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZZZZZ")
    private ZonedDateTime endTime;

    @NoArgsConstructor
    @Data
    public static class Face{
        private String faceData;

        public Face(String faceData) {
            this.faceData = faceData;
        }
    }

    public AddPerson(String personCode, String personFamilyName, String personGivenName, Integer gender, String orgIndexCode, String phoneNo, String email
            , String faceData, ZonedDateTime beginTime, ZonedDateTime endTime) {
        this.personCode = personCode;
        this.personFamilyName = personFamilyName;
        this.personGivenName = personGivenName;
        this.gender = gender;
        this.orgIndexCode = orgIndexCode;
        this.phoneNo = phoneNo;
        this.email = email;
        this.faces = new ArrayList<>();
        this.faces.add(new Face(faceData));
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}
