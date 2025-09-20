package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.Namespace;
import com.self.appreciation.helpme.yaml.template.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Objects;

public class TemplateParser {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TemplateParser.class);

    public static final Path fileStorageLocation;

    static {
        fileStorageLocation = Paths.get("k8s-templates").toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create file storage directory", ex);
        }
    }

    public static String getNamespaceTemplate(String project) throws Exception {
        Namespace namespace = new Namespace(project);
        LinkedHashMap<String, Object> namespaceMap = NamespaceParser.parseTemplate(namespace);
        return JacksonUtils.toYaml(namespaceMap);
    }

    public static String getResourceTemplate(String project, String template, String[] kvPairs) throws Exception {
        if ("mysql".equalsIgnoreCase(template)) {
            MysqlTemplate mysqlTemplate = (MysqlTemplate) parseArgsToTemplate(kvPairs, MysqlTemplate.class);
            return MysqlParser.ParseTemplate(project, mysqlTemplate);
        } else if ("redis".equalsIgnoreCase(template)) {
            RedisTemplate redisTemplate = (RedisTemplate) parseArgsToTemplate(kvPairs, RedisTemplate.class);
            return RedisParser.ParseTemplate(project, redisTemplate);
        } else if ("kafka".equalsIgnoreCase(template)) {
            KafkaTemplate kafkaTemplate = (KafkaTemplate) parseArgsToTemplate(kvPairs, KafkaTemplate.class);
            return KafkaParser.parseTemplate(project, kafkaTemplate);
        } else if ("rabbitmq".equalsIgnoreCase(template)) {
            RabbitMQTemplate rabbitMQTemplate = (RabbitMQTemplate) parseArgsToTemplate(kvPairs, RabbitMQTemplate.class);
            return RabbitMQParser.parseTemplate(project, rabbitMQTemplate);
        } else if ("rocketmq".equalsIgnoreCase(template)) {
            RocketMQTemplate rocketMQTemplate = (RocketMQTemplate) parseArgsToTemplate(kvPairs, RocketMQTemplate.class);
            return RocketMQParser.parseTemplate(project, rocketMQTemplate);
        } else if ("zookeeper".equalsIgnoreCase(template)) {
            ZookeeperTemplate zookeeperTemplate = (ZookeeperTemplate) parseArgsToTemplate(kvPairs, ZookeeperTemplate.class);
            return ZookeeperParser.parseTemplate(project, zookeeperTemplate);
        } else if ("consul".equalsIgnoreCase(template)) {
            ConsulTemplate consulTemplate = (ConsulTemplate) parseArgsToTemplate(kvPairs, ConsulTemplate.class);
            return ConsulParser.parseTemplate(project, consulTemplate);
        } else if ("nacos".equalsIgnoreCase(template)) {
            NacosTemplate nacosTemplate = (NacosTemplate) parseArgsToTemplate(kvPairs, NacosTemplate.class);
            return NacosParser.parseTemplate(project, nacosTemplate);
        } else if ("sentinel".equalsIgnoreCase(template)) {
            SentinelTemplate sentinelTemplate = (SentinelTemplate) parseArgsToTemplate(kvPairs, SentinelTemplate.class);
            return SentinelParser.parseTemplate(project, sentinelTemplate);
        } else if ("seata".equalsIgnoreCase(template)) {
            SeataTemplate seataTemplate = (SeataTemplate) parseArgsToTemplate(kvPairs, SeataTemplate.class);
            return SeataParser.parseTemplate(project, seataTemplate);
        }
        throw new IllegalArgumentException("Unsupported resource type:" + template);
    }

    public static Object parseArgsToTemplate(String[] kvPairs, Class<?> templateClass) {
        Objects.requireNonNull(templateClass);
        Object template;
        ConversionService conversionService = new DefaultConversionService();
        try {
            template = templateClass.getConstructor().newInstance();
            if (kvPairs != null) {
                for (String kv : kvPairs) {
                    if (!StringUtils.hasText(kv)) {
                        continue;
                    }
                    String[] kvPair = kv.split("=");
                    if (kvPair.length != 2 || !StringUtils.hasText(kvPair[0]) || !StringUtils.hasText(kvPair[1])) {
                        continue;
                    }
                    try {
                        java.lang.reflect.Field field = templateClass.getDeclaredField(kvPair[0]);
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        Object convertedValue = conversionService.convert(kvPair[1], fieldType);
                        field.set(template, convertedValue);
                    } catch (NoSuchFieldException e) {
                        throw new IllegalArgumentException("MysqlTemplate has No such field: " + kvPair[0]);
                    } catch (Exception e) {
                        logger.error("Error setting field '{}': {}", kvPair[0], e.getMessage());
                        throw new RuntimeException("Error setting field", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error creating instance of class '{}'", templateClass.getName());
            throw new RuntimeException("Error creating instance of class " + templateClass.getName(), e);
        }
        return template;
    }
}
