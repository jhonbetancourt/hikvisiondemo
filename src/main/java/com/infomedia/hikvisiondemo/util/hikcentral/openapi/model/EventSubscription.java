package com.infomedia.hikvisiondemo.util.hikcentral.openapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventSubscription {
    private ArrayList<Detail> detail;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Detail{
        private String eventDest;
        private ArrayList<Integer> eventTypes;
    }
}


