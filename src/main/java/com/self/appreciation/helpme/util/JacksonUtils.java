// JacksonUtils.java
package com.self.appreciation.helpme.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.self.appreciation.helpme.config.YamlObjectMapperConfig;
import com.self.appreciation.helpme.yaml.model.*;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class JacksonUtils {

    @Getter
    private static final ObjectMapper yamlMapper = createConfiguredYamlMapper();

    private static ObjectMapper createConfiguredYamlMapper() {
        return YamlObjectMapperConfig.createYamlObjectMapper();
    }

    // 序列化对象为 YAML 字符串
    public static String toYaml(Object obj) throws Exception {
        return yamlMapper.writeValueAsString(obj);
    }

    // 从 YAML 字符串反序列化对象
    public static <T> T fromYaml(String yaml, Class<T> clazz) throws Exception {
        return yamlMapper.readValue(yaml, clazz);
    }

    // 处理多文档 YAML (--- 分隔的多个资源)
    public java.util.List<Map<String, Object>> parseMultiDocumentYaml(String yamlContent)
            throws Exception {
        return yamlMapper.readValue(
                yamlContent,
                new TypeReference<List<Map<String, Object>>>() {
                }
        );
    }

    public static LinkedHashMap<String, Object> readYamlFromFile(Path path) throws IOException {
        return yamlMapper.readValue(path.toFile(), LinkedHashMap.class);
    }

    /**
     * 从 Path 路径读取 YAML 文件并反序列化为指定类型的对象
     *
     * @param path  YAML 文件路径
     * @param clazz 目标对象类型
     * @param <T>   泛型类型
     * @return 反序列化后的对象
     * @throws IOException 读取文件或反序列化时发生错误
     */
    public static <T> T readYamlFromFile(Path path, Class<T> clazz) throws IOException {
        return yamlMapper.readValue(path.toFile(), clazz);
    }

    /**
     * 从 Path 路径读取 YAML 文件并反序列化为指定类型的对象（支持泛型类型）
     *
     * @param path         YAML 文件路径
     * @param valueTypeRef 类型引用
     * @param <T>          泛型类型
     * @return 反序列化后的对象
     * @throws IOException 读取文件或反序列化时发生错误
     */
    public static <T> T readYamlFromFile(Path path, com.fasterxml.jackson.core.type.TypeReference<T> valueTypeRef) throws IOException {
        return yamlMapper.readValue(path.toFile(), valueTypeRef);
    }

    /**
     * 将对象序列化并写入到指定路径的 YAML 文件
     *
     * @param path 目标文件路径
     * @param obj  要序列化的对象
     * @param <T>  对象类型
     * @throws Exception 写入文件或序列化时发生错误
     */
    public static <T> void writeYamlToFile(Path path, T obj) throws Exception {
        String yamlContent = toYaml(obj);
        Files.writeString(path, yamlContent);
    }

    public static void main(String[] args) throws Exception {
        // 示例：创建一个 PersistentVolumeClaim 并序列化为 YAML
        PersistentVolumeClaim pvc = new PersistentVolumeClaim("test-pvc");
        pvc.setStorageClassName("standard");
        pvc.setAccessModes(java.util.List.of("ReadWriteOnce"));
        pvc.setRequestStorage("10Gi");

        String yamlString = toYaml(pvc);
        System.out.println("Generated YAML:");
        System.out.println(yamlString);

        // 反序列化示例
        String sampleYaml = """
                apiVersion: v1
                kind: PersistentVolumeClaim
                metadata:
                  name: project-mysql-pvc
                spec:
                  accessModes:
                    - ReadWriteOnce
                  resources:
                    requests:
                      storage: 5Gi
                """;

        PersistentVolumeClaim deserializedPvc = fromYaml(sampleYaml, PersistentVolumeClaim.class);
        System.out.println("Deserialized PVC name: " + deserializedPvc.getName());
    }
}
