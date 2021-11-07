package com.lemonprogis.model;

import lombok.Data;

@Data
public class POI {
    public String id;
    private String title;
    private String lat;
    private String lng;
    private Boolean track;
}
