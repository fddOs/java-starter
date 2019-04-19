package cn.seed.common.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author xianglong.chen
 * @description 分页统一请求对象
 * @time 2019/1/9 15:52
 */
@ApiModel(value = "RequestPage", description = "分页统一请求对象")
public class RequestPage<T> {

    @ApiModelProperty("当前页码")
    @NotNull(message = "pageNum不能为空")
    private Integer pageNum;

    @ApiModelProperty("每页数量")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    @ApiModelProperty("查询条件")
    @NotNull(message = "t不能为空")
    @Valid
    private T t;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
