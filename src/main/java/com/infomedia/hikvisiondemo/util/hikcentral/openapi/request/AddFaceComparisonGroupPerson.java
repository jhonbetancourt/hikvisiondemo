package com.infomedia.hikvisiondemo.util.hikcentral.openapi.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddFaceComparisonGroupPerson {
    private String personIndexCode;
    private String faceGroupIndexCode;
}
