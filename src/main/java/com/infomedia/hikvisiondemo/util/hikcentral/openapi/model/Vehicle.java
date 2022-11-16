package com.infomedia.hikvisiondemo.util.hikcentral.openapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vehicle {
    private String vehicleId;
    private String plateNo;
    private String personName;
    private String personFamilyName;
    private String personGivenName;
    private String phoneNo;
    private int vehicleColor;
    private String vehicleGroupIndexCode;
}
