package com.self.appreciation.helpme.controller;

import com.self.appreciation.helpme.service.FileEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/edit")
public class FileEditController {
    private final FileEditService fileEditService;

    @Autowired
    public FileEditController(FileEditService fileEditService) {
        this.fileEditService = fileEditService;
    }

    @PostMapping("/file")
    public Mono<ResponseEntity<Resource>> editFile(
            @RequestPart("filePart") FilePart filePart,
            @RequestPart(value = "args", required = false) String args) {

        System.out.println("Received args: " + args); // 调试输出

        String filename = filePart.filename();
        return fileEditService.editFile(filePart, args != null ? args : "")
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + filename + "\"")
                        .body(resource))
                .onErrorResume(Mono::error);
    }


}
