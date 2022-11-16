package com.infomedia.hikvisiondemo.util.hikcentral.openapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FaceData {

    private String indexCode;
    private FaceInfo faceInfo;
    private FacePic facePic;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FacePic{
        private String faceUrl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FaceInfo{
        private String personGivenName;
        private String personFamilyName;
        private String sex;

    }
}

