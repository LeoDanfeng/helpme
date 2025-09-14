package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class ResourceList {
    // Getters and Setters
    private Map<String, String> resources = new HashMap<>();

    public void put(String key, String value) {
        this.resources.put(key, value);
    }

    public String get(String key) {
        return this.resources.get(key);
    }
}
