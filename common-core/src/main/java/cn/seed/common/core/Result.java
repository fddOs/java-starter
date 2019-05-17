package cn.seed.common.core;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author xianglong.chen
 * @description 统一API响应结果封装
 * @time 2019/1/9 15:52
 */
@ApiModel(value = "Result", description = "统一API响应结果封装")
public class Result<T> {

    @ApiModelProperty("错误码：[0:成功],[400:失败]，其它错误码请询问后端开发人员")
    private int errorCode;
    @ApiModelProperty("提示信息")
    private String message;
    @ApiModelProperty("返回数据")
    private T result;

    public Result() {

    }

    public Result<T> setErrorCode(ResultCode resultCode) {
        this.errorCode = resultCode.getCode();
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public Result<T> setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}