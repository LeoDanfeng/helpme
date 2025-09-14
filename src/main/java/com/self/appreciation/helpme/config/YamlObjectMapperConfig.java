package com.self.appreciation.helpme.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class YamlObjectMapperConfig {

    public static ObjectMapper createYamlObjectMapper() {
        // 创建 YAML 工厂
        YAMLFactory yamlFactory = new YAMLFactory();

        // 启用字面量块样式和文档开始标记
        yamlFactory.enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);
        yamlFactory.enable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);

        // 创建 ObjectMapper
        ObjectMapper mapper = new ObjectMapper(yamlFactory);

        // 配置 ObjectMapper
        configureObjectMapper(mapper);

        return mapper;
    }

    public static void configureObjectMapper(ObjectMapper mapper) {
        // 设置属性命名策略，将驼峰命名转换为短横线分隔 (camelCase -> camel-case)
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

        // 只序列化非空值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 反序列化时忽略未知属性
        mapper.configure(
            com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false
        );

        // 允许字段名没有引号
        mapper.configure(
            com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
            false
        );
    }
}
