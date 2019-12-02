package cn.seed.authority.dto;

import java.io.Serializable;

/**
 * 权限系统模块信息封装类
 *
 * @author 方典典
 * @time 2019/11/29 17:01
 */
public class ModuleInfoDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String levelId;
    private String moduleId;
    private String moduleInfo;
    private String moduleMethod;
    private String moduleName;
    private String moduleOrderBy;
    private String moduleSelectValue;
    private String moduleShow;
    private String moduleUrl;
    private String parentId;
    private String systemCode;

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(String moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    public String getModuleMethod() {
        return moduleMethod;
    }

    public void setModuleMethod(String moduleMethod) {
        this.moduleMethod = moduleMethod;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleOrderBy() {
        return moduleOrderBy;
    }

    public void setModuleOrderBy(String moduleOrderBy) {
        this.moduleOrderBy = moduleOrderBy;
    }

    public String getModuleSelectValue() {
        return moduleSelectValue;
    }

    public void setModuleSelectValue(String moduleSelectValue) {
        this.moduleSelectValue = moduleSelectValue;
    }

    public String getModuleShow() {
        return moduleShow;
    }

    public void setModuleShow(String moduleShow) {
        this.moduleShow = moduleShow;
    }

    public String getModuleUrl() {
        return moduleUrl;
    }

    public void setModuleUrl(String moduleUrl) {
        this.moduleUrl = moduleUrl;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @Override
    public String toString() {
        return "ModuleInfoDTO{" +
                "levelId='" + levelId + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", moduleInfo='" + moduleInfo + '\'' +
                ", moduleMethod='" + moduleMethod + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", moduleOrderBy='" + moduleOrderBy + '\'' +
                ", moduleSelectValue='" + moduleSelectValue + '\'' +
                ", moduleShow='" + moduleShow + '\'' +
                ", moduleUrl='" + moduleUrl + '\'' +
                ", parentId='" + parentId + '\'' +
                ", systemCode='" + systemCode + '\'' +
                '}';
    }
}
