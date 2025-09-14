package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class ResourceRequirements {
    // Getters and Setters
    private Map<String, String> requests = new HashMap<>();
    private Map<String, String> limits = new HashMap<>();


    public void putRequest(String key, String value) {
        requests.put(key, value);
    }
    public void putLimit(String key, String value) {
        limits.put(key, value);
    }


}
