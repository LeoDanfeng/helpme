package com.self.appreciation.helpme.controller;

import org.springframework.web.bind.annotation.RestController;
import com.self.appreciation.helpme.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileShareController {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileShareController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<Map<String, String>>> uploadFile(@RequestPart("file") FilePart filePart) {
        return fileStorageService.storeFile(filePart)
                .map(filename -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("filename", filename);
                    response.put("status", "uploaded");
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", e.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(errorResponse));
                });
    }

    @GetMapping("/download/{filename}")
    public Mono<ResponseEntity<Resource>> downloadFile(@PathVariable String filename) {
        return fileStorageService.loadFileAsResource(filename)
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + filename + "\"")
                        .body(resource))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @GetMapping("/list")
    public Mono<ResponseEntity<Iterable<String>>> listFiles() {
        return fileStorageService.listFiles()
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(500).build());
    }

    @DeleteMapping("/delete/{filename}")
    public Mono<ResponseEntity<Map<String, String>>> deleteFile(@PathVariable String filename) {
        return fileStorageService.deleteFile(filename)
                .map(deleted -> {
                    Map<String, String> response = new HashMap<>();
                    if (deleted) {
                        response.put("filename", filename);
                        response.put("status", "deleted");
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("error", "File not found");
                        return ResponseEntity.status(404).body(response);
                    }
                })
                .onErrorResume(e -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", e.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(errorResponse));
                });
    }
}
