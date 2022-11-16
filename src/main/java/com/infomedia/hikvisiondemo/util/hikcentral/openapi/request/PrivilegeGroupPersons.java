package com.infomedia.hikvisiondemo.util.hikcentral.openapi.request;


import com.infomedia.hikvisiondemo.util.hikcentral.openapi.model.PersonId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PrivilegeGroupPersons {
    private String privilegeGroupId;
    private int type;
    private List<PersonId> list;
}
