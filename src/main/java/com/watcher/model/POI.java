package com.watcher.model;

import lombok.Data;

@Data
public class POI {
    private String title;
    private String address;
    private String zipCode;
    private String city;
    private String state;
    private String lat;
    private String lng;
}
