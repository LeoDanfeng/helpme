package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class EnvVar {
    // Getters and Setters
    private String name;
    private String value;
    private EnvVarSource valueFrom;

    public EnvVar(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public EnvVar(String name, EnvVarSource valueFrom) {
        this.name = name;
        this.valueFrom = valueFrom;
    }

}
