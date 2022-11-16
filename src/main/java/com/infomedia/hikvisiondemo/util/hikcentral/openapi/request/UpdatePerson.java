package com.infomedia.hikvisiondemo.util.hikcentral.openapi.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePerson {
    private String personId;
    private String personFamilyName;
    private String personGivenName;
    private Integer gender;
    private String orgIndexCode;
    private String phoneNo;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZZZZZ")
    private ZonedDateTime beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZZZZZ")
    private ZonedDateTime endTime;
}
