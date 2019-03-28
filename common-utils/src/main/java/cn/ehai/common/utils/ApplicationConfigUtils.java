package cn.ehai.common.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:ApplicationConfigUtils
 * @author:方典典
 * @time:2019/3/26 16:02
 */
public class ApplicationConfigUtils {
    private static final ResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();

    public static Object getEnvConfig(String key) {
        Resource[] resources = new Resource[]{};
        String[] keys = key.split("\\.");
        Object value = null;
        try {
            resources = RESOLVER.getResources("classpath*:application.yml");
        } catch (IOException e) {
            LoggerUtils.error(ApplicationConfigUtils.class, ExceptionUtils.getStackTrace(e));
        }
        for (Resource resource : resources) {
            Map<String, Object> map = getConfigMapByResource(resource);
            for (int i = 0; i < keys.length - 1; i++) {
                if (map == null) {
                    break;
                }
                map = (Map<String, Object>) map.get(keys[i]);
            }
            if (map == null) {
                continue;
            }
            value = map.get(keys[keys.length - 1]);
            if (value == null) {
                continue;
            }
            String protocol;
            try {
                protocol = resource.getURL().getProtocol();
            } catch (IOException e) {
                LoggerUtils.error(ApplicationConfigUtils.class, ExceptionUtils.getStackTrace(e));
                protocol = null;
            }
            if ("file".equalsIgnoreCase(protocol)) {
                return value;
            }
        }
        return value;
    }

    private static Map<String, Object> getConfigMapByResource(Resource resource) {
        Yaml yaml = new Yaml();
        Map<String, Object> result = new HashMap<>();
        try (InputStream inputStream = resource.getInputStream()) {
            result = (Map<String, Object>) yaml.load(inputStream);
        } catch (Exception e) {
            LoggerUtils.error(ApplicationConfigUtils.class, ExceptionUtils.getStackTrace(e));
        }
        return result;
    }
}
