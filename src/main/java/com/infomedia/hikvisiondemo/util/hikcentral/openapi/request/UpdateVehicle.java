package com.infomedia.hikvisiondemo.util.hikcentral.openapi.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateVehicle {
    private String vehicleId;
    private String plateNo;
    private Integer vehicleColor;
    private String vehicleGroupIndexCode;
}
