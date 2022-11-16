package com.infomedia.hikvisiondemo.util.hikcentral.openapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Door {
    private String doorIndexCode;
    private String acsDevIndexCode;
    private String regionIndexCode;
    private String doorNo;
    private String doorName;
    private int doorState;
}