package com.self.appreciation.helpme.service;

import com.self.appreciation.helpme.yaml.parser.TemplateParser;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class K8sResourceService {

    private final FileStorageService fileStorageService;

    public K8sResourceService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public Mono<Resource> getResourcesForProject(String project, String resourceDeclare) {
        // 参数验证
        if (Strings.isBlank(resourceDeclare)) {
            return Mono.empty();
        }
        if (Strings.isBlank(project)) {
            return Mono.error(new IllegalArgumentException("An non-empty project name is required."));
        }

        // 响应式地检查项目唯一性并添加
//        Mono<Void> projectCheckAndAdd = fileStorageService.readFileLines("project.txt", "created-projects")
//                .filter(project::equals)
//                .hasElements()
//                .flatMap(exists -> {
//                    if (exists) {
//                        return Mono.error(new RuntimeException("The project name have been existed and it must be unique."));
//                    } else {
//                        String appendContent = project + "\n";
//                        return fileStorageService.appendFile("project.txt", appendContent,"zz");
//                    }
//                });

        // 处理资源文件
        String[] resources = resourceDeclare.split(";");
        Flux<String> combinedFlux = Flux.empty();
        combinedFlux = combinedFlux.concatWith(
                Mono.fromCallable(() -> TemplateParser.getNamespaceTemplate(project))
        );
        // mysql=A=a,B=b
        for (String resource : resources) {
            if (Strings.isBlank(resource)) {
                continue;
            }
            int i = resource.indexOf("=");
            if (i != -1) {
                String resourceName = resource.substring(0, i);
                String[] kvPair;
                String resourceSetup = resource.substring(i + 1);
                if (Strings.isBlank(resourceSetup)) {
                    kvPair = null;
                } else {
                    kvPair = resourceSetup.split(",");
                }

                combinedFlux = combinedFlux.concatWith(
                        Mono.fromCallable(() -> TemplateParser.getResourceTemplate(project, resourceName, kvPair))
                );
            } else {
                combinedFlux = combinedFlux.concatWith(
                        Mono.fromCallable(() -> TemplateParser.getResourceTemplate(project, resource, null))
                );
            }
        }

        // 将项目检查和资源处理组合
        return
//                projectCheckAndAdd
//                .then()
                fileStorageService.fluxStringToResource(combinedFlux)
                        .onErrorResume(e -> Mono.error(new RuntimeException("Failed to generate resources", e)));
    }
}
