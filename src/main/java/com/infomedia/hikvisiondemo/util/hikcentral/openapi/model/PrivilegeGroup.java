package com.infomedia.hikvisiondemo.util.hikcentral.openapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PrivilegeGroup {
    private String privilegeGroupId;
    private String privilegeGroupName;
    private String description;
}
