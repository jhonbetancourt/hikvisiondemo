package com.infomedia.hikvisiondemo.util.hikcentral.openapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {
    private String personId;
    private String personCode;
    private String personName;
    private int gender;
    private String orgIndexCode;
    private PersonPhoto personPhoto;
    private String phoneNo;
    private String email;
    private ZonedDateTime beginTime;
    private ZonedDateTime endTime;
    private String personFamilyName;
    private String personGivenName;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class PersonPhoto{
        private String picUri;
    }
}
