package cn.seed.web.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.seed.common.core.Result;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ResultGenerator;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.LoggerUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author 刘旭
 * @description:统一异常处理
 * @time 2018年1月9日 下午5:46:41
 */
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    private <T> Result<T> serviceExceptionHandler(HttpServletRequest request, ServiceException e) {
        LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e),e.getCause());
        return ResultGenerator.genFailResult(e.getResultCode(), e.getMessage());
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    private <T> Result<T> bindExceptionHandler(HttpServletRequest request, Exception e) {
        List<FieldError> fieldErrors;
        try {
            fieldErrors = ((BindException) e).getBindingResult().getFieldErrors();
        } catch (Exception e2) {
            fieldErrors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
        }
        LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
        return ResultGenerator.genFailResult(ResultCode.BAD_REQUEST, fieldErrors.get(0).getDefaultMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    private <T> Result<T> noHandlerFoundExceptionHandler(HttpServletRequest request, NoHandlerFoundException e) {
        LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
        return ResultGenerator.genFailResult(ResultCode.NOT_FOUND, e.getMessage());
    }

}