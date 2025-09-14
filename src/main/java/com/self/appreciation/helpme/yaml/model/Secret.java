package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class Secret {
    // ObjectMeta
    private String name;                     // metadata.name
    private String namespace;                      // metadata.namespace
    private Map<String, String> labels;            // metadata.labels
    private Map<String, String> annotations;       // metadata.annotations

    // SecretSpec
    private Map<String, String> data;              // data (base64 encoded)
    private Map<String, String> stringData;        // stringData (not base64 encoded)
    private String type = "Opaque";                // type (Opaque, kubernetes.io/service-account-token,
                                                   // kubernetes.io/dockercfg, kubernetes.io/dockerconfigjson,
                                                   // kubernetes.io/basic-auth, kubernetes.io/ssh-auth,
                                                   // kubernetes.io/tls, bootstrap.kubernetes.io/token)

    public Secret(String name) {
        this.name = name;
    }

}
