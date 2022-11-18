package com.infomedia.hikvisiondemo.dto;

import com.infomedia.hikvisiondemo.util.hikcentral.openapi.model.Organization;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.model.PrivilegeGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class HikcentralDataDto {
    private List<Organization> organizations;
    private List<PrivilegeGroup> privilegeGroups;
}
