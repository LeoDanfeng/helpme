package com.self.appreciation.helpme.service;

import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class K8sResourceService {

    private final FileStorageService fileStorageService;

    public K8sResourceService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public Mono<Resource> getResourcesForProject(String project, String resourceDeclare) {
        if (Strings.isBlank(resourceDeclare)) {
            return Mono.empty();
        }
        String[] resources = resourceDeclare.split(";");
        Flux<String> combinedFlux = Flux.empty();
        // mysql=A=a,B=b
        for (String resource : resources) {
            if (Strings.isBlank(resourceDeclare)) {
                continue;
            }
            int i = resource.indexOf("=");
            if (i != -1) {
                String resourceName = resource.substring(0, i);
                String resourceFileName = resourceName + "-template.yaml";
                String resourceSetup = resource.substring(i + 1);
                String[] kvPair = resourceSetup.split(",");
                combinedFlux = combinedFlux.concatWith(fileStorageService.readFileContent(resourceFileName)
                        .map(content -> {
                            content = content.replaceAll("PROJECT", project);
                            for (String kv : kvPair) {
                                String[] kvSplit = kv.split("=");
                                content = content.replaceAll(kvSplit[0], kvSplit[1]);
                            }
                            return content;
                        }));
            } else {
                combinedFlux = combinedFlux.concatWith(fileStorageService.readFileContent(resource));
            }
        }
        return fileStorageService.fluxStringToResource(combinedFlux);
    }
}
