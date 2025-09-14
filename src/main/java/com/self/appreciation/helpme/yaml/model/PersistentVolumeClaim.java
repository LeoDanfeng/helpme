package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class PersistentVolumeClaim {
    private String name;                           // metadata.name
    private String namespace;                      // metadata.namespace
    private Map<String, String> labels;            // metadata.labels
    private Map<String, String> annotations;       // metadata.annotations

    // PersistentVolumeClaimSpec
    private List<String> accessModes = List.of("ReadWriteOnce");              // spec.accessModes (ReadWriteOnce, ReadOnlyMany, ReadWriteMany)
    private String storageClassName;               // spec.storageClassName
    private Map<String, String> resources;         // spec.resources (requests and limits)
    private String requestStorage = "256Mi";                 // spec.resources.requests.storage
    private String limitStorage = "256Mi";                   // spec.resources.limits.storage
    private String volumeName;                     // spec.volumeName
    private Map<String, String> selector;          // spec.selector

    // PersistentVolumeClaimStatus (可选)
    private String phase;                          // status.phase (Pending, Bound, Lost)
    private Map<String, String> capacity;          // status.capacity

    public PersistentVolumeClaim(String name) {
        this.name = name;
    }
}
