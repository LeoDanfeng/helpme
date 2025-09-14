package com.self.appreciation.helpme.util;

import com.self.appreciation.helpme.yaml.SkipNullRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;


public class SnakeYamlUtils {
    public static Yaml createConfiguredYaml() {
        // 配置输出选项
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);

        // 配置 Re-Presenter 以跳过 null 值
        Representer representer = new SkipNullRepresenter();

        return new Yaml(representer, options);
    }

    // 将 Map 转换为格式化的 YAML 字符串
    public static String toYamlString(Object yamlObject) {
        Yaml yaml = createConfiguredYaml();
        return yaml.dump(yamlObject);
    }
}
