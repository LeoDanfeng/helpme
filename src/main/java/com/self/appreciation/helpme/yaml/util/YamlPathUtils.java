package com.self.appreciation.helpme.yaml.util;

import java.util.*;

@SuppressWarnings("unchecked")
public class YamlPathUtils {

    /**
     * 通过路径获取YAML中的值
     *
     * @param yamlMap YAML Map对象
     * @param path    点分隔的路径，如 "spec.template.spec.containers"
     * @return 对应的值
     */
    public static Object getValueByPath(Map<String, Object> yamlMap, String path) {
        String[] parts = path.split("\\.");
        Object current = yamlMap;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return null;
            }
        }

        return current;
    }

    /**
     * 通过路径设置YAML中的值
     *
     * @param yamlMap YAML Map对象
     * @param path    点分隔的路径
     * @param value   要设置的值
     */
    public static void setValueByPath(Map<String, Object> yamlMap, String path, Object value) {

        if (value == null) return;

        if (value instanceof Collection<?> col && col.isEmpty()) return;

        if (value instanceof Map<?, ?> map && map.isEmpty()) return;

        if (value instanceof String str && str.isEmpty()) return;

        String[] parts = path.split("\\.");
        Map<String, Object> current = yamlMap;

        // 导航到目标位置的父级
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object next = current.get(part);

            if (next == null) {
                // 如果路径不存在，创建新的Map
                next = new LinkedHashMap<String, Object>();
                current.put(part, next);
            }

            if (!(next instanceof Map)) {
                throw new IllegalArgumentException("Path conflict at: " + part);
            }

            current = (Map<String, Object>) next;
        }

        // 设置最终值
        current.put(parts[parts.length - 1], value);
    }

    /**
     * 确保路径存在，如果不存在则创建
     *
     * @param yamlMap YAML Map对象
     * @param path    点分隔的路径
     */
    public static void ensurePathExists(Map<String, Object> yamlMap, String path) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = yamlMap;

        for (String part : parts) {
            Object next = current.get(part);

            if (next == null) {
                next = new LinkedHashMap<String, Object>();
                current.put(part, next);
            }

            if (!(next instanceof Map)) {
                throw new IllegalArgumentException("Path conflict at: " + part);
            }

            current = (Map<String, Object>) next;
        }
    }

    /**
     * 批量设置多个路径的值
     *
     * @param yamlMap        YAML Map对象
     * @param pathValuePairs 路径和值的映射
     */
    public static void setValuesByPaths(Map<String, Object> yamlMap, Map<String, Object> pathValuePairs) {
        for (Map.Entry<String, Object> entry : pathValuePairs.entrySet()) {
            setValueByPath(yamlMap, entry.getKey(), entry.getValue());
        }
    }
}
