package com.self.appreciation.helpme.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileEditService {
    public Mono<Resource> editFile(FilePart filePart, String args) {
        HashMap<String, String> map = new HashMap<>();
        if (args != null) {
            String[] argsArray = args.split("\\s");
            if (argsArray.length % 2 != 0) {
                return Mono.error(new IllegalArgumentException("Invalid arguments, must be pairs."));
            }
            for (int i = 0; i < argsArray.length; i += 2) {
                map.put(argsArray[i], argsArray[i + 1]);
            }
        }
        return filePart.content()
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return Mono.just(new String(bytes, StandardCharsets.UTF_8));
                })
                .reduce(new StringBuilder(), StringBuilder::append)
                .map(StringBuilder::toString)
                .map((content) -> {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        content = content.replaceAll(entry.getKey(), entry.getValue());
                    }
                    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                    return new ByteArrayResource(bytes);
                });
    }
}
