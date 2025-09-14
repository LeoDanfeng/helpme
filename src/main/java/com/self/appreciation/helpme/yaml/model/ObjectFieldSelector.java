package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObjectFieldSelector {
    // Getters and Setters
    private String fieldPath;
    private String name;
    private String key;

}
