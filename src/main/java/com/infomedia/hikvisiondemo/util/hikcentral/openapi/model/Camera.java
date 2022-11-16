package com.infomedia.hikvisiondemo.util.hikcentral.openapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Camera {
    private String cameraIndexCode;
    private String cameraName;
    private String capabilitySet;
    private String encodeDevIndexCode;
    private String recordType;
    private String recordLocation;
    private String regionIndexCode;
    private String siteIndexCode;
    private int status;
}