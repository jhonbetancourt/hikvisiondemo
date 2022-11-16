package com.infomedia.hikvisiondemo.util.hikcentral.openapi.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventUnsubscriptionRequest {
    @NotNull
    @Size(min = 1)
    private List<Integer> eventTypes;
}
