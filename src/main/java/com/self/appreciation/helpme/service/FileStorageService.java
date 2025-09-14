package com.self.appreciation.helpme.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("shared-files").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create file storage directory", ex);
        }
    }

    public Mono<String> storeFile(FilePart filePart, String... subPaths) {
        String filename = filePart.filename();
        Path targetLocation = findPath(subPaths).resolve(filename);
        return filePart.transferTo(targetLocation)
                .then(Mono.just(filename));
    }

    public Mono<Resource> loadFileAsResource(String filename, String... subPaths) {
        try {
            Path filePath = findPath(subPaths);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return Mono.just(resource);
            } else {
                return Mono.error(new RuntimeException("File not found: " + filename));
            }
        } catch (Exception ex) {
            return Mono.error(new RuntimeException("File not found: " + filename, ex));
        }
    }

    public Mono<Iterable<String>> listFiles() {
        return Mono.fromCallable(() -> {
            try (Stream<Path> pathStream = Files.list(this.fileStorageLocation)) {
                Iterable<String> filenames = pathStream
                        .map(path -> path.getFileName().toString())
                        .toList();
                return filenames;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Boolean> deleteFile(String filename, String... subPaths) {
        return Mono.fromCallable(() -> {
            try {
                Path filePath = findPath(subPaths);
                return Files.deleteIfExists(filePath);
            } catch (IOException e) {
                return false;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 读取文件内容为字符串（推荐用于文本文件）
    public Mono<String> readFileContent(String filename, String... subPaths) {
        return Mono.fromCallable(() -> {
            try {
                Path filePath = findPath(subPaths);
                return Files.readString(filePath, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Error reading file: " + filename, e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 使用DataBuffer方式读取文件（适用于WebFlux流式处理）
    public Mono<String> readFileContentReactive(String filename, String... subPaths) {
        try {
            Path filePath = findPath(subPaths).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024)
                        .map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            return new String(bytes, StandardCharsets.UTF_8);
                        })
                        .reduce(String::concat);
            } else {
                return Mono.error(new RuntimeException("File not found: " + filename));
            }
        } catch (Exception ex) {
            return Mono.error(new RuntimeException("Error reading file: " + filename, ex));
        }
    }

    // 流式读取文件行
    public Flux<String> readFileLines(String filename, String... subPaths) {
        try {
            Path filePath = findPath(subPaths).resolve(filename).normalize();
            return Flux.using(
                    () -> Files.lines(filePath, StandardCharsets.UTF_8),
                    Flux::fromStream,
                    Stream::close
            ).subscribeOn(Schedulers.boundedElastic());
        } catch (Exception ex) {
            return Flux.error(new RuntimeException("Error reading file: " + filename, ex));
        }
    }

    public Mono<Resource> fluxStringToResource(Flux<String> contentFlux) {
        return contentFlux
                .collect(StringBuilder::new, StringBuilder::append)
                .map(StringBuilder::toString)
                .map(content -> {
                    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                    return new ByteArrayResource(bytes);
                });
    }

    public Mono<Void> appendFile(String filename, String content, String... subPaths) {
        return Mono.fromCallable(() -> {
            try {
                Path filePath = findPath(subPaths).resolve(filename).normalize();
                Files.writeString(filePath, content, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                return null;
            } catch (IOException e) {
                throw new RuntimeException("Error appending to file: " + filename, e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }


    private Path findPath(String... subPaths) {
        Path path = this.fileStorageLocation;
        for (String subPath : subPaths) {
            path = path.resolve(subPath);
        }
        return path;
    }
}
