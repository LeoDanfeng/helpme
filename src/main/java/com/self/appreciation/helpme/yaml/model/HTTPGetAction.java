package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HTTPGetAction {
    // Getters and Setters
    private String path;
    private String port;
    private String host;
    private String scheme;
    private List<HTTPHeader> httpHeaders;

}
