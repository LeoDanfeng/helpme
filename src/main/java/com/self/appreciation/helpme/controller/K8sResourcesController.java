package com.self.appreciation.helpme.controller;

import com.self.appreciation.helpme.service.K8sResourceService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/k8s")
public class K8sResourcesController {

    private final K8sResourceService k8sResourceService;

    public K8sResourcesController(K8sResourceService k8sResourceService) {
        this.k8sResourceService = k8sResourceService;
    }

    @PostMapping("/resources-for-project")
    public Mono<ResponseEntity<Resource>> getResourcesForProject(
            ServerWebExchange exchange) {
        StringBuilder filenameBuilder = new StringBuilder();
        return exchange.getFormData()
                .flatMap(formData -> {
                    Map<String, String> singleValueMap = formData.toSingleValueMap();
                    String project = singleValueMap.get("project");
                    String resourceDeclare = singleValueMap.get("resourceDeclare");
                    filenameBuilder.append(project).append(".yaml");
                    return k8sResourceService.getResourcesForProject(project, resourceDeclare);
                })
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=" + filenameBuilder)
                        .body(resource))
                .onErrorResume(Mono::error);
    }
}
