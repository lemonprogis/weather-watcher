package com.watcher.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("poi")
public class POI {
    @Id
    public String id;
    private String title;
    private String lat;
    private String lng;
    private Boolean track = true;
}
