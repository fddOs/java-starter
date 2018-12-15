package cn.ehai.common.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:ProjectInfoPropertis
 * @author:方典典
 * @time:2018/12/14 16:08
 */
@ConfigurationProperties(prefix = "project")
public class ProjectInfoProperties {
    private String basePackage;
    private String context;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
