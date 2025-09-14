package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnvVarSource {
    // Getters and Setters
    private ObjectFieldSelector fieldRef;
    private ObjectFieldSelector secretKeyRef;
    private ObjectFieldSelector configMapKeyRef;

}
